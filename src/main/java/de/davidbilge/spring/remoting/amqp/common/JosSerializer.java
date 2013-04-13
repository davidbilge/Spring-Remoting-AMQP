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
