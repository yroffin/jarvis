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

var jobs = require(__dirname + '/../resources/jobs');
var neo4j = require(__dirname + '/../resources/neo4j');
var clients = require(__dirname + '/../resources/clients');
var connectors = require(__dirname + '/../resources/connectors');

/**
 * initialise all routes
 */
exports.init = function(app) {
	logger.info('Store routes configuration');
	/**
	 * jobs resource
	 * Method	Scope	Semantics
	 * GET	collection	Retrieve all resources in a collection
	 * GET	resource	Retrieve a single resource (with filter on field)
	 * POST	collection	Create a new resource in a collection
	 * PUT	resource	Update a resource
	 * PATCH	resource	Update a resource
	 * DELETE	resource	Delete a resource
	 */
	app.get('/api/jobs', jobs.jobs.get);
	app.get('/api/jobs/:id', jobs.jobs.get);
	app.put('/api/jobs/:id', jobs.jobs.put);
	app.post('/api/jobs', jobs.jobs.post);
	app.delete('/api/jobs/:id', jobs.jobs.delete);
	app.delete('/api/jobs', jobs.jobs.delete);
	app.patch('/api/jobs/:id', jobs.jobs.patch);
	app.post('/api/jobs/execute', jobs.jobs.execute);
	/**
	 * configuration
	 * - crontab
	 * - neo4j
	 *   - labels
	 * Method	Scope	Semantics
	 * GET	collection	Retrieve all resources in a collection
	 * GET	resource	Retrieve a single resource (with filter on field)
	 */
	app.get('/api/configurations', jobs.jobs.get);
	app.get('/api/configurations/crontabs', jobs.crontabs.get);
	app.get('/api/configurations/crontabs/:id', jobs.crontabs.get);
	app.get('/api/configurations/neo4j', neo4j.get);
	app.get('/api/configurations/neo4j/labels', neo4j.labels.get);
	app.get('/api/configurations/neo4j/labels/:id', neo4j.labels.get);
	app.get('/api/configurations/clients', clients.listener.get);
	app.get('/api/configurations/clients/:id', clients.listener.get);
	/**
	 * connectors
	 * Method	Scope	Semantics
	 * GET	collection	Retrieve all resources in a collection
	 * GET	resource	Retrieve a single resource (with filter on field)
	 * POST	collection	Create a new resource in a collection
	 */
	app.get('/api/connectors', connectors.resources.get);
	app.get('/api/connectors/:id', connectors.resources.get);
	app.post('/api/connectors/task', connectors.services.task);
	return;
};
