package org.jarvis.main.main.core.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;

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
	 * @throws IOException
	 */
	private IAimlCoreEngine instance(String resources) throws AimlParsingError,
			IOException {
		IAimlCoreEngine core = new AimlCoreEngineImpl(Arrays.asList(
				"aiml-std-fr/A.aiml", "aiml-std-fr/apparence.aiml",
				"aiml-std-fr/atomique.aiml", "aiml-std-fr/B.aiml",
				"aiml-std-fr/botmaster.aiml", "aiml-std-fr/C.aiml",
				"aiml-std-fr/calendrier.aiml", "aiml-std-fr/cest.aiml",
				"aiml-std-fr/chngmode.aiml", "aiml-std-fr/chngvoie.aiml",
				"aiml-std-fr/combien.aiml", "aiml-std-fr/comment.aiml",
				"aiml-std-fr/connaissance.aiml", "aiml-std-fr/connexion.aiml",
				"aiml-std-fr/D.aiml", "aiml-std-fr/deduction.aiml",
				"aiml-std-fr/E.aiml", "aiml-std-fr/estceque.aiml",
				"aiml-std-fr/estu.aiml", "aiml-std-fr/etranger.aiml",
				"aiml-std-fr/exclamation.aiml", "aiml-std-fr/F.aiml",
				"aiml-std-fr/feminin.aiml", "aiml-std-fr/G.aiml",
				"aiml-std-fr/genre.aiml", "aiml-std-fr/H.aiml",
				"aiml-std-fr/humour.aiml", "aiml-std-fr/I.aiml",
				"aiml-std-fr/insulte.aiml", "aiml-std-fr/interjections.aiml",
				"aiml-std-fr/interrogation.aiml", "aiml-std-fr/J.aiml",
				"aiml-std-fr/K.aiml", "aiml-std-fr/L.aiml",
				"aiml-std-fr/M.aiml", "aiml-std-fr/masculin.aiml",
				"aiml-std-fr/merci.aiml", "aiml-std-fr/N.aiml",
				"aiml-std-fr/negation.aiml", "aiml-std-fr/nom.aiml",
				"aiml-std-fr/nombres.aiml", "aiml-std-fr/O.aiml",
				"aiml-std-fr/ou.aiml", "aiml-std-fr/ouinon.aiml",
				"aiml-std-fr/P.aiml", "aiml-std-fr/peuxtu.aiml",
				"aiml-std-fr/pourquoi.aiml", "aiml-std-fr/prefixes.aiml",
				"aiml-std-fr/profile.aiml", "aiml-std-fr/Q.aiml",
				"aiml-std-fr/quand.aiml", "aiml-std-fr/que.aiml",
				"aiml-std-fr/quel.aiml", "aiml-std-fr/questceque.aiml",
				"aiml-std-fr/questions.aiml", "aiml-std-fr/qui.aiml",
				"aiml-std-fr/quoi.aiml", "aiml-std-fr/R.aiml",
				"aiml-std-fr/reponses.aiml", "aiml-std-fr/rumeur.aiml",
				"aiml-std-fr/S.aiml", "aiml-std-fr/salutation.aiml",
				"aiml-std-fr/sexe.aiml", "aiml-std-fr/singulier.aiml",
				"aiml-std-fr/srai-deduction.aiml",
				"aiml-std-fr/srai-insulte.aiml", "aiml-std-fr/srai-sexe.aiml",
				"aiml-std-fr/srai.aiml", "aiml-std-fr/substitueur.aiml",
				"aiml-std-fr/suffixes.aiml", "aiml-std-fr/synonymes.aiml",
				"aiml-std-fr/system.aiml", "aiml-std-fr/T.aiml",
				"aiml-std-fr/that-questions.aiml", "aiml-std-fr/that.aiml",
				"aiml-std-fr/U.aiml", "aiml-std-fr/V.aiml",
				"aiml-std-fr/W.aiml", "aiml-std-fr/X.aiml",
				"aiml-std-fr/Z.aiml"));
		return core;
	}

	@Override
	public void initialize(String aiml) throws AimlParsingError, IOException {
		System.getProperties().put("freetts.voices",
				"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

		/*
		 * The VoiceManager manages all the voices for FreeTTS.
		 */
		voiceManager = VoiceManager.getInstance();
		voice = voiceManager.getVoice(voiceName);

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
	public List<IAimlHistory> chat(String sentence) throws AimlParsingError {
		List<IAimlHistory> answers = engine.ask(sentence);
		return answers;
	}

	@Override
	public void ask(String sentence) throws AimlParsingError {
		for (IAimlHistory answer : chat(sentence)) {
			speak(answer.getAnswer());
		}
	}
}
