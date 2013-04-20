/*
 * Copyright 2002-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package de.davidbilge.spring.remoting.amqp.common;

/**
 * Selects the queue name according to the canonical class name of the service interface.
 * 
 * @author David Bilge
 * @since 13.04.2013
 * 
 */
public class CanonicalNameQueueNameStrategy implements QueueNameStrategy {

	@Override
	public String getQueueName(Class<?> serviceInterface) {
		return serviceInterface.getCanonicalName();
	}

}
