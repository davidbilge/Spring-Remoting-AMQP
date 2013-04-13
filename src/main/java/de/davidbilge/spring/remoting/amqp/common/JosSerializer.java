package de.davidbilge.spring.remoting.amqp.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serializes arguments and return values using the java builtin serialization
 * mechanism (using {@link ObjectOutputStream} and {@link ObjectInputStream}).
 * This has the advantage of being a simple and powerful mechanism with the
 * drawback of missing inter-platform compatibility.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * 
 */
public class JosSerializer implements Serializer {

	@Override
	public byte[] serialize(Object... arguments) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		oos.writeObject(arguments);

		oos.flush();
		oos.close();

		return bos.toByteArray();
	}

	@Override
	public Object[] deserialize(byte[] serializedArgs) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(serializedArgs);
		ObjectInputStream ois = new ObjectInputStream(bis);

		Object readObject = ois.readObject();

		if (readObject instanceof Object[]) {
			return (Object[]) readObject;
		} else {
			Object[] wrapperArray = { readObject };
			return wrapperArray;
		}
	}
}
