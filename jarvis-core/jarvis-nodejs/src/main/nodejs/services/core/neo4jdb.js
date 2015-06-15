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

/**
 * native mongo driver for raw operation
 */
var neo4j = require('neo4j');
var _db_jarvis = undefined;

/**
 * retrieve all collections stored in mongodb
 */
var init = function(jarvisUrl) {
	/**
	 * main database
	 */
	_db_jarvis = new neo4j.GraphDatabase(jarvisUrl);

	_db_jarvis.cypher({
		query : 'MATCH (n) RETURN n LIMIT 1',
		params : {},
	}, function(err, results) {
		if (err)
			throw err;
		var result = results[0];
		if (!result) {
			logger.error('No row found.');
		} else {
			var user = result['u'];
			logger.debug("Neo4j test : ", JSON.stringify(result, null, 4));
		}
	});
}

/**
 * exports
 */
module.exports = {
	init : init,
}
