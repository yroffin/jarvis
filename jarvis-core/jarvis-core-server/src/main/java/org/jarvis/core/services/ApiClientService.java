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

import org.jarvis.core.model.rest.ClientRest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:server.properties")
public class ApiClientService implements GenericService<ClientRest> {

	public List<ClientRest> findAll() {
		return new ArrayList<ClientRest>();
	}

	public ClientRest getById(String params) {
		return new ClientRest();
	}

	public ClientRest create(ClientRest clientRest) {
		return clientRest;
	}

	public ClientRest update(String params, ClientRest clientRest) {
		return clientRest;
	}

}
