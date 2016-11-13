package org.jarvis.core.log4j2;

import java.io.Serializable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * custom gelf appender
 */
@Plugin(name="GelfCustomAppender", category="Core", elementType="appender", printObject=true)
public class GelfLocalAppender extends AbstractAppender {
	protected static Logger logger = LoggerFactory.getLogger(GelfLocalAppender.class);

	/**
	 * default serial
	 */
	private static final long serialVersionUID = 1L;

	protected GelfRestClient client;
	
	protected GelfLocalAppender(String name, Filter filter, Layout<? extends Serializable> layout, GelfRestClient client) {
		super(name, filter, layout);
		this.client = client;
	}

	@Override
	public void append(LogEvent event) {
		/**
		 * build response
		 */
		javax.ws.rs.core.Response entity = null;
		try {
			entity = client.target()
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .post(Entity.entity(client.getMapper().writeValueAsString(new Object() {
			        	@SuppressWarnings("unused")
						public String level = event.getLevel().name();
			        	@SuppressWarnings("unused")
			        	public String caller = event.getLoggerFqcn();
			        	@SuppressWarnings("unused")
			        	public String message = event.getMessage().getFormattedMessage();
			        }),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			logger.warn("Gelf Error {}:{}", client.target(), e);
		} catch (Exception e) {
			logger.debug("Gelf Error {}:{}", client.target(), e);
		}

		/**
		 * verify result
		 */
		if(entity == null) {
			logger.debug("Gelf Error {} null entity", client.target());
			return;
		}
		if(entity.getStatus() != 202) {
			logger.debug("Gelf Error {}:{}", client.target(), entity.getStatus());
		}
	}

	/**
	 * Your custom appender needs to declare a factory method
	 * annotated with `@PluginFactory`. Log4j will parse the configuration
	 * and call this factory method to construct an appender instance with
	 * the configured attributes.
	 * 
	 * @param name
	 * @param layout
	 * @param filter
	 * @param url 
	 * @return GelfLocalAppender
	 */
    @PluginFactory
    public static GelfLocalAppender createAppender(
    		@PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("url") String url) {
        if (name == null) {
            LOGGER.error("No name provided for GelfLocalAppender");
            return null;
        }
        if (url == null) {
            LOGGER.error("No url provided for GelfLocalAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        
        return new GelfLocalAppender(name, filter, layout, new GelfRestClient(url));
    }
}
