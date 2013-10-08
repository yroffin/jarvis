package org.jarvis.main.main.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javazoom.jl.decoder.JavaLayerException;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;

import com.gtranslate.Audio;
import com.gtranslate.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarvisCoreSystemImpl implements IJarvisCoreSystem {
	protected Logger logger = LoggerFactory.getLogger(JarvisCoreSystemImpl.class);

	private IAimlCoreEngine engine;

	/**
	 * internal utility
	 * 
	 * @param resources
	 * @return
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	private IAimlCoreEngine instance(String resources) throws AimlParsingError,
			IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resources);
		byte[] b = new byte[is.available()];
        is.read(b);
        List<String> res = new ArrayList<String>();
        for(String resource : new String(b).split("\n")) {
        	if(resource.trim().length() > 0) {
        		logger.warn("Read resource : " + resource);
        		res.add(resource);
        	}
        }
		IAimlCoreEngine core = new AimlCoreEngineImpl(res);
		return core;
	}

	Audio voiceManager = null;

	@Override
	public void initialize(String aiml) throws AimlParsingError, IOException {
		/*
		 * The VoiceManager manages all the voices for FreeTTS.
		 */
		voiceManager = Audio.getInstance();

		if (voiceManager == null) {
			System.err.println("Cannot initialize audio system");
			System.exit(1);
		}

		/**
		 * configure aiml
		 */
		engine = instance(aiml);
	}

	@Override
	public void speak(String value) throws IOException {
		InputStream sound = null;
		try {
			try {
				if(value != null && value.length()>0) {
					sound = voiceManager.getAudio(value, Language.FRENCH);
					voiceManager.play(sound);
				}
			} catch (JavaLayerException e) {
				throw new IOException(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			if(sound != null) sound.close();
		}
	}

	@Override
	public void release() {
	}

	@Override
	public List<IAimlHistory> chat(String sentence) throws AimlParsingError {
		List<IAimlHistory> answers = engine.ask(sentence);
		return answers;
	}

	@Override
	public List<IAimlHistory> ask(String sentence) throws AimlParsingError {
		List<IAimlHistory> answers = chat(sentence);
		for (IAimlHistory answer : answers) {
			try {
				speak(answer.getAnswer().replace("\n", "").replace("\r", "").trim());
			} catch (IOException e) {
				throw new AimlParsingError(e);
			}
		}
		return answers;
	}

	@Override
	public IAimlCoreEngine getEngine() {
		return engine;
	}
}
