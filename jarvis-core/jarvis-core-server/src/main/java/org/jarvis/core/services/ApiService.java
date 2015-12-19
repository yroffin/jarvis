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

public class ApiService<T> extends Neo4jService<T> {

	/**
	 * base class
	 */
	Class<T> genericTypeClass = null;

	public Class<T> fake(Class<T> genericTypeClass) {
		return genericTypeClass;
	}

	/**
	 * default constructor
	 */
	@SuppressWarnings("unchecked")
	public ApiService() {
		genericTypeClass = (Class<T>) getClass().getMethods()[0].getReturnType();		
	}
	
	/**
	 * find all
	 * @return
	 */
	public List<T> findAll() {
		return super.findAll(genericTypeClass);
	}

	/**
	 * get this bean by id
	 * @param id
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	public T getById(String id) throws TechnicalNotFoundException {
		return super.getById(genericTypeClass, id);
	}

	/**
	 * create this bean
	 */
	public T create(T bean) {
		return super.create(bean);
	}

	/**
	 * update this bean
	 * @param id
	 * @param bean
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	public T update(String id, T bean) throws TechnicalNotFoundException {
		return super.update(genericTypeClass, bean, id);
	}
}