package de.davidbilge.spring.remoting.amqp.clientservice;

public interface TestServiceInterface {
	void simpleTestMethod();

	String simpleStringReturningTestMethod(String string);

	void exceptionThrowingMethod();

	Object echo(Object o);
}