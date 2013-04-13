package de.davidbilge.spring.remoting.amqp.common;

/**
 * Selects the queue name according to the canonical class name of the service
 * interface.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * 
 */
public class CanonicalNameQueueNameStrategy implements QueueNameStrategy {

	@Override
	public String getQueueName(Class<?> serviceInterface) {
		return serviceInterface.getCanonicalName();
	}

}
