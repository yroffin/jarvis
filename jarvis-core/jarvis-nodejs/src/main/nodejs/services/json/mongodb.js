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

var logger = require('blammo').LoggerFactory.getLogger('services');

var mongo = require(__dirname + '/../core/mongodb');
var kernel = require(__dirname + '/../core/kernel');

exports.init = function() {
	return;
};

/**
 * info services
 */
exports.collections = function(req, res) {
	/**
	 * collections
	 */
	logger.info('info() key [%s]', req.params.key);
	if (req.params.key == 'collections') {
		if (req.params.operation == undefined) {
			res.json(mongo.getSyncCollections());
			return;
		}
		return;
	}
	if (req.params.key == 'crontab') {
		if (req.params.operation == undefined) {
			res.json(mongo.getSyncCollections());
			return;
		}
		return;
	}
	res.json({});
};

/**
 * crud operation for collections
 */
exports.collectionCount = function(req, res) {
	logger.info('collectionCrud() database [%s]', req.params.database);
	logger.info('collectionCrud() name [%s]', req.params.name);
	logger.info('collectionCrud() operation [%s]', req.params.operation);

	/**
	 * count
	 */
	res.json(mongo.syncCountCollectionByName(req.params.database, req.params.name));
};

/**
 * crud operation for collections
 */
exports.collectionPages = function(req, res) {
	logger.info('collectionPages() request', req.query);
	logger.info('collectionPages() params', req.params);

	/**
	 * count
	 */
	res.json(mongo.syncPageCollectionByName(req.params.database, req.params.name, req.query.offset, req.query.page));
};

/**
 * crud operation for crontab
 */
exports.cronPlugin = function(req, res) {
	logger.info('cronPlugin() request', req.query);
	logger.info('cronPlugin() params', req.params);

	/**
	 * job, cronTime, plugin, params
	 */
	res.json(mongo.syncCronCreate(req.query.job, req.query.cronTime, req.params.plugin, JSON.parse(req.query.params)));
}

/**
 * crud operation for crontab
 */
exports.cronTestPlugin = function(req, res) {
	logger.info('cronPlugin() request', req.query);
	logger.info('cronPlugin() params', req.params);

	/**
	 * job, cronTime, plugin, params
	 */
	var job = mongo.syncCronList({
		job : req.query.job
	})[0];
	logger.error(kernel.xmppcliForkScript(job));
	res.json(job);
}

/**
 * crud operation for crontab
 */
exports.cronList = function(req, res) {
	logger.info('cronList() request', req.query);
	logger.info('cronList() params', req.params);

	/**
	 * job, cronTime, plugin, params
	 */
	res.json(mongo.syncCronList(req.query.filter));
};
