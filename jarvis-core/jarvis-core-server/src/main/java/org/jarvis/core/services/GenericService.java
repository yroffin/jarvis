package org.jarvis.core.services;

import java.util.List;

public interface GenericService<K> {
	public List<K> findAll();

	public K getById(String id);

	public K create(K entity);

	public K update(String params, K jobReentityst);
}
