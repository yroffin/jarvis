/**
 * 
 */
package org.jarvis.rest.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import us.monoid.web.Resty;

/**
 * @author yannick
 *
 */
public class GoogleTranslate {
	/**
	 * google translate url
	 */
	private static URI url;

	/**
	 * send message to target
	 * 
	 * @param url
	 * @param message
	 * @throws IOException
	 */
	static private void send(Resty client, URI url, String parameters) throws IOException {
		client.withHeader("Content-Type", "application/json");
		client.json(url);
	}

	/**
	 * send message to target
	 * 
	 * @param url
	 * @param message
	 * @throws IOException
	 */
	static public void translate(String googleApiKey, String text, String target) throws IOException {
		if (url == null) {
			try {
				url = new URI("https://www.googleapis.com/language/translate/v2" + "?key=" + googleApiKey + "&q="
						+ URLEncoder.encode(text, "UTF-8") + String.format("&target=%s&source=%s", target, target));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		GoogleTranslate.send(new Resty(), url,
				"?key=" + googleApiKey + "&q=" + text + "&target=" + target + "&source=" + target);
	}
}
