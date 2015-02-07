/**
 *  Copyright 2015 Yannick Roffin
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

package org.jarvis.runtime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class CaptureResult {
	CaptureStream inputStream;
	CaptureStream errorStream;
	int result;

	/**
	 * constructor
	 * 
	 * @param process
	 * @throws InterruptedException
	 */
	public CaptureResult(Process process) throws InterruptedException {
		inputStream = new CaptureStream(process.getInputStream());
		errorStream = new CaptureStream(process.getErrorStream());
		inputStream.start();
		errorStream.start();
		result = process.waitFor();
		inputStream.join();
		errorStream.join();
	}

	public CaptureStream getInputStream() {
		return inputStream;
	}

	public CaptureStream getErrorStream() {
		return errorStream;
	}

	public int getResult() {
		return result;
	}

	public String getJson() throws IOException {
		JsonFactory jfactory = new JsonFactory();
		Writer w = new StringWriter();
		JsonGenerator jGenerator = jfactory.createGenerator(w);
		jGenerator.writeStartObject();
		jGenerator.writeFieldName("result");
		jGenerator.writeNumber(result);
		jGenerator.writeArrayFieldStart("stdout");
		for (String value : inputStream.getValues()) {
			jGenerator.writeString(value);
		}
		jGenerator.writeEndArray();
		jGenerator.writeArrayFieldStart("stderr");
		for (String value : errorStream.getValues()) {
			jGenerator.writeString(value);
		}
		jGenerator.writeEndArray();
		jGenerator.writeEndObject();
		jGenerator.close();
		w.flush();
		return w.toString();
	}
}
