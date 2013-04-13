/*
 * Copyright 2002-2012 the original author or authorimport java.io.IOException;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;
 License at
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

import java.io.IOException;

import de.davidbilge.spring.remoting.amqp.client.AmqpClientInterceptor;
import de.davidbilge.spring.remoting.amqp.service.AmqpServiceExporter;

/**
 * A facility to serialize and deserialize arguments and return values for an
 * AMQP method call. Used in both the {@link AmqpClientInterceptor} and the
 * {@link AmqpServiceExporter} and has to match in a pair of those.
 * 
 * @author David
 * @since 13.04.2013
 */
public interface Serializer {

	byte[] serialize(Object... arguments) throws IOException;

	Object[] deserialize(byte[] serializedArgs) throws IOException, ClassNotFoundException;

}
