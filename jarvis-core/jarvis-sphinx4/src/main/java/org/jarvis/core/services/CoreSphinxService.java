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

package org.jarvis.core.services;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.common.core.exception.TechnicalException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jarvis.core.sphinx4.LiveSpeechLocalRecording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

/**
 * simple sphinx service
 */
@Component
public class CoreSphinxService {

	protected static Logger logger = LoggerFactory.getLogger(CoreSphinxService.class);
	protected static LiveSpeechLocalRecording recognizer;
	protected static LinkedBlockingQueue<SpeechResult> queue = new LinkedBlockingQueue<SpeechResult>(10);

	@Autowired
	Environment env;

	/**
	 * simple json mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * mqtt client
	 */
	protected MqttClient client;

	/**
	 * stop threads
	 */
	public static boolean running = true;
	/**
	 * wake word
	 */
	public static String wakeWord = "default";

	/**
	 * @throws IOException
	 */
	@PostConstruct
	public void init() throws IOException {
		wakeWord = env.getProperty("jarvis.sphinx4.wakeword", "jarvis");
		boolean enabled = Boolean.parseBoolean(env.getProperty("jarvis.sphinx4.enabled", "false"));
		if (enabled) {
			/**
			 * declare a mqtt client for suscribe to event
			 */
			try {
				/**
				 * Construct an MQTT blocking mode client
				 */
				this.client = new MqttClient(env.getProperty("jarvis.mqtt.url"),
						"sphinx-service-" + Thread.currentThread().getName());
			} catch (MqttException e) {
				logger.error("Unable to set up client: {}", e);
				throw new TechnicalException(e);
			}

			/**
			 * and now sphinx4 configuration
			 */
			Configuration configuration = new Configuration();

			configuration.setAcousticModelPath("resource:/models/fr/ptm-8khz");
			configuration.setDictionaryPath("resource:/models/fr/jarvis/jarvis.dict");
			configuration.setLanguageModelPath("resource:/models/fr/jarvis/jarvis.lm");
			configuration.setGrammarPath("resource:/models/fr/jarvis");
			configuration.setGrammarName("jarvis");
			configuration.setSampleRate(8000);
			configuration.setUseGrammar(false);

			recognizer = new LiveSpeechLocalRecording(configuration);

			class Listener implements ResultListener {
				@Override
				public void newProperties(PropertySheet ps) throws PropertyException {
				}

				@Override
				public void newResult(Result result) {
					logger.debug("newResult: {}", result.getBestPronunciationResult());
				}
			}
			;

			recognizer.add(new Listener());

			/**
			 * Allocate data.
			 */
			recognizer.allocate();

			/**
			 * launch capture
			 */
			capture();
		}
	}

	/**
	 * Captures the words
	 */
	void capture() {
		/**
		 * producer
		 */
		new Thread() {
			@Override
			public void run() {
				try {
					while (running) {
						/**
						 * Start recognition process pruning previously cached
						 * data.
						 */
						recognizer.startRecording();
						SpeechResult result = recognizer.getResult();
						if (result != null) {
							queue.put(result);
						}
					}
				} catch (InterruptedException ex) {
					logger.warn("Exception {}", ex);
				}
			}
		}.start();

		/**
		 * simple class to publish result
		 */
		class BrokerMsg {
			@SuppressWarnings("unused")
			public String hypothesis;
			@SuppressWarnings("unused")
			public String bestFinalResultNoFiller;
			@SuppressWarnings("unused")
			public String bestPronunciationResult;
			@SuppressWarnings("unused")
			public String bestResultNoFiller;

			public BrokerMsg(SpeechResult r) {
				hypothesis = r.getHypothesis();
				bestFinalResultNoFiller = r.getResult().getBestFinalResultNoFiller();
				bestPronunciationResult = r.getResult().getBestPronunciationResult();
				bestResultNoFiller = r.getResult().getBestResultNoFiller();
			}
		}

		/**
		 * consumer
		 */
		new Thread() {
			@Override
			public void run() {
				try {
					while (running) {
						SpeechResult result = queue.take();
						if (result.getHypothesis() != null && !result.getHypothesis().trim().equals("<unk>")
								&& !result.getHypothesis().trim().equals("")) {
							logger.info("newResult: {}", result.getHypothesis());
						}
						try {
							BrokerMsg b = new BrokerMsg(result);
							/**
							 * check connexion
							 */
							if (!client.isConnected()) {
								client.connect();
							}
							/**
							 * only send sentence with wake word
							 */
							if (result.getHypothesis() != null && result.getHypothesis().startsWith(wakeWord)) {
								client.publish("/sphinx4", mapper.writeValueAsString(b).getBytes(), 0, false);
							}
						} catch (JsonProcessingException e) {
							logger.error("json parse error {}", e);
						} catch (MqttException e) {
							logger.error("publish error {} - {}", e, e.getReasonCode());
						}
						/**
						 * Get individual words and their times.
						 */
						if (logger.isDebugEnabled()) {
							for (WordResult r : result.getWords()) {
								logger.info("word: {} {} {}", r.toString(), r.getScore(), r.getConfidence());
							}
						}
					}
				} catch (InterruptedException ex) {
					logger.warn("Exception {}", ex);
				}
			}
		}.start();

		/**
		 * hook
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				running = false;
				;
			}
		});
	}

	/**
	 * Entry to run the program
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final CoreSphinxService recorder = new CoreSphinxService();

		recorder.init();
		recorder.capture();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		running = false;
	}
}
