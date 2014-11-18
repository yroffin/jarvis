package org.jarvis.sphinx;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;

public class SimpleTest {

	@Test
	public void testLiveSpeechRecognizer() throws MalformedURLException, IOException {
		Configuration configuration = new Configuration();

		// Set language model.
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/language/en-us.lm.dmp");
		// Set path to acoustic model.
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/acoustic/wsj");
		// Set path to dictionary.
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/acoustic/wsj/dict/cmudict.0.6d");

		LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(true);
		SpeechResult result = recognizer.getResult();
		// Pause recognition process. It can be resumed then with startRecognition(false).
		recognizer.stopRecognition();
	}
	
	
		@Test
	public void test() throws MalformedURLException, IOException {
		Configuration configuration = new Configuration();
		 
		// Set language model.
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/language/en-us.lm.dmp");
		// Set path to acoustic model.
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/acoustic/wsj");
		// Set path to dictionary.
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/acoustic/wsj/dict/cmudict.0.6d");
		
		Context context = new Context(configuration);
		// Use microphone input.
		//context.useMicrophone();
		 
		// Get required instances. 
		Recognizer recognizer = context.getInstance(Recognizer.class);
		Microphone microphone = context.getInstance(Microphone.class);
		 
		// Start recognition.
		recognizer.allocate();
		microphone.startRecording();
		Result result = recognizer.recognize();
		microphone.stopRecording();
		recognizer.deallocate();
	}

}
