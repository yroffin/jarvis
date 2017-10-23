package org.jarvis.core.resources.api.tools;

import org.joda.time.DateTime;

/**
 * simple observable run
 */
public interface AnalyzedRun extends Runnable {

	/**
	 * retrieve last run
	 * @return DateTime
	 */
	DateTime getLastRun();

}
