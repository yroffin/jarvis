package controllers;

import models.message.DefaultAnswer;
import models.message.DefaultMessage;

import play.*;
import play.libs.Json;
import play.mvc.*;
import service.application.IJarvisApplication;

import views.html.*;

public class Application extends Controller {
	private static IJarvisApplication jarvisApplication;

	public void setJarvisApplication(IJarvisApplication jarvisApplication) {
		Application.jarvisApplication = jarvisApplication;
	}

	public static Result index() {
		return ok(index.render("Your new application is ready : " + jarvisApplication));
	}

	/**
	 * REST api for sending a message to jarvis
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.TolerantJson.class)
	public static Result send() {
		DefaultMessage msg = (DefaultMessage) extractMessage(DefaultMessage.class);

		if(Logger.isDebugEnabled()) Logger.debug("story: " + msg);

		try {
			DefaultAnswer answers = jarvisApplication.send(msg);
			Logger.info(answers.toString());
			return ok(Json.toJson(answers));
		} catch (Exception e) {
			e.printStackTrace();
			return badRequest();
		}
	}

	/**
	 * extract message from body
	 * @param class1 
	 * 
	 * @return
	 */
	public static DefaultMessage extractMessage(Class<DefaultMessage> class1) {
		/**
		 * retrieve json message
		 */
		return Json.fromJson(request().body().asJson(), class1);
	}
}
