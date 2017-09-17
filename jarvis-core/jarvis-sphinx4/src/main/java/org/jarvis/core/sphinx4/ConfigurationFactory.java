package org.jarvis.core.sphinx4;

import edu.cmu.sphinx.api.Configuration;

/**
 * sphinx 4 configuration factory
 */
public class ConfigurationFactory {

	/**
	 * @return Configuration
	 */ 
	public static Configuration createConfigurationFrenchPtm() {
		/**
		 * and now sphinx4 configuration
		 */
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/models/fr/ptm");
		configuration.setDictionaryPath("resource:/models/fr/jarvis/jarvis.dict");
		configuration.setLanguageModelPath("resource:/models/fr/jarvis/jarvis.lm");
		configuration.setSampleRate(16000);
		/*
		configuration.setGrammarPath("resource:/models/fr/jarvis");
		configuration.setGrammarName("jarvis");
		configuration.setSampleRate(8000);
		configuration.setUseGrammar(false);
		*/
		return configuration;
	}

	/**
	 * @return Configuration
	 */ 
	public static Configuration createConfigurationFrenchPtm8khz() {
		/**
		 * and now sphinx4 configuration
		 */
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/models/fr/ptm-8khz");
		configuration.setDictionaryPath("resource:/models/fr/jarvis/jarvis.dict");
		configuration.setLanguageModelPath("resource:/models/fr/jarvis/jarvis.lm");
		configuration.setSampleRate(16000);
		/*
		configuration.setGrammarPath("resource:/models/fr/jarvis");
		configuration.setGrammarName("jarvis");
		configuration.setSampleRate(8000);
		configuration.setUseGrammar(false);
		*/
		return configuration;
	}

	/**
	 * @return Configuration
	 */ 
	public static Configuration createConfigurationUs() {
		/**
		 * and now sphinx4 configuration
		 */
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		return configuration;
	}
}
