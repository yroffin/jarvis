import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.TargetDataLine;

import org.junit.Test;

/**
 * Copyright 2012 Yannick Roffin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public class Sphinx4SoundTest {

	@Test
	public void test() {
		javax.sound.sampled.Line.Info line = new javax.sound.sampled.Line.Info(
				javax.sound.sampled.Line.class);

		float sampleRate = 44100;
		int bitsPerSample = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;

		Type[] list = AudioSystem.getAudioFileTypes();
		for (Type a : list) {
			System.out.println(a.toString());
		}

		AudioFormat desiredFormat = new AudioFormat(sampleRate, bitsPerSample,
				channels, signed, bigEndian);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
				desiredFormat);

		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("Not supported: " + desiredFormat);
		}

		Info[] local = AudioSystem.getTargetLineInfo(line);
		for (Info a : local) {
			System.out.println(a.toString());
			System.out.println(a.getLineClass().toString());
		}
	}
}
