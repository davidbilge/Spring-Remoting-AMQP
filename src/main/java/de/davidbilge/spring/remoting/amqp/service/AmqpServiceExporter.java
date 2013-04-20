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

package de.davidbilge.spring.remoting.amqp.service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.serializer.Serializer;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.client.AmqpProxyFactoryBean;
import de.davidbilge.spring.remoting.amqp.common.CanonicalNameQueueNameStrategy;
import de.davidbilge.spring.remoting.amqp.common.QueueNameStrategy;

/**
 * AmqpServiceExporter that exposes the specified service as an AMQP queue. Such services can be accessed via plain AMQP
 * or via {@link AmqpProxyFactoryBean} (depending on the utilized {@link Serializer}).
 * 
 * @author David Bilge
 * @since 13.04.2013
 * @see AmqpClientInterceptor
 * @see AmqpProxyFactoryBean
 */
public class AmqpServiceExporter extends AmqpMessageListener implements DisposableBean {

	private ConnectionFactory connectionFactory;

	private QueueNameStrategy queueNameStrategy = new CanonicalNameQueueNameStrategy();

	private SimpleMessageListenerContainer listenerContainer;

	private int concurrentConsumers = 1;

	private RabbitAdmin rabbitAdmin;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Queue q = new Queue(getQueueNameStrategy().getQueueName(getServiceInterface()));
		getRabbitAdmin().declareQueue(q);

		listenerContainer = new SimpleMessageListenerContainer(connectionFactory);
		listenerContainer.setQueueNames(getQueueNameStrategy().getQueueName(getServiceInterface()));

		listenerContainer.setMessageListener(this);

		listenerContainer.setConcurrentConsumers(getConcurrentConsumers());

		listenerContainer.initialize();
		listenerContainer.start();
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * The connection factory that is being used to listen to incoming messages on the service queue.
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public QueueNameStrategy getQueueNameStrategy() {
		return queueNameStrategy;
	}

	/**
	 * Determines how the queue for the exposed service is named. Make sure that this matches the strategy in the
	 * respective {@link AmqpClientInterceptor} on the client side. Defaults to {@link CanonicalNameQueueNameStrategy}.
	 */
	public void setQueueNameStrategy(QueueNameStrategy queueNameStrategy) {
		this.queueNameStrategy = queueNameStrategy;
	}

	@Override
	public void destroy() throws Exception {
		listenerContainer.stop();
	}

	public int getConcurrentConsumers() {
		return concurrentConsumers;
	}

	/**
	 * The number of concurrent consumers to use when receiving service calls. Defaults to 1.
	 * 
	 * @see SimpleMessageListenerContainer#setConcurrentConsumers(int)
	 */
	public void setConcurrentConsumers(int concurrentConsumers) {
		this.concurrentConsumers = concurrentConsumers;
	}

	public RabbitAdmin getRabbitAdmin() {
		return rabbitAdmin;
	}

	/**
	 * The RabbitAdmin that is used for creating the service queue on the message broker
	 */
	public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
		this.rabbitAdmin = rabbitAdmin;
	}

}
