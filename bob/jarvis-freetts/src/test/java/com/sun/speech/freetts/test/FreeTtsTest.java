package com.sun.speech.freetts.test;

/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import junit.framework.TestCase;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * Simple program to demonstrate the use of the FreeTTS speech synthesizer. This
 * simple program shows how to use FreeTTS without requiring the Java Speech API
 * (JSAPI).
 */
public class FreeTtsTest extends TestCase {

	private final String voiceName = "kevin16";

	public void testSimple() {
		System.getProperties().put("freetts.voices",
				"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

		/*
		 * The VoiceManager manages all the voices for FreeTTS.
		 */
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice voice = voiceManager.getVoice(voiceName);

		if (voice == null) {
			System.err.println("Cannot find a voice named " + voiceName
					+ ".  Please specify a different voice.");
			System.exit(1);
		}

		/*
		 * Allocates the resources for the voice.
		 */
		voice.allocate();
		voice.speak("bonjour yannick");
	}
}
