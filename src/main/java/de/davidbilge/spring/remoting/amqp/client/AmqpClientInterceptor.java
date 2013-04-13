package de.davidbilge.spring.remoting.amqp.client;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.support.RemoteAccessor;

import de.davidbilge.spring.remoting.amqp.common.CanonicalNameQueueNameStrategy;
import de.davidbilge.spring.remoting.amqp.common.Constants;
import de.davidbilge.spring.remoting.amqp.common.JosSerializer;
import de.davidbilge.spring.remoting.amqp.common.MethodSerializer;
import de.davidbilge.spring.remoting.amqp.common.QueueNameStrategy;
import de.davidbilge.spring.remoting.amqp.common.Serializer;
import de.davidbilge.spring.remoting.amqp.common.SimpleMethodSerializer;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * {@link org.aopalliance.intercept.MethodInterceptor} for accessing RMI-style
 * AMQP services.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * @see AmqpServiceExporter
 * @see AmqpProxyFactoryBean
 * @see org.springframework.remoting.RemoteAccessException
 */
public class AmqpClientInterceptor extends RemoteAccessor implements MethodInterceptor {

	private AmqpTemplate amqpTemplate;

	private MethodSerializer methodSerializer = new SimpleMethodSerializer();

	private QueueNameStrategy queueNameStrategy = new CanonicalNameQueueNameStrategy();

	private Serializer serializer = new JosSerializer();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader(Constants.INVOKED_METHOD_HEADER_NAME,
				getMethodSerializer().serialize(invocation.getMethod()));
		Message m = new Message(getSerializer().serialize(invocation.getArguments()), messageProperties);

		Message resultMessage = amqpTemplate.sendAndReceive(queueNameStrategy.getQueueName(getServiceInterface()), m);

		Object[] resultArray = serializer.deserialize(resultMessage.getBody());
		Object result;
		if (resultArray == null || resultArray.length == 0) {
			result = null;
		} else {
			result = resultArray[0];
		}

		if (invocation.getMethod().getReturnType().getCanonicalName().equals(Void.class.getCanonicalName())) {
			return null;
		} else if (result instanceof Throwable
				&& !Throwable.class.isAssignableFrom(invocation.getMethod().getReturnType())) {
			// TODO handle for case where exceptions that are not known to the
			// caller
			throw new RemoteAccessException("The called method threw an exception", (Throwable) result);
		} else {
			return result;
		}
	}

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	/**
	 * The AMQP template to be used for sending messages and receiving results.
	 * This class is using "Request/Reply" for sending messages as described <a
	 * href=
	 * "http://static.springsource.org/spring-amqp/reference/html/amqp.html#request-reply"
	 * >in the Spring-AMQP documentation</a>.
	 */
	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public QueueNameStrategy getQueueNameStrategy() {
		return queueNameStrategy;
	}

	/**
	 * Determines how the queue for the proxied service is named. Make sure that
	 * this matches the strategy in the respective {@link AmqpServiceExporter}
	 * on the service side. Defaults to {@link CanonicalNameQueueNameStrategy}.
	 */
	public void setQueueNameStrategy(QueueNameStrategy queueNameStrategy) {
		this.queueNameStrategy = queueNameStrategy;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	/**
	 * The strategy being used for serialization of method arguments and return
	 * values. Make sure to use the same strategy in the respective
	 * {@link AmqpServiceExporter} on the service side.Defaults to
	 * {@link JosSerializer}.
	 */
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public MethodSerializer getMethodSerializer() {
		return methodSerializer;
	}

	/**
	 * A strategy to name methods in the message header for lookup in the
	 * service. Make sure to use the same serializer on the service side.
	 */
	public void setMethodSerializer(MethodSerializer methodSerializer) {
		this.methodSerializer = methodSerializer;
	}

}
