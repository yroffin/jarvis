/** 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * logging
 */
var logger = require('blammo').LoggerFactory.getLogger('neo4j');

var deasync = require('deasync');
var EventEmitter = require('events').EventEmitter;

/**
 * native mongo driver for raw operation
 */
var neo4jDriver = require('./neo4j-driver');
var _neo4j_driver = undefined;

/**
 * check if it's a field
 * @param key
 * @param val
 * @returns {boolean}
 */
var isField = function(key, val) {
	return typeof (val) != 'object' || val == null || val instanceof Date;
}

/**
 * object visitor
 * @param visited
 * @param node
 * @param fn
 * @returns {*}
 */
var objectVisitor = function(visited, filled, keys, postCreate, ownerId, label) {
	var len = keys.length;
	/**
	 * first iterate on all plain field such as string, number etc ...
	 */
	for(var index = 0;index < len;index++) {
		var key = keys[index];
		var val = visited[key];
		if (isField(key,val)) {
			/**
			 * new field detection
			 */
			var realValue = val;
			if(realValue == null) {
				realValue = undefined;
			}
			filled[key] = realValue;
		}
		var i = 0;
	}
	/**
	 * entity is now filled, we can create it on neo4j
	 */
	neo4jDriver.node.create(
		_neo4j_driver,
		filled,
		/**
		 * call back for first new entity
		 * @param filledEntity, fresh neo4j entity created
		 */
		function(filledEntity) {
			/**
			 * postCreate is used to produce relationship callback
			 */
			if(postCreate) {
				postCreate(filledEntity.id, ownerId, label);
			}
			/**
			 * now iterate on each sub object for recursive creation
			 */
			for(var index = 0;index < len;index++) {
				var key = keys[index];
				var val = visited[key];
				if (!isField(key,val)) {
					/**
					 * recursive call
					 */
					objectVisitor(val, {}, Object.keys(val),
						/**
						 * callback function to produce relationship
						 * @param ownerId
						 * @param entityId
						 * @param label
						 */
						function(ownerId, entityId, label) {
							neo4jDriver.relationship.create(
								_neo4j_driver,
								entityId,
								ownerId,
								label
							);
						},
						filledEntity.id,
						key
					);
				}
			}
		}
	);
}

/**
 * find target entity by relationship
 * @param id
 * @param type
 * @param cb
 */
var findRelationshipEnd = function(ctx, id, idx, type, cb) {
	/**
	 * find relationship
	 */
	neo4jDriver.node.relationships(_neo4j_driver, id, type,
		function (entity) {
			/**
			 * assume only one params by node
			 */
			var lastIndex = entity[idx].end.lastIndexOf('/');
			var endId = entity[idx].end.substr(lastIndex + 1);
			/**
			 * enrich with param entity
			 */
			neo4jDriver.node.get(_neo4j_driver, endId, ctx, cb);
		}
	);
}

/**
 * public method
 *
 * @type {{init: Function, raw: {relationship: Function, cypher: Function}, cron: {get: Function, update: Function, create: Function, delete: Function}, label: {get: Function, count: Function, store: Function}}}
 */
