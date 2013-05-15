package org.jarvis.main.main.core.impl;

import java.io.File;
import java.util.List;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class JarvisCoreSystemImpl implements IJarvisCoreSystem {

	private final String voiceName = "kevin16";
	private Voice voice;
	private VoiceManager voiceManager;
	private IAimlCoreEngine engine;

	/**
	 * internal utility
	 * 
	 * @param resources
	 * @return
	 * @throws AimlParsingError
	 */
	private IAimlCoreEngine instance(String resources) throws AimlParsingError {
		IAimlCoreEngine core = new AimlCoreEngineImpl();
		core.register(new File(resources));
		core.parse();
		return core;
	}

	@Override
	public void initialize(String aiml) throws AimlParsingError {
		System.getProperties().put("freetts.voices",
				"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

		/*
		 * The VoiceManager manages all the voices for FreeTTS.
		 */
		voiceManager = VoiceManager.getInstance();
		voice = voiceManager.getVoice(voiceName );

		if (voice == null) {
			System.err.println("Cannot find a voice named " + voiceName
					+ ".  Please specify a different voice.");
			System.exit(1);
		}

		/*
		 * Allocates the resources for the voice.
		 */
		voice.allocate();
		
		/**
		 * configure aiml
		 */
		engine = instance(aiml);
	}

	@Override
	public void speak(String value) {
		voice.speak(value);
	}

	@Override
	public void release() {
		/*
		 * Clean up and leave.
		 */
		voice.deallocate();
	}

	@Override
	public void ask(String sentence) throws AimlParsingError {
		List<String> answers = engine.ask(sentence);
		for(String answer : answers) {
			speak(answer);
		}
	}
}
