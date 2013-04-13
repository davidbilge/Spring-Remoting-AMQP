/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.davidbilge.spring.remoting.amqp.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

import de.davidbilge.spring.remoting.amqp.common.Constants;
import de.davidbilge.spring.remoting.amqp.common.JosSerializer;
import de.davidbilge.spring.remoting.amqp.common.MethodSerializer;
import de.davidbilge.spring.remoting.amqp.common.Serializer;
import de.davidbilge.spring.remoting.amqp.common.SimpleMethodSerializer;

/**
 * A convenient superclass for AMQP service exporters, providing an
 * implementation of the {@link MessageListener} interface.
 * 
 * <p>
 * When receiving a message, a service method is called - which is determined by
 * the {@link Constants#INVOKED_METHOD_HEADER_NAME} header. An exception thrown
 * by the invoked method is serialized and returned to the client as is a
 * regular method return value.
 * 
 * <p>
 * This listener responds to "Request/Reply"-style messages as described <a
 * href=
 * "http://static.springsource.org/spring-amqp/reference/html/amqp.html#request-reply"
 * >here</a>.
 * 
 * @author David Bilge
 * @since 13.04.2013
 */
public class AmqpMessageListener extends RemoteExporter implements MessageListener, InitializingBean {

	private AmqpTemplate amqpTemplate;

	private MethodSerializer methodSerializer = new SimpleMethodSerializer();

	private Map<String, Method> methodCache = new HashMap<String, Method>();

	private Serializer serializer = new JosSerializer();

	@Override
	public void afterPropertiesSet() throws Exception {
		Method[] methods = getService().getClass().getMethods();
		for (Method method : methods) {
			methodCache.put(methodSerializer.serialize(method), method);
		}
	}

	@Override
	public void onMessage(Message message) {
		Address replyToAddress = message.getMessageProperties().getReplyToAddress();

		Map<String, Object> headers = message.getMessageProperties().getHeaders();
		Object invokedMethodRaw = headers.get(Constants.INVOKED_METHOD_HEADER_NAME);
		if (invokedMethodRaw == null || !(invokedMethodRaw instanceof String)) {
			send(new RuntimeException("The 'invoked method' header is missing (expected name '"
					+ Constants.INVOKED_METHOD_HEADER_NAME + "')"), replyToAddress);
			return;
		}

		String invokedMethodName = (String) invokedMethodRaw;
		Method invokedMethod = methodCache.get(invokedMethodName);
		if (invokedMethod == null) {
			send(new RuntimeException("The invoked method does not exist on the service (Method: '" + invokedMethodName
					+ "')"), replyToAddress);
			return;
		}

		Object retVal;
		try {
			retVal = invokedMethod.invoke(getService(), serializer.deserialize(message.getBody()));
		} catch (InvocationTargetException ite) {
			send(ite.getCause(), replyToAddress);
			return;
		} catch (Throwable e) {
			send(e, replyToAddress);
			return;
		}

		send(retVal, replyToAddress);
	}

	private void send(Object o, Address replyToAddress) {
		byte[] serializedReturnValue;
		try {
			serializedReturnValue = serializer.serialize(o);
		} catch (IOException e) {
			// This really should not happen
			return;
		}

		Message m = new Message(serializedReturnValue, new MessageProperties());

		getAmqpTemplate().send(replyToAddress.getExchangeName(), replyToAddress.getRoutingKey(), m);
	}

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	/**
	 * The AMQP template to use for sending the return value.
	 */
	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public MethodSerializer getMethodSerializer() {
		return methodSerializer;
	}

	/**
	 * A strategy to name methods in the message header for lookup in the
	 * service. Make sure to use the same serializer on the client side.
	 */
	public void setMethodSerializer(MethodSerializer methodSerializer) {
		this.methodSerializer = methodSerializer;
	}

}
