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

package org.jarvis.core.services;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.model.rest.ScenarioRest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:server.properties")
public class ApiScenarioService implements GenericService<ScenarioRest> {

	public List<ScenarioRest> findAll() {
		return new ArrayList<ScenarioRest>();
	}

	public ScenarioRest getById(String params) {
		return new ScenarioRest();
	}

	public ScenarioRest create(ScenarioRest ScenarioRest) {
		return ScenarioRest;
	}

	public ScenarioRest update(String params, ScenarioRest ScenarioRest) {
		return ScenarioRest;
	}

}
