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

package org.jarvis.core.resources.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jarvis.core.model.bean.JobBean;
import org.jarvis.core.model.bean.job.ParamBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.JobRest;
import org.jarvis.core.model.rest.job.ParamRest;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefResources extends ApiMapper {

	@Autowired
	ApiNeo4Service apiNeo4Service;

	/**
	 * @param job
	 * @param param
	 */
	public void add(JobRest job, ParamRest param) {
		try (Transaction create = apiNeo4Service.beginTx()) {
			apiNeo4Service.cypherAddLink(JobBean.class.getSimpleName(), job.id, ParamBean.class.getSimpleName(), param.id, "HREF");
			create.success();
		}
	}

	/**
	 * @param job
	 * @param param
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(JobRest job, Class<ParamRest> param) {
		Result result = apiNeo4Service.cypherAllLink(JobBean.class.getSimpleName(), job.id, ParamBean.class.getSimpleName(), "HREF", "node");
		List<GenericEntity> resultset = new ArrayList<GenericEntity>();
		while (result.hasNext()) {
			GenericEntity genericEntity = new GenericEntity();
			Map<String, Object> fields = result.next();
			genericEntity.id = ((Node) fields.get("node")).getId()+"";
			genericEntity.href = "/api/params/" + genericEntity.id;
			resultset.add(genericEntity);
		}
		return resultset;
	}
}
