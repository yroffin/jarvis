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

package org.jarvis.core.model.bean.device;

import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.GenericBean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * device object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceBean extends GenericBean {
	/**
	 * name of this device
	 */
	public String name;
	/**
	 * parameters
	 */
	public String parameters;
	/**
	 * owner of this device
	 */
	public String owner;
	/**
	 * visible
	 */
	public boolean visible;
	/**
	 * icon of this device
	 */
	public String icon;
	/**
	 * tag color
	 */
	public String tagColor;
	/**
	 * tag opacity
	 */
	public String tagOpacity;
	/**
	 * tag text color
	 */
	public String tagTextColor;
	/**
	 * rowSpan
	 */
	public String rowSpan;
	/**
	 * colSpan
	 */
	public String colSpan;
	/**
	 * template
	 */
	public String template;
	/**
	 * render
	 */
	public GenericMap render;
	@Override
	public String toString() {
		return "DeviceBean [name=" + name + ", parameters=" + parameters + ", owner=" + owner + ", visible=" + visible
				+ ", icon=" + icon + ", tagColor=" + tagColor + ", tagOpacity=" + tagOpacity + ", tagTextColor="
				+ tagTextColor + ", rowSpan=" + rowSpan + ", colSpan=" + colSpan + ", template=" + template + "]";
	}
}
