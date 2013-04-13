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
