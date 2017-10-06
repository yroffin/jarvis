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

package org.jarvis.core.bpm.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BpmServiceTaskStartExecutionListener
 */
@CamundaSelector(type = "serviceTask", event = ExecutionListener.EVENTNAME_START)
public class BpmServiceTaskStartExecutionListener implements ExecutionListener {
	protected Logger logger = LoggerFactory.getLogger(BpmServiceTaskStartExecutionListener.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		logger.info("[ServiceTask] start {} {}", 
				execution.getCurrentActivityName(),
				execution.getActivityInstanceId());
	}

}
