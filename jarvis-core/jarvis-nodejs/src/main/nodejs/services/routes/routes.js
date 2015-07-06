/**
 * Copyright 2014 Yannick Roffin.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var logger = require('blammo').LoggerFactory.getLogger('kernel');
var config = require(__dirname + '/../json/config');
var interact = require(__dirname + '/../json/interact');
var neo4j = require(__dirname + '/../json/neo4jdb');

/**
 * initialise all routes
 */
exports.init = function(app) {
	logger.info('Store routes configuration');
	/**
	 * configuration services
	 */
	app.get('/services/info', config.info);
	app.get('/services/info/:key', config.info);
	/**
	 * interactions services
	 */
	app.get('/services/send', interact.send);
	/**
	 * collections services
	 */
	app.get('/services/neo4j/:key', neo4j.collections);
	app.get('/services/neo4j/crud/:name/count', neo4j.collectionCount);
	app.get('/services/neo4j/crud/:name/page', neo4j.collectionPages);
	/**
	 * cron plugin
	 */
	app.get('/services/neo4j/crontab/:plugin/create', neo4j.cronPlugin);
	app.post('/services/neo4j/crontab/:plugin/create', neo4j.cronPlugin);
	app.get('/services/neo4j/crontab/:plugin/test', neo4j.cronTestPlugin);
	app.post('/services/neo4j/crontab/:plugin/test', neo4j.cronTestPlugin);
	app.get('/services/neo4j/crontabs/list', neo4j.cronList);
	return;
};
