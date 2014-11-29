package service.application.impl;

import org.jarvis.main.exception.AimlParsingError;

import play.Logger;

import models.message.DefaultAnswer;
import models.message.DefaultMessage;
import service.application.IJarvisApplication;
import service.business.IJarvisBusiness;
import tools.exception.TechnicalException;

public class JarvisApplicationImpl implements IJarvisApplication {
	private IJarvisBusiness jarvisBusiness;

	public void setJarvisBusiness(IJarvisBusiness jarvisBusiness) {
		this.jarvisBusiness = jarvisBusiness;
	}

	@Override
	public DefaultAnswer send(DefaultMessage msg) throws TechnicalException {
		try {
			return jarvisBusiness.send(msg);
		} catch (AimlParsingError e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
			throw new TechnicalException(e);
		}
	}
}
