package de.davidbilge.spring.remoting.amqp.common;

import java.io.IOException;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * A facility to serialize and deserialize arguments and return values for an
 * AMQP method call. Used in both the {@link AmqpClientInterceptor} and the
 * {@link AmqpServiceExporter} and has to match in a pair of those.
 * 
 * @author David
 * @since 13.04.2013
 */
public interface Serializer {

	byte[] serialize(Object... arguments) throws IOException;

	Object[] deserialize(byte[] serializedArgs) throws IOException, ClassNotFoundException;

}
