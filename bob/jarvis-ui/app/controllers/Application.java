package controllers;

import org.springframework.beans.factory.annotation.Autowired;

import play.*;
import play.mvc.*;
import service.application.IJarvisApplication;

import views.html.*;

public class Application extends Controller {
	private static IJarvisApplication jarvisApplication;

	public void setJarvisApplication(IJarvisApplication jarvisApplication) {
		this.jarvisApplication = jarvisApplication;
	}

	public static Result index() {
		return ok(index.render("Your new application is ready : " + jarvisApplication));
	}

}
