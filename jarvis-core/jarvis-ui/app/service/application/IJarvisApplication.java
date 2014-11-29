package service.application;

import tools.exception.TechnicalException;
import models.message.DefaultAnswer;
import models.message.DefaultMessage;

public interface IJarvisApplication {

	DefaultAnswer send(DefaultMessage msg) throws TechnicalException;

}
