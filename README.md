Spring-Remoting-AMQP
====================

This is a library to use Spring-Remoting via the AMQP protocol.

Usage
-----
To expose a service via Spring-Remoting-AMQP, you need to first make sure to have created an interface and an implementation for this interface. Both can be POJOs, i.e. they do not need to extend/implement framework-specific interfaces. It is recommended to put the service interface (and all classes and exceptions that are being used by the service methods externally, i.e. as arguments or return parameters) into a common library which is then added as a dependency to both the client and the service.

Let's assume that you created a service interface called `MyService` and an according implementation called `MyServiceImpl` (which implements `MyService`). To expose the service on the service-side, you need the following minimal application context xml:

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
	
	<!-- service config -->
	<bean id="myService" class="x.y.z.MyServiceImpl" />
	<bean id="amqpMyServiceExporter" class="de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter">
		<property name="amqpTemplate" ref="amqpTemplate" />
		<property name="connectionFactory" ref="connectionFactory" />
		<property name="service" ref="myService" />
		<property name="serviceInterface" value="x.y.z.MyService" />
	</bean>

</beans>
```

The service exporter will automatically start when the application context is loaded and create a queue with the name `x.y.z.MyService` on the broker defined by the `<rabbit:connection-factory>` element. It will from then on wait for messages to appear in this queue.

On the client-side, use a configuration like this:
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
	
	<!--  client config -->
	<bean id="myServiceProxy" class="de.davidbilge.spring.remoting.amqp.client.AmqpProxyFactoryBean">
		<property name="amqpTemplate" ref="amqpTemplate" />
		<property name="rabbitAdmin" ref="rabbitAdmin" />
		<property name="serviceInterface" value="x.y.z.MyService" />
	</bean>
</beans>
```

You can then inject the `myServiceProxy` bean into any other bean and call methods on this proxy. This will cause the arguments to be serialized and posted as a message to the queue that was defined by the service previously. A temporary response queue will be established to which the service posts the serialized return object as soon as it is finished. The call to the service proxy method will block until either the service-side method has returned something or the timeout defined on the `rabbit-template` has expired.
