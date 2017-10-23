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
package org.jarvis.core.resources.api;

import java.io.IOException;
import java.util.List;

import org.common.core.exception.TechnicalException;
import org.common.core.type.GenericMap;
import org.jarvis.core.type.ResultType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * pair
 */
public class GenericValue {

	protected static ObjectMapper mapper = new ObjectMapper();

	{
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}

	private ResultType type;

	/**
	 * boolean value
	 */
	private Boolean voBoolean;
	/**
	 * object value
	 */
	private GenericMap voObject;
	/**
	 * list value
	 */
	private List<?> voList;
	/**
	 * string value
	 */
	private String voString;
	

	/**
	 * @param value
	 */
	public void initialize(String value) {
		/**
		 * boolean value ?
		 */
		if(value != null && (value.equals("true") || value.equals("false"))) {
			this.type = ResultType.BOOLEAN;
			this.voBoolean = value.equals("true");
			return;
		}
		/**
		 * object value ?
		 */
		if(value != null && value.startsWith("{")) {
			this.type = ResultType.OBJECT;
			try {
				this.voObject = (GenericMap) mapper.readValue(value, GenericMap.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			return;
		}
		/**
		 * list value ?
		 */
		if(value != null && value.startsWith("[")) {
			this.type = ResultType.ARRAY;
			try {
				this.voList = (List<?>) mapper.readValue(value, List.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			return;
		}
		this.type = ResultType.STRING;
		this.voString = value;
	}
	
	/**
	 * @param value
	 */
	public GenericValue(String value) {
		initialize(value);
	}
	
	/**
	 * @param type
	 * @param value
	 */
	public GenericValue(ResultType type, String value) {
		initialize(value);
		this.type = type;
		this.voString = value;
	}

	/**
	 * @param result
	 */
	public GenericValue(GenericMap result) {
		try {
			initialize(mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * build as object
	 * @param bean
	 */
	public GenericValue(Object bean) {
		try {
			initialize(mapper.writeValueAsString(bean));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @return ResultType
	 */
	public ResultType getType() {
		return type;
	}

	/**
	 * @return Boolean
	 */
	public Boolean asBoolean() {
		if(type != ResultType.BOOLEAN) {
			throw new TechnicalException("Internal error, must be a boolean");
		}
		return voBoolean;
	}

	/**
	 * @return GenericMap
	 */
	public GenericMap asObject() {
		if(type != ResultType.OBJECT) {
			throw new TechnicalException("Internal error, must be an Object");
		}
		return voObject;
	}

	/**
	 * @return List
	 */
	public List<?> asList() {
		if(type != ResultType.ARRAY) {
			throw new TechnicalException("Internal error, must be a List");
		}
		return voList;
	}

	/**
	 * @return String
	 */
	public String asSvg() {
		if(type != ResultType.SVG) {
			throw new TechnicalException("Internal error, must be svg data");
		}
		return voString;
	}

	/**
	 * @return String
	 */
	public String asString() {
		if(type != ResultType.STRING) {
			throw new TechnicalException("Internal error, must be a String");
		}
		return voString;
	}

	/**
	 * @return String
	 */
	public String asFileStream() {
		if(type != ResultType.FILE_STREAM) {
			throw new TechnicalException("Internal error, must be a Stream");
		}
		return voString;
	}

	@Override
	public String toString() {
		return "GenericValue [type=" + type + ", voBoolean=" + voBoolean + ", voObject=" + voObject + ", voList="
				+ voList + ", voString=" + voString + "]";
	}
}
