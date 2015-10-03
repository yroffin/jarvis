package org.jarvis.speech.recognizer;

import java.io.File;
import java.io.IOException;

public interface IRecognizer {

	String transform(File file) throws IOException;

}