module.exports = {
	/**
	 * retrieve all collections stored in mongodb
	 */
	init: function (jarvisUrl, drop) {
		/**
		 * main database
		 */
		_neo4j_driver = neo4jDriver.init(jarvisUrl);
		if (drop) {
			/**
			 * just make a single select to test access
			 */
			neo4jDriver.cypher.query(_neo4j_driver, 'MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r',
				function (response) {
					logger.debug("Neo4j test : ", JSON.stringify(response));
				}
			);
		}
	},
	raw : {
		/**
		 * find any attached relationship
		 * @param id
		 * @param type
		 * @param idx
		 * @param callback
		 */
		relationship: function (id, type, idx, callback) {
			var ctx = {};

			/**
			 * find relationship
			 */
			neo4jDriver.node.relationships(_neo4j_driver, id, type,
				function (entities) {
					/**
					 * assume only one params by node
					 */
					var lastIndex = entities[idx].end.lastIndexOf('/');
					var endId = entities[idx].end.substr(lastIndex + 1);
					/**
					 * enrich with typed entity
					 */
					neo4jDriver.node.get(_neo4j_driver, endId, ctx, function (ctx, metadata, data) {
							/**
							 * return result
							 */
							callback(data);
						}
					)
				}
			)
		},
		/**
		 * filtered cypher query (generic)
		 * @param label
		 * @param filter
		 * @param cb
		 */
		cypher: function (label, filter, callback) {
			/**
			 * create filter if undefined
			 */
			if (!filter) {
				filter = {};
			}

			/**
			 * skip offset rows, warn to order by it correctly
			 * @type {string}
			 */
			var skip = '';
			if (filter.offset) {
				skip = 'SKIP ' + filter.offset;
			}

			/**
			 * limit resultset
			 * @type {string}
			 */
			var limit = '';
			if (filter.page) {
				limit = 'LIMIT ' + filter.page;
			}

			/**
			 * filter resultset
			 * @type {string}
			 */
			var where = '';
			if (filter.filter) {
				where = ' AND (' + filter.filter + ')';
			}

			/**
			 * query by labels
			 */
			var query = 'MATCH (n) WHERE labels(n) = [\'' + label + '\']  ' + where + ' RETURN n, id(n) ' + skip + ' ' + limit;
			logger.debug(query);
			neo4jDriver.cypher.query(_neo4j_driver, query,
				function (response) {
					/**
					 * TODO
					 * check this response[0]
					 */
					var resultset = response[0].data;
					var result = [];
					var len = resultset.length;
					for (var index = 0; index < len; index++) {
						result[index] = resultset[index].row[0];
						result[index].id = resultset[index].row[1];
					}
					/**
					 * produce result
					 */
					callback(result);
				},
				function (err) {
					throw err;
				}
			);
		}
	},
	cron : {
		/**
		 * query all crontab elements
		 * @param filter
		 * @param callback
		 */
		get: function (filter, callback) {
			/**
			 * get all crontab elements
			 */
			module.exports.raw.cypher('crontab', filter, function (crons) {
				var len = crons.length;
				if (len > 0) {
					/**
					 * params will be used to evaluate the end of param recover
					 * queries
					 */
					var params = len;
					for (var index = 0; index < len; index++) {
						findRelationshipEnd({
								index: index,
								crons: crons
							}, crons[index].id, 0, 'params',
							function (ctx, metadata, data) {
								/**
								 * assume only one params by node
								 */
								ctx.crons[ctx.index].params = data;
								params--;
								if (params == 0) {
									/**
									 * all is done, then callback now
									 */
									callback(crons);
								}
							}
						);
					}
				}
			});
		},
		/**
		 * update cron job, ex: register a new plugin for cron jobs
		 *
		 * @param job
		 * @param cronTime
		 * @param plugin
		 * @param params
		 * @param timestamp
		 * @param started
		 * @returns {*}
		 */
		update: function (job, cronTime, plugin, params, timestamp, started, callback) {
			/**
			 * find entity from database
			 */
			module.exports.raw.cypher('crontab', {filter: "n.job = '" + job + "'"}, function (existingJobs) {
				var cron = existingJobs[0];
				if (cron) {
					/**
					 * update this node
					 */
					neo4jDriver.node.update(_neo4j_driver, cron.id, {
							job: job,
							cronTime: cronTime,
							plugin: plugin,
							timestamp: timestamp,
							started: started
						},
						function (entity) {
							/**
							 * find params entity and update it
							 */
							findRelationshipEnd({}, cron.id, 0, 'params',
								function (ctx, metadata, data) {
									neo4jDriver.node.update(_neo4j_driver, metadata.id, params,
										function (relation) {
											var updated = entity;
											updated.params = relation;
											callback(updated);
										}
									);
								}
							);
						}
					);
				}
			});
		},
		/**
		 * create cron job
		 *
		 * @param job
		 * @param cronTime
		 * @param plugin
		 * @param params
		 * @param callback
		 */
		create: function (job, cronTime, plugin, params, callback) {
			var blob = {
				job: job,
				cronTime: cronTime,
				plugin: plugin,
				params: params,
				started: false,
				timestamp: new Date()
			};
			/**
			 * iterate on members
			 */
			objectVisitor(blob, {}, Object.keys(blob),
				/**
				 * post create function
				 * @param label
				 */
				function (nodeId) {
					neo4jDriver.node.label(_neo4j_driver, nodeId, 'crontab',
						function () {
							callback(true);
						});
				}
			);
		},
		/**
		 * delete crontab by name
		 * @param label
		 */
		deleteByName: function (name, callback) {
			/**
			 * query by labels
			 */
			neo4jDriver.cypher.query(_neo4j_driver, 'MATCH (n) WHERE n.job=\'' + name + '\' OPTIONAL MATCH (n)-[r]-() DELETE n,r', function (response) {
				callback(response);
			});
		},
		/**
		 * delete crontab by id
		 * @param id
		 * @param callback
		 */
		deleteById: function (id, callback) {
			/**
			 * query by labels
			 */
			neo4jDriver.cypher.query(_neo4j_driver, 'MATCH (n) WHERE id(n) =' + id + ' OPTIONAL MATCH (n)-[r]-() DELETE n,r', function (response) {
				callback(response);
			});
		}
	},
	label : {
		/**
		 * get collection count by label
		 * @param label
		 */
		get: function (callback) {
			/**
			 * query by labels
			 */
			neo4jDriver.cypher.query(_neo4j_driver, 'START n=node(*) RETURN distinct labels(n), count(*)',
				function (response) {
					var resultset = response[0].data;
					var result = [];
					var len = resultset.length;
					for(var index = 0;index < len;index++) {
						result[index] = {name:resultset[index].row[0][0], count:resultset[index].row[1]};
					}
					callback(result);
				}
			);
		},
		/**
		 * get collection count by label
		 * @param label name
		 */
		count: function (label, callback) {
			var syncCountCollectionByNameRes = {
				'result' : undefined
			};

			/**
			 * query by labels
			 */
			neo4jDriver.cypher.query(_neo4j_driver, 'MATCH (n) WHERE labels(n) = [\''+label+'\']  RETURN count(n)',
				function (response) {
					callback(response[0].data[0].row[0]);
				}
			);
		},
		/**
		 * store object
		 * @param blobEntity
		 */
		store: function (name, blob) {
			/**
			 * iterate on member
			 */
			objectVisitor(blob, {}, Object.keys(blob),
				/**
				 * post create function
				 * @param label
				 */
				function(nodeId) {
					neo4jDriver.node.label(_neo4j_driver, nodeId, name);
				}
			);
		}
	},
}
