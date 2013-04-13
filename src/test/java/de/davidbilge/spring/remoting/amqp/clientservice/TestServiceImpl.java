package de.davidbilge.spring.remoting.amqp.clientservice;

public class TestServiceImpl implements TestServiceInterface {
	@Override
	public void simpleTestMethod() {
		// Do nothing
	}

	@Override
	public String simpleStringReturningTestMethod(String string) {
		return "Echo " + string;
	}

	@Override
	public void exceptionThrowingMethod() {
		throw new RuntimeException("This is an exception");
	}

	@Override
	public Object echo(Object o) {
		return o;
	}
}