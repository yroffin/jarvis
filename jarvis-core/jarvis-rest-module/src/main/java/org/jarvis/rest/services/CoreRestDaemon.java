package org.jarvis.rest.services;

import static spark.Spark.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRestDaemon {

	@Autowired
	CoreRestServices coreRestServices;

	public void server() {
		post("/hello", (request, response) -> coreRestServices.post(request, response));
	}
}
