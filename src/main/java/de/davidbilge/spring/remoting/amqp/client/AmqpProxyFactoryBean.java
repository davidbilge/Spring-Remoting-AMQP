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

package de.davidbilge.spring.remoting.amqp.client;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * {@link FactoryBean} for AMQP proxies. Exposes the proxied service for use as
 * a bean reference, using the specified service interface. Proxies will throw
 * Spring's unchecked RemoteAccessException on remote invocation failure.
 * 
 * <p>
 * This is intended for an "RMI-style" (i.e. synchroneous) usage of the AMQP
 * protocol. Obviously, AMQP allows for a much broader scope of execution
 * styles, which are not the scope of the mechanism at hand.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * @see #setServiceInterface
 * @see AmqpClientInterceptor
 * @see RmiServiceExporter
 * @see org.springframework.remoting.RemoteAccessException
 */
public class AmqpProxyFactoryBean extends AmqpClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware,
		InitializingBean {

	private Object serviceProxy;

	private RabbitAdmin rabbitAdmin;

	@Override
	public void afterPropertiesSet() {
		if (getServiceInterface() == null) {
			throw new IllegalArgumentException("Property 'serviceInterface' is required");
		}
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());

		Queue q = new Queue(getQueueNameStrategy().getQueueName(getServiceInterface()));
		getRabbitAdmin().declareQueue(q);
	}

	@Override
	public Object getObject() throws Exception {
		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public RabbitAdmin getRabbitAdmin() {
		return rabbitAdmin;
	}

	/**
	 * The RabbitAdmin that is used for creating the service queue on the
	 * message broker
	 */
	public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
		this.rabbitAdmin = rabbitAdmin;
	}

}
