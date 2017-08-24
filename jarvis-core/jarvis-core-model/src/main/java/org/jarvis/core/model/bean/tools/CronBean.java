/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.core.model.bean.tools;

import org.jarvis.core.model.bean.GenericBean;

/**
 * configuration cron object
 */
public class CronBean extends GenericBean {
	/**
	 * name
	 */
	public String name;
	/**
	 * icon
	 */
	public String icon;
	/**
	 * triggerType
	 */
	public String triggerType;
	/**
	 * latitude
	 */
	public String latitude;
	/**
	 * longitude
	 */
	public String longitude;
	/**
	 * shift
	 */
	public long shift;
	/**
	 * cron
	 */
	public String cron;
	/**
	 * startAtRuntime
	 */
	public boolean startAtRuntime;
	
	@Override
	public String toString() {
		return "CronBean [name=" + name + ", icon=" + icon + ", triggerType=" + triggerType + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", cron=" + cron + ", startAtRuntime=" + startAtRuntime + "]";
	}
}
