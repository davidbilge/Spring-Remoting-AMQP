package de.davidbilge.spring.remoting.amqp;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration("/rabbit-context.xml")
public class SimpleMessageSendingTest extends AbstractJUnit4SpringContextTests {

	@Resource
	private AmqpTemplate amqpTemplate;

	@Test
	public void testSendMessage() {
		Object result = amqpTemplate.convertSendAndReceive("testqueue", "Test");
		System.out.println(result);

	}

	public static class MessageReceiver implements MessageListener {
		@Resource
		private AmqpTemplate amqpTemplate;

		@Override
		public void onMessage(Message m) {
			Address replyToAddress = m.getMessageProperties().getReplyToAddress();

			amqpTemplate.convertAndSend(replyToAddress.getExchangeName(),
					replyToAddress.getRoutingKey(), "Echo " + new String(m.getBody()));
		}
	}
}
