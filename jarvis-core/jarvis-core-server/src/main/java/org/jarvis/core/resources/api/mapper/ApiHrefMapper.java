package org.jarvis.core.resources.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * api mapper
 *
 * @param <Owner>
 * @param <Child>
 */
public abstract class ApiHrefMapper<Owner extends GenericEntity,Child extends GenericEntity> extends ApiMapper {

	private String ownerLabel;
	private String childLabel;
	private String type;

	protected void init(String ownerLabel, String childLabel, String type) {
		super.init();
		this.ownerLabel = ownerLabel;
		this.childLabel = childLabel;
		this.type = type;
	}

	@Autowired
	ApiNeo4Service apiNeo4Service;

	/**
	 * @param owner 
	 * @param child 
	 * @param type 
	 * @return String
	 * @throws TechnicalNotFoundException 
	 */
	public GenericEntity add(Owner owner, Child child, String type) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			Result result = apiNeo4Service.cypherAddLink(ownerLabel, owner.id, childLabel, child.id, "HREF");
			create.success();
			if(result.hasNext()) {
				GenericEntity genericEntity = new GenericEntity();
				genericEntity.id = child.id;
				genericEntity.instance = result.next().get("id(r)")+"";
				genericEntity.href = "/api/"+type+"/" + genericEntity.id;
				return genericEntity;
			}
		}
		throw new TechnicalNotFoundException();
	}

	/**
	 * @param owner 
	 * @param child 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(Owner owner, Class<Child> child) {
		Result result = apiNeo4Service.cypherAllLink(ownerLabel, owner.id, childLabel, "HREF", "node");
		List<GenericEntity> resultset = new ArrayList<GenericEntity>();
		while (result.hasNext()) {
			GenericEntity genericEntity = new GenericEntity();
			Map<String, Object> fields = result.next();
			genericEntity.id = ((Node) fields.get("node")).getId()+"";
			genericEntity.instance = ((Relationship) fields.get("r")).getId()+"";
			genericEntity.href = "/api/"+type+"/" + genericEntity.id;
			resultset.add(genericEntity);
		}
		return resultset;
	}

	/**
	 * remove relationship
	 * @param owner 
	 * @param child 
	 * @param instance
	 * @return GenericEntity
	 * @throws TechnicalNotFoundException 
	 */
	public GenericEntity remove(Owner owner, Child child, String instance) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			Result result = apiNeo4Service.cypherDeleteLink(ownerLabel, owner.id, childLabel, child.id, "HREF", instance);
			create.success();
			if(result.hasNext()) {
				GenericEntity genericEntity = new GenericEntity();
				genericEntity.id = child.id;
				genericEntity.instance = instance;
				genericEntity.href = "/api/"+type+"/" + genericEntity.id;
				return genericEntity;
			}
		}
		throw new TechnicalNotFoundException();
	}
}
