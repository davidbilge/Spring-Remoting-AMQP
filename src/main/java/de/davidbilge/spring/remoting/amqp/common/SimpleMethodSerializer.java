package de.davidbilge.spring.remoting.amqp.common;

import java.lang.reflect.Method;

/**
 * Uses the parameters suggested in the {@link Method#equals(Object)} method
 * with the omission of the declaring class: That should be unnecessary in the
 * given context.
 * 
 * <p>
 * Will create a String of the form
 * <code>returnType + " " + name + "(" + parameterTypes + ")"</code> which has
 * the side-effect of being human-readable.
 * 
 * @author David
 * 
 */
public class SimpleMethodSerializer implements MethodSerializer {

	@Override
	public String serialize(Method method) {
		String name = method.getName();
		String parameterTypes = serializeParameterTypes(method.getParameterTypes());
		String returnType = method.getReturnType().getCanonicalName();

		return returnType + " " + name + "(" + parameterTypes + ")";
	}

	private String serializeParameterTypes(Class<?>[] parameterTypes) {
		StringBuilder sb = new StringBuilder();

		for (Class<?> parameterType : parameterTypes) {
			sb.append(parameterType.getCanonicalName() + ",");
		}

		return sb.toString();
	}

}
