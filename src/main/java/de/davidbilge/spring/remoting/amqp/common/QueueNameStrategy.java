package de.davidbilge.spring.remoting.amqp.common;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * A strategy for naming an AMQP queue. Is used in both
 * {@link AmqpClientInterceptor} and {@link AmqpServiceExporter} and has to
 * match for a pair of them.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * 
 */
public interface QueueNameStrategy {
	public String getQueueName(Class<?> serviceInterface);
}
