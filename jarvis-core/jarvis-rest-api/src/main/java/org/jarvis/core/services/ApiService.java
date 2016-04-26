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

import java.util.List;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.services.neo4j.Neo4jService;

/**
 * SERVICE resource
 * @param <S>
 */
public class ApiService<S extends GenericBean> extends Neo4jService<S> {

	/**
	 * base class
	 */
	Class<S> beanClass = null;

	/**
	 * @param beanClass
	 */
	public void setBeanClass(Class<S> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * default constructor
	 * @param apiNeo4Service 
	 */
	public ApiService(ApiNeo4Service apiNeo4Service) {
		super(apiNeo4Service);
	}
	
	/**
	 * find all
	 * @return List<Bean>
	 */
	public List<S> findAll() {
		return super.findAll(beanClass);
	}

	/**
	 * get this bean by id
	 * @param id
	 * @return Bean
	 * @throws TechnicalNotFoundException
	 */
	public S getById(String id) throws TechnicalNotFoundException {
		return super.getById(beanClass, id);
	}

	/**
	 * create this bean
	 */
	public S create(S bean) {
		return super.create(bean);
	}

	/**
	 * create this bean
	 * @param id 
	 * @return Bean
	 * @throws TechnicalNotFoundException 
	 */
	public S remove(String id) throws TechnicalNotFoundException {
		return super.remove(beanClass, id);
	}

	/**
	 * update this bean
	 * @param id
	 * @param bean
	 * @return Bean
	 * @throws TechnicalNotFoundException
	 */
	public S update(String id, S bean) throws TechnicalNotFoundException {
		return super.update(beanClass, bean, id);
	}
}