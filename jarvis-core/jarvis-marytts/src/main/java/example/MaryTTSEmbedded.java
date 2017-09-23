package example;
import java.util.Locale;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.util.data.audio.AudioPlayer;


public class MaryTTSEmbedded {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		MaryInterface marytts = new LocalMaryInterface();
		marytts.setLocale(Locale.FRENCH);
		
		Set<String> voices = marytts.getAvailableVoices();
		marytts.setVoice("enst-catherine-hsmm");
		AudioInputStream audio = marytts.generateAudio("Bonjour yannick, tu va bien");
		AudioPlayer player = new AudioPlayer(audio);
		player.start();
		player.join();
	}

}