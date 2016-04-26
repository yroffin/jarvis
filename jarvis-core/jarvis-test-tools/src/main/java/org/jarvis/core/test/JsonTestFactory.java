package org.jarvis.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.jarvis.core.exception.TechnicalException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * test tools
 */
public class JsonTestFactory {
	
	protected static ObjectMapper mapper = new ObjectMapper();

	{
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new JodaModule());
	}

	/**
	 * @param bundle
	 * @param instance
	 * @return Object
	 */
	public static Object loadFromClasspath(String bundle, Object instance) {
		/**
		 * find resource
		 */
		InputStream resource = instance.getClass().getClassLoader().getResourceAsStream(bundle);
		if(resource != null) {
			try {
				mapper.readValue(resource, instance.getClass());
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}
		return instance;
	}

	/**
	 * @param bundle
	 * @return String
	 */
	public static String loadFromClasspath(String bundle) {
		/**
		 * find resource
		 */
		InputStream resource = mapper.getClass().getClassLoader().getResourceAsStream(bundle);
		if(resource != null) {
			try {
				StringWriter writer = new StringWriter();
				IOUtils.copy(resource, writer);
				resource.close();
				return writer.toString();
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}
		return "";
	}

	/**
	 * @param instance
	 * @return Object
	 */
	public static String serialize(Object instance) {
		if(instance != null) {
			try {
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}
		return "";
	}
}
