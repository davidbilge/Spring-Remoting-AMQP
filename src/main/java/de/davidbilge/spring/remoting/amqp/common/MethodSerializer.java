package de.davidbilge.spring.remoting.amqp.common;

import java.lang.reflect.Method;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * A strategy to create a unique method identifier by which client and service
 * can determine which method is to be called. Used in both the
 * {@link AmqpClientInterceptor} and the {@link AmqpServiceExporter} and has to
 * match in a pair of those.
 * 
 * <p>
 * The method identifier is put in the message header and passed along with the
 * serialized arguments.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * 
 */
public interface MethodSerializer {
	String serialize(Method method);
}
