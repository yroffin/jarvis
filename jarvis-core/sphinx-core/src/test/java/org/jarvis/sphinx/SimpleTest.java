package org.jarvis.sphinx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;

import org.junit.Test;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.Recognizer;

public class SimpleTest {

	@Test
	public void go() throws IOException {
		File waveFile = new File(
				"C:/Users/yannick/AppData/Local/Temp/test7331341722432731118.wav");
		System.err.println(waveFile.getAbsolutePath());
		Recognizer r = new Recognizer();
		r.getRecognizedDataForWave(waveFile);
	}

	@Test
	public void testLiveSpeechRecognizer() throws MalformedURLException,
			IOException, LineUnavailableException, InterruptedException {
		Microphone m = new Microphone(AudioFileFormat.Type.WAVE);
		File waveFile = File.createTempFile("test", ".wav");
		waveFile.deleteOnExit();
		m.captureAudioToFile(waveFile);
		System.err.println("Go !!");
		System.in.read();
		m.close();
		System.err.println(waveFile.getAbsolutePath());
		Recognizer r = new Recognizer();
		r.getRecognizedDataForWave(waveFile);
	}
}
