package org.jarvis.rest.services;

import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;

@Component
public class CoreRestServices {

	private static boolean shouldReturnHtml(Request request) {
		String accept = request.headers("Accept");
		return accept != null && accept.contains("text/html");
	}

	public Object post(Request request, Response response) {
		if (shouldReturnHtml(request)) {
			// produce HTML
			return new String("<html></html>");
		} else {
			// produce JSON
			return "{}";
		}
	}

}
