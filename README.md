Spring-Remoting-AMQP
====================

This is a library to use Spring-Remoting via the AMQP protocol.

A sample configuration might look like in the test:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:rabbit="http://www.springframework.org/schema/rabbit" xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.2.xsd">

	<rabbit:connection-factory id="connectionFactory" />
	<rabbit:template id="amqpTemplate" connection-factory="connectionFactory" reply-timeout="50000" />
	<rabbit:admin id="rabbitAdmin" connection-factory="connectionFactory" />
	<rabbit:direct-exchange name="testxchange" />
	
	<context:annotation-config />

	
	<!--  client config -->
	<bean id="testServiceProxy" class="de.davidbilge.spring.remoting.amqp.client.AmqpProxyFactoryBean">
		<property name="amqpTemplate" ref="amqpTemplate" />
		<property name="rabbitAdmin" ref="rabbitAdmin" />
		<property name="serviceInterface" value="de.davidbilge.spring.remoting.amqp.clientservice.TestServiceInterface" />
	</bean>
	
	<!-- service config -->
	<bean id="testService" class="de.davidbilge.spring.remoting.amqp.clientservice.TestServiceImpl" />
	<bean id="amqpTestServiceExporter" class="de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter">
		<property name="amqpTemplate" ref="amqpTemplate" />
		<property name="connectionFactory" ref="connectionFactory" />
		<property name="service" ref="testService" />
		<property name="serviceInterface" value="de.davidbilge.spring.remoting.amqp.clientservice.TestServiceInterface" />
		<property name="concurrentConsumers" value="10" />
	</bean>

</beans>
```