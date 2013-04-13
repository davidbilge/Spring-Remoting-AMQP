package de.davidbilge.spring.remoting.amqp;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.davidbilge.spring.remoting.amqp.clientservice.TestServiceInterface;

@ContextConfiguration("/rabbit-context-clientservice.xml")
public class ClientServiceMessageSendingTest extends AbstractJUnit4SpringContextTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceMessageSendingTest.class);

	@Resource
	private TestServiceInterface testServiceProxy;

	@Test
	public void testSendMessage() {
		Assert.assertEquals("Echo Test123", testServiceProxy.simpleStringReturningTestMethod("Test123"));

	}

	@Test(expected = RuntimeException.class)
	public void testExceptionPropagation() {
		testServiceProxy.exceptionThrowingMethod();
	}

	@Test
	public void performanceTest() {
		int cycles = 2500;

		long accumulatedDelta = 0;
		for (int i = 0; i < cycles; ++i) {
			Long old = (Long) testServiceProxy.echo(System.currentTimeMillis());
			accumulatedDelta += System.currentTimeMillis() - old;
		}

		LOGGER.info("Sending " + cycles + " messages with an average duration of "
				+ ((double) accumulatedDelta / (double) cycles) + "ms was successful.");
	}

}
