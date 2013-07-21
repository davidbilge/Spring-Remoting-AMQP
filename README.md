Spring-Remoting-AMQP
====================

This is a library to use Spring-Remoting via the AMQP protocol.

Important note
--------------

This library is no longer necessary. Please use Spring-AMQP 1.2 (or higher) instead.

I have contributed the code in this library directly to the Spring-AMQP project. A detailed description of how it works can be found in the [documentation](http://static.springsource.org/spring-amqp/reference/html/amqp.html#remoting).

However, feel free to use this library if you are stuck on an older version of Spring-AMQP.


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
	
	<!-- service config -->
	<bean id="myService" class="x.y.z.MyServiceImpl" />
	<bean id="amqpMyServiceExporter" class="de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter">
		<property name="rabbitAdmin" ref="rabbitAdmin" />
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
	
	<!--  client config -->
	<bean id="myServiceProxy" class="de.davidbilge.spring.remoting.amqp.client.AmqpProxyFactoryBean">
		<property name="amqpTemplate" ref="amqpTemplate" />
		<property name="rabbitAdmin" ref="rabbitAdmin" />
		<property name="serviceInterface" value="x.y.z.MyService" />
	</bean>
</beans>
```

You can then inject the `myServiceProxy` bean into any other bean and call methods on this proxy. This will cause the arguments to be serialized and posted as a message to the queue that was defined by the service previously. A temporary response queue will be established to which the service posts the serialized return object as soon as it is finished. The call to the service proxy method will block until either the service-side method has returned something or the timeout defined on the `rabbit-template` has expired.

Download
--------
This library is available via Maven:

```xml
<dependency>
  <groupId>de.davidbilge.spring</groupId>
  <artifactId>spring-remoting-amqp</artifactId>
  <version>1.0.2</version>
</dependency>
```

Or as a [direct download](https://oss.sonatype.org/service/local/repositories/releases/content/de/davidbilge/spring/spring-remoting-amqp/1.0.2/spring-remoting-amqp-1.0.2.jar).
