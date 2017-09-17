package org.jarvis.core.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.sphinx4.ConfigurationFactory;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;

/**
 * @author kazoar
 *
 */
public class CoreSphinxServiceTest {

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	private void readStream(Configuration configuration, String resource) throws MalformedURLException, IOException {
		Context context = new Context(configuration);
		//context.setLocalProperty("decoder->searchManager", "allphoneSearchManager");
		
		Recognizer recognizer = context.getInstance(Recognizer.class);
        InputStream stream = CoreSphinxServiceTest.class
                .getResourceAsStream(resource);
        stream.skip(44);

        // Simple recognition with generic model
        recognizer.allocate();
        context.setSpeechSource(stream, TimeFrame.INFINITE);
        Result result;
        
        List<SpeechResult> speechResultList = new ArrayList<SpeechResult>();
        
        while ((result = recognizer.recognize()) != null) {
            SpeechResult speechResult = new SpeechResult(result);
            speechResultList.add(speechResult);
        }

        for(SpeechResult speechResult : speechResultList) {
            System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
            System.out.println("List of recognized words and their times:");
            for (WordResult r : speechResult.getWords()) {
                System.out.println(r);
            }
        }

        recognizer.deallocate();
	}

	/**
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Test
	public void testFrench() throws MalformedURLException, IOException {
		Configuration configuration = ConfigurationFactory.createConfigurationFrenchPtm();
		readStream(configuration, "/wav/serbe.wav");
	}

	/**
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Test
	public void test_100019021001803() throws MalformedURLException, IOException {
		Configuration configuration = ConfigurationFactory.createConfigurationUs();
		readStream(configuration, "/wav/10001-90210-01803.wav");
	}
}
