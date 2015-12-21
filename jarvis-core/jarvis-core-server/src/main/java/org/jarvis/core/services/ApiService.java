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
import org.jarvis.core.services.neo4j.Neo4jService;

/**
 * SERVICE resource
 * @param <Bean>
 */
public class ApiService<Bean> extends Neo4jService<Bean> {

	/**
	 * base class
	 */
	Class<Bean> beanClass = null;

	/**
	 * @param beanClass
	 */
	public void setBeanClass(Class<Bean> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * default constructor
	 */
	public ApiService() {
	}
	
	/**
	 * find all
	 * @return List<Bean>
	 */
	public List<Bean> findAll() {
		return super.findAll(beanClass);
	}

	/**
	 * get this bean by id
	 * @param id
	 * @return Bean
	 * @throws TechnicalNotFoundException
	 */
	public Bean getById(String id) throws TechnicalNotFoundException {
		return super.getById(beanClass, id);
	}

	/**
	 * create this bean
	 */
	public Bean create(Bean bean) {
		return super.create(bean);
	}

	/**
	 * create this bean
	 * @param id 
	 * @return Bean
	 * @throws TechnicalNotFoundException 
	 */
	public Bean remove(String id) throws TechnicalNotFoundException {
		return super.remove(beanClass, id);
	}

	/**
	 * update this bean
	 * @param id
	 * @param bean
	 * @return Bean
	 * @throws TechnicalNotFoundException
	 */
	public Bean update(String id, Bean bean) throws TechnicalNotFoundException {
		return super.update(beanClass, bean, id);
	}
}