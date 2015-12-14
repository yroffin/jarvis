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

public interface GenericService<K> {
	/**
	 * find all
	 * @return
	 */
	public List<K> findAll();

	/**
	 * find by id
	 * @param id
	 * @return
	 */
	public K getById(String id);

	/**
	 * create entity
	 * @param entity
	 * @return
	 */
	public K create(K entity);

	/**
	 * update entity
	 * @param params
	 * @param jobReentityst
	 * @return
	 */
	public K update(String params, K jobReentityst);
}
