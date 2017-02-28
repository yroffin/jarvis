/**
 *  Copyright 2017 Yannick Roffin
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

package org.jarvis.core.model.bean.connector;

/**
 * bean connector
 */
public class MeasureDataBean {
	/**
	 * label
	 */
	public String label;
	/**
	 * min
	 */
	public Double min;
	public Double minref;
	/**
	 * max
	 */
	public Double max;
	public Double maxref;
	/**
	 * avg
	 */
	public Double avg;
	public Double avgref;
	
	/**
	 * @param label
	 * @param max
	 * @param min
	 * @param avg
	 */
	public MeasureDataBean(String label, double max, double min, double avg) {
		this.label = label;
		this.avg = avg;
		this.min = min;
		this.max = max;
		this.avgref = avg;
		this.minref = min;
		this.maxref = max;
	}

	/**
	 * fix reference
	 * @param reference
	 */
	public void delta(MeasureDataBean reference) {
		this.avg = this.avgref - reference.avgref;
		this.min = this.minref - reference.minref;
		this.max = this.maxref - reference.maxref;
	}

	/**
	 * reset
	 */
	public void reset() {
		this.avg = 0.;
		this.min = 0.;
		this.max = 0.;
	}
}
