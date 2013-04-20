/*
 * Copyright 2002-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package de.davidbilge.spring.remoting.amqp.client;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.remoting.support.RemoteAccessor;

import de.davidbilge.spring.remoting.amqp.common.CanonicalNameQueueNameStrategy;
import de.davidbilge.spring.remoting.amqp.common.Constants;
import de.davidbilge.spring.remoting.amqp.common.MethodHeaderNamingStrategy;
import de.davidbilge.spring.remoting.amqp.common.QueueNameStrategy;
import de.davidbilge.spring.remoting.amqp.common.SimpleHeaderNamingStrategy;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * {@link org.aopalliance.intercept.MethodInterceptor} for accessing RMI-style AMQP services.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * @see AmqpServiceExporter
 * @see AmqpProxyFactoryBean
 * @see org.springframework.remoting.RemoteAccessException
 */
public class AmqpClientInterceptor extends RemoteAccessor implements MethodInterceptor {

	private AmqpTemplate amqpTemplate;

	private MethodHeaderNamingStrategy methodHeaderNamingStrategy = new SimpleHeaderNamingStrategy();

	private QueueNameStrategy queueNameStrategy = new CanonicalNameQueueNameStrategy();

	private MessageConverter messageConverter = new SimpleMessageConverter();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader(Constants.INVOKED_METHOD_HEADER_NAME,
				methodHeaderNamingStrategy.generateMethodName(invocation.getMethod()));

		Message m = getMessageConverter().toMessage(invocation.getArguments(), messageProperties);

		Message resultMessage = amqpTemplate.sendAndReceive(queueNameStrategy.getQueueName(getServiceInterface()), m);

		Object result = getMessageConverter().fromMessage(resultMessage);

		if (invocation.getMethod().getReturnType().getCanonicalName().equals(Void.class.getCanonicalName())) {
			return null;
		} else if (result instanceof Throwable
				&& !invocation.getMethod().getReturnType().isAssignableFrom(result.getClass())) {
			// TODO handle for case where exceptions that are not known to the
			// caller are being thrown (might be nested unchecked exceptions)
			throw (Throwable) result;
		} else {
			return result;
		}
	}

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	/**
	 * The AMQP template to be used for sending messages and receiving results. This class is using "Request/Reply" for
	 * sending messages as described <a href=
	 * "http://static.springsource.org/spring-amqp/reference/html/amqp.html#request-reply" >in the Spring-AMQP
	 * documentation</a>.
	 */
	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public QueueNameStrategy getQueueNameStrategy() {
		return queueNameStrategy;
	}

	/**
	 * Determines how the queue for the proxied service is named. Make sure that this matches the strategy in the
	 * respective {@link AmqpServiceExporter} on the service side. Defaults to {@link CanonicalNameQueueNameStrategy}.
	 */
	public void setQueueNameStrategy(QueueNameStrategy queueNameStrategy) {
		this.queueNameStrategy = queueNameStrategy;
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	/**
	 * Set the message converter for this remote service. Used to serialize arguments to called methods and to
	 * deserialize their return values.
	 * <p>
	 * The default converter is a SimpleMessageConverter, which is able to handle byte arrays, Strings, and Serializable
	 * Objects depending on the message content type header.
	 * 
	 * @see org.springframework.amqp.support.converter.SimpleMessageConverter
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public MethodHeaderNamingStrategy getMethodHeaderNamingStrategy() {
		return methodHeaderNamingStrategy;
	}

	/**
	 * A strategy to name methods in the message header for lookup in the service. Make sure to use the same strategy on
	 * the service side.
	 */
	public void setMethodHeaderNamingStrategy(MethodHeaderNamingStrategy methodHeaderNamingStrategy) {
		this.methodHeaderNamingStrategy = methodHeaderNamingStrategy;
	}

}
