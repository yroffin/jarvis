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

package org.jarvis.core.sphinx4;

import java.io.IOException;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.util.StreamDataSource;


/**
 * High-level class for live speech recognition.
 */
public class LiveSpeechLocalRecording extends AbstractSpeechRecognizer {

    private final Microphone microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     * @throws IOException if model IO went wrong
     */
    public LiveSpeechLocalRecording(Configuration configuration) throws IOException
    {
        super(configuration);
        microphone = new Microphone(16000, 16, true, false);
        context.getInstance(StreamDataSource.class)
            .setInputStream(microphone.getStream());
    }

    /**
     * @param resultListener
     */
    public void add(ResultListener resultListener) {
    	this.recognizer.addResultListener(resultListener);
	}

    /**
     * Allocate recognition process.
     *
     * @see         LiveSpeechLocalRecording#stopRecognition()
     */
    public void allocate() {
        recognizer.allocate();
    }

    /**
     * Starts recording process.
     *
     * @see         LiveSpeechLocalRecording#stopRecognition()
     */
    public void startRecording() {
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see LiveSpeechLocalRecording#stopRecognition()
     */
    public void stopRecognition() {
        microphone.stopRecording();
        recognizer.deallocate();
    }
}
