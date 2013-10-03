import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.xml.sax.InputSource;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.db.ebean.Transactional;

public class Global extends GlobalSettings {
	@Override
	public void beforeStart(Application app) {
		super.beforeStart(app);
    	Logger.info("Spring configuration ok");
	}
	@Override
	public void onStart(Application app){
		initialize(app);
		try {
			initializeData(app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	@Transactional
	public void initializeData(Application app) throws Exception{
	}

	/** Configuration keys **/
    private static final String SPRING_CONTEXT_PATH = "spring.context";
    private static final String SPRING_NAMESPACE_AWARE = "spring.namespace-aware";
    private static final String SPRING_ADD_PLAY_PROPERTIES = "spring.add-play-properties";

    /** The spring context available for anyone */
    public static GenericApplicationContext applicationContext;

    public void initialize(Application app) {
    	String contextPath = app.configuration().getString(SPRING_CONTEXT_PATH);
    	String namespaceAware = app.configuration().getString(SPRING_NAMESPACE_AWARE);
    	String addPlayProperties = app.configuration().getString(SPRING_ADD_PLAY_PROPERTIES);
    	
    	URL url = null;
    	if (contextPath != null) {
    		Logger.info("Loading application context: "+contextPath);
    		url = app.classloader().getResource(contextPath);
    	} 
    	if (url == null) {
    		Logger.info("Loading default application context: application-context.xml");
            url = app.classloader().getResource("application-context.xml");
        }
        if (url != null) {
            InputStream is = null;
            try {
                Logger.info("Starting Spring application context from "+url.toExternalForm());
                applicationContext = new GenericApplicationContext();
                applicationContext.setClassLoader(Play.application().classloader());
                XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(applicationContext);
                if (!"false".equalsIgnoreCase(namespaceAware)) {
                	Logger.info("Application context is namespace aware");
                    xmlReader.setNamespaceAware(true);
                } else {
                	Logger.warn("Application context is NOT namespace aware");
                }
                xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);

                // 
                // Load Play Configuration
                //
                if (!"false".equalsIgnoreCase(addPlayProperties)) {
                    Logger.info("Adding PropertyPlaceholderConfigurer with Play properties");
                    
                    // Convert play's configuration to plain old java properties
                    Properties properties = new Properties();
                    Set<String> keys = app.configuration().keys();
                    for ( String key : keys) {
                    	try  {
	                    	String value = app.configuration().getString(key);
	                    	properties.setProperty(key, value);
                    	} catch (Throwable t) {
                    		// Some config items are complex, so we'll just skip those for now.
                    	}
                    }
                    
                    PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
                    configurer.setProperties(properties);
                    Logger.info("Adding BeanFactory Post Processor ...");
                    applicationContext.addBeanFactoryPostProcessor(configurer);
                } else {
                    Logger.warn("PropertyPlaceholderConfigurer with Play properties NOT added");
                }
                
                is = url.openStream();
                xmlReader.loadBeanDefinitions(new InputSource(is));
                ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(app.classloader());
                try {
                    applicationContext.refresh();
                } catch (BeanCreationException e) {
                    Throwable ex = e.getCause();
                    throw new RuntimeException("Unable to instantiate Spring application context",ex);
                } finally {
                    Thread.currentThread().setContextClassLoader(originalClassLoader);
                }
            } catch (IOException e) {
                Logger.error("Can't load spring config file",e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Logger.error("Can't close spring config file stream",e);
                    }
                }
            }
        	Logger.info("Spring configuration ok");
        }
    }
    
    void finalyze(Application app) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(app.classloader());
        try {
            applicationContext.refresh();
        } catch (BeanCreationException e) {
            Throwable ex = e.getCause();
            throw new RuntimeException("Unable to instantiate Spring application context",ex);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

    }
}