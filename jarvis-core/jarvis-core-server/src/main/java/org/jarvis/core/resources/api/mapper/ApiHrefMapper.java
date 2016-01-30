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
 * api mapper based on href link
 *
 * @param <T>
 * @param <S>
 */
public abstract class ApiHrefMapper<T extends GenericEntity,S extends GenericEntity> extends ApiMapper {

	private String ownerLabel;
	private String childLabel;
	private String type;

	/**
	 * initialize this href mapper
	 * @param ownerLabel
	 * @param childLabel
	 * @param type
	 */
	protected void init(String ownerLabel, String childLabel, String type) {
		super.init();
		this.ownerLabel = ownerLabel;
		this.childLabel = childLabel;
		this.type = type;
	}

	@Autowired
	ApiNeo4Service apiNeo4Service;

	/**
	 * add a new link between T and S
	 * @param owner
	 * owner entity
	 * @param child
	 * target entity 
	 * @param properties
	 * properties on the relationship 
	 * @param type 
	 * href
	 * @param relation
	 * relationship type 
	 * @return GenericEntity
	 * @throws TechnicalNotFoundException
	 * when no data found
	 */
	public GenericEntity add(T owner, S child, GenericMap properties, String type, String relation) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			/**
			 * relation can be override by post
			 */
    		String defaultRelType = relation;
    		if(properties.get(HREF.toLowerCase()) != null) {
    			defaultRelType = (String) properties.get(HREF.toLowerCase());
    		}
			Result result = apiNeo4Service.cypherAddLink(ownerLabel, owner.id, childLabel, child.id, defaultRelType);
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
	 * update a relationship between two entities
	 * @param relId
	 * the relation id
	 * @param properties
	 * properties on this relationship 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException
	 * when no data found
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
	 * find all element filtered by the relation type
	 * @param owner
	 * the owner entity
	 * @param relation
	 * the relation type (default HREF)
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(T owner, String relation) {
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
	 * remove relationship in neo4j database<br>
	 * @param owner
	 * the owner of this relation 
	 * @param child
	 * the target entity 
	 * @param instance
	 * the relationship instance in database
	 * @param relation
	 * the relation type (default HREF) : HREF, HREF_IF, HREF_THEN, HREF_ELSE ... 
	 * @return GenericEntity, the old entity
	 * @throws TechnicalNotFoundException
	 * then no relation or entity exist
	 */
	public GenericEntity remove(T owner, S child, String instance, String relation) throws TechnicalNotFoundException {
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
