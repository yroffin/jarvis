package org.jarvis.speech.recognizer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.jarvis.speech.recognizer.IRecognizer;

public class RecognizerImpl implements IRecognizer {

	private static final String GOOGLE_RECOGNIZER_URL = "https://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium";
	private String language;

	/**
	 * Constructor
	 */
	public RecognizerImpl(String language) {
		this.language = language;
	}

	@Override
	public String transform(File file) throws IOException {
		return getRecognizedResponseForFlac(file.getAbsolutePath(), 1, 48000, false, "fr");
	}

	/**
	 * Get recognized data from a FLAC file.
	 *
	 * @param flacFile
	 *            FLAC file to recognize
	 * @param maxResults
	 *            the maximum number of results to return in the response
	 * @param samepleRate
	 *            The sampleRate of the file. Default is 8000.
	 * @return Returns a GoogleResponse, with the response and confidence score
	 * @throws IOException
	 *             Throws exception if something goes wrong
	 */
	public String getRecognizedResponseForFlac(String flacFile, int maxResults, int sampleRate, boolean pfilter,
			String language) throws IOException {
		return getRecognizedResponse(new File(flacFile), maxResults, sampleRate, pfilter, language);
	}

	/**
	 * Get recognized data from a FLAC file.
	 *
	 * @param flacFile
	 *            FLAC file to recognize
	 * @param maxResults
	 *            the maximum number of results to return in the response
	 * @param samepleRate
	 *            The sampleRate of the file. Default is 8000.
	 * @return Returns a GoogleResponse, with the response and confidence score
	 * @throws IOException
	 *             Throws exception if something goes wrong
	 */
	public String getRecognizedResponse(File flacFile, int maxResults, int sampleRate, boolean pfilter, String language)
			throws IOException {
		String response = rawRequest(flacFile, maxResults, sampleRate, pfilter, language);
		return response;
	}

	/**
	 * Performs the request to Google with a file <br>
	 * Request is buffered
	 *
	 * @param inputFile
	 *            Input files to recognize
	 * @return Returns the raw, unparsed response from Google
	 * @throws IOException
	 *             Throws exception if something went wrong
	 */
	private String rawRequest(File inputFile, int maxResults, int sampleRate, boolean pfilter, String language)
			throws IOException {
		URL url;
		URLConnection urlConn;
		OutputStream outputStream;
		BufferedReader br;

		StringBuilder sb = new StringBuilder(GOOGLE_RECOGNIZER_URL);
		if (language != null) {
			sb.append("&lang=");
			sb.append(language);
		} else {
			sb.append("&lang=auto");
		}
		if (!pfilter) {
			sb.append("&pfilter=0");
		}
		sb.append("&maxresults=");
		sb.append(maxResults);

		// URL of Remote Script.
		url = new URL(sb.toString());

		// Open New URL connection channel.
		urlConn = url.openConnection();

		// we want to do output.
		urlConn.setDoOutput(true);

		// No caching
		urlConn.setUseCaches(false);

		// Specify the header content type.
		urlConn.setRequestProperty("Content-Type", "audio/x-flac; rate=" + sampleRate);

		// Send POST output.
		outputStream = urlConn.getOutputStream();

		FileInputStream fileInputStream = new FileInputStream(inputFile);

		byte[] buffer = new byte[256];

		while ((fileInputStream.read(buffer, 0, 256)) != -1) {
			outputStream.write(buffer, 0, 256);
		}

		fileInputStream.close();
		outputStream.close();

		// Get response data.
		br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8")));

		String response = br.readLine();

		br.close();

		return response;

	}
}
