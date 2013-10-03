package service.business;

import org.jarvis.main.exception.AimlParsingError;

import models.message.DefaultAnswer;
import models.message.DefaultMessage;

public interface IJarvisBusiness {

	DefaultAnswer send(DefaultMessage msg) throws AimlParsingError;

}
