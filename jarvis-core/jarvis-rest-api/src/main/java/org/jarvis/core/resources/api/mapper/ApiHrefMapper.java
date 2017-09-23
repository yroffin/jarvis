package org.jarvis.core.resources.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.common.neo4j.client.Entities;
import org.common.neo4j.client.Node;
import org.common.neo4j.client.Transaction;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * api mapper based on href link
 *
 * @param <OWNER>
 * @param <TARGET>
 */
public abstract class ApiHrefMapper<OWNER extends GenericBean,TARGET extends GenericBean> extends ApiMapper {

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

	/**
	 * must be implemented
	 */
	public void mount() {
		throw new TechnicalException("Not implemented");
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
	public GenericEntity add(OWNER owner, TARGET child, GenericMap properties, String type, String relation) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			/**
			 * relation can be override by post
			 */
    		String defaultRelType = relation;
    		if(properties.get(HREF.toLowerCase()) != null) {
    			defaultRelType = (String) properties.get(HREF.toLowerCase());
    		}
			Entities result = apiNeo4Service.cypherAddLink(ownerLabel, owner.id, childLabel, child.id, defaultRelType, "relation");
			create.success();
			if(result.hasNext()) {
				Map<String, Object> rows = result.next();
				/**
				 * set node properties
				 */
				update(rows.get("id(relation)")+"", properties);
				GenericEntity genericEntity = new GenericEntity();
				genericEntity.id = child.id;
				genericEntity.instance = rows.get("id(relation)")+"";
				genericEntity.href = "/api/"+type+"/" + genericEntity.id;
				/**
				 * set relationship properties
				 */
				for(Entry<String, Object> property : properties.entrySet()) {
					if(String.class == property.getValue().getClass()) {
						genericEntity.put(property.getKey(), (String) property.getValue());
					}
				}
				return genericEntity;
			}
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
		throw new TechnicalNotFoundException(owner.id);
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
			Node toUpdate = new Node(properties);
			toUpdate.setId(relId);
			apiNeo4Service.updateRelationship(toUpdate);
			update.success();
			return properties;
		} catch (Exception e) {
			throw new TechnicalNotFoundException(relId);
		}
	}

	/**
	 * find all element filtered by the relation type
	 * @param owner
	 * the owner entity
	 * @param relation
	 * the relation type (default HREF)
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(OWNER owner, String relation) {
		Entities result = apiNeo4Service.cypherAllLink(ownerLabel, owner.id, childLabel, relation, "relation", "entity");
		List<GenericEntity> resultset = new ArrayList<GenericEntity>();
		while (result.hasNext()) {
			GenericEntity genericEntity = new GenericEntity();
			Map<String, Object> fields = result.next();
			/**
			 * get optional properties on relationship
			 */
			Node r = (Node) fields.get("relation");
			try (Transaction find = apiNeo4Service.beginTx()) {
				for(Entry<String, Object> property : r.getAllProperties().entrySet()) {
					if(String.class == property.getValue().getClass()) {
						genericEntity.put(property.getKey(), (String) property.getValue());					
					}
				}
				find.success();
			} catch (Exception e) {
				throw new TechnicalException(e);
			}
			/**
			 * read id
			 */
			genericEntity.id = ((Node) fields.get("entity")).getId()+"";
			genericEntity.instance = ((Node) fields.get("relation")).getId()+"";
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
	public GenericEntity remove(OWNER owner, TARGET child, String instance, String relation) throws TechnicalNotFoundException {
		try (Transaction create = apiNeo4Service.beginTx()) {
			apiNeo4Service.cypherDeleteLink(ownerLabel, owner.id, childLabel, child.id, relation, instance, "relation");
			create.success();
			GenericEntity genericEntity = new GenericEntity();
			genericEntity.id = child.id;
			genericEntity.instance = instance;
			genericEntity.href = "/api/"+type+"/" + genericEntity.id;
			return genericEntity;
		} catch (Exception e) {
			throw new TechnicalNotFoundException(owner.id);
		}
	}
}
