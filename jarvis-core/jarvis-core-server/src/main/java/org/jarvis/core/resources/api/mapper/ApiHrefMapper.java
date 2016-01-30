package org.jarvis.core.resources.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.type.GenericMap;
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
	 * add a new link between Owner and Child
	 * 
	 * @param owner 
	 * @param child 
	 * @param properties 
	 * @param type 
	 * @param relation 
	 * @return String
	 * @throws TechnicalNotFoundException 
	 */
	public GenericEntity add(Owner owner, Child child, GenericMap properties, String type, String relation) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			Result result = apiNeo4Service.cypherAddLink(ownerLabel, owner.id, childLabel, child.id, relation);
			create.success();
			if(result.hasNext()) {
				Map<String, Object> rows = result.next();
				/**
				 * set node properties
				 */
				GenericEntity genericEntity = new GenericEntity();
				genericEntity.id = child.id;
				genericEntity.instance = rows.get("id(r)")+"";
				genericEntity.href = "/api/"+type+"/" + genericEntity.id;
				/**
				 * set relationship properties
				 */
				Relationship r = (Relationship) rows.get("r");
				for(Entry<String, Object> property : properties.entrySet()) {
					if(String.class == property.getValue().getClass()) {
						r.setProperty(property.getKey(), (String) property.getValue());
						genericEntity.put(property.getKey(), (String) property.getValue());
					}
				}
				return genericEntity;
			}
		}
		throw new TechnicalNotFoundException();
	}

	/**
	 * add a new link between Owner and Child
	 * 
	 * @param relId 
	 * @param properties 
	 * @return String
	 * @throws TechnicalNotFoundException 
	 */
	public GenericMap update(String relId, GenericMap properties) throws TechnicalNotFoundException {
		try (Transaction update = apiNeo4Service.beginTx()) {
			Result result = apiNeo4Service.cypherFindLink(ownerLabel, childLabel, relId);
			if(result.hasNext()) {
				Map<String, Object> rows = result.next();
				/**
				 * set relationship properties
				 */
				Relationship r = (Relationship) rows.get("r");
				for(Entry<String, Object> property : properties.entrySet()) {
					if(String.class == property.getValue().getClass()) {
						r.setProperty(property.getKey(), (String) property.getValue());
					}
				}
				update.success();
				return properties;
			}
		}
		throw new TechnicalNotFoundException();
	}

	/**
	 * @param owner 
	 * @param relation 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(Owner owner, String relation) {
		Result result = apiNeo4Service.cypherAllLink(ownerLabel, owner.id, childLabel, relation, "node");
		List<GenericEntity> resultset = new ArrayList<GenericEntity>();
		while (result.hasNext()) {
			GenericEntity genericEntity = new GenericEntity();
			Map<String, Object> fields = result.next();
			/**
			 * get optional properties on relationship
			 */
			Relationship r = (Relationship) fields.get("r");
			try (Transaction find = apiNeo4Service.beginTx()) {
				for(Entry<String, Object> property : r.getAllProperties().entrySet()) {
					if(String.class == property.getValue().getClass()) {
						genericEntity.put(property.getKey(), (String) property.getValue());					
					}
				}
				find.success();
			}
			/**
			 * read id
			 */
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
	public GenericEntity remove(Owner owner, Child child, String instance, String relation) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			Result result = apiNeo4Service.cypherDeleteLink(ownerLabel, owner.id, childLabel, child.id, relation, instance);
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
