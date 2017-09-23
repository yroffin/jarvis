/**
 *  Copyright 2017 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.common.core.type;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.common.type.ParamInternalType;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic map
 */
public class GenericMap extends TreeMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1054917224455618539L;

	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * tranform type
	 * @param field
	 * @return
	 */
	private ParamInternalType getType(Field field) {
		if(field.getType() == java.lang.String.class) {
			return ParamInternalType.STRING;
		}
		return ParamInternalType.STRING;
	}

	/**
	 * @param body
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public GenericMap(Object body) throws JsonProcessingException, IOException {
		if(body.getClass() == String.class) {
			/**
			 * from String
			 */
			JsonNode jsonNode = mapper.readTree((String) body);
			Iterator<Map.Entry<String,JsonNode>> iter = jsonNode.fields();
			while ( iter.hasNext() ) {
			     Map.Entry<String,JsonNode> currentEntry = iter.next();
			     if(currentEntry.getValue().isBoolean()) {
			    	 put(currentEntry.getKey(), currentEntry.getValue().asBoolean());
			     }
			     if(currentEntry.getValue().isTextual()) {
			    	 put(currentEntry.getKey(), currentEntry.getValue().asText());
			     }
			     if(currentEntry.getValue().isFloat()) {
			    	 put(currentEntry.getKey(), currentEntry.getValue().asDouble());
			     }
			     if(currentEntry.getValue().isNumber()) {
			    	 put(currentEntry.getKey(), currentEntry.getValue().asLong());
			     }
			}
		} else {
			/**
			 * from Object
			 */
			ReflectionUtils.doWithFields(body.getClass(), new FieldCallback() {
				@Override
				public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
					field.setAccessible(true);
					Object value = field.get(body);
					if (value != null) {
						switch(getType(field)) {
							case STRING:
							case INT:
							case FLOAT:
								put(field.getName(), value);
								break;
						default:
							break;
						}
					}
				}
			});
		}
	}

	/**
	 * 
	 */
	public GenericMap() {
	}
}
