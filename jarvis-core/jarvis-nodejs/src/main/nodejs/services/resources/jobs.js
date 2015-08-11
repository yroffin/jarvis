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

var cron = require('cron');
var kernel = require(__dirname + '/../core/kernel');
var neo4jdb = require(__dirname + '/../core/neo4jdb');

var cronJobs = {}

/**
 * create a new crontab job
 * @param job
 */
var createCrontabEntry = function (job, callback) {
    /**
     * fork this jobs if not started
     */
    logger.info('Create crontab entry for ', job.id, job.name, job.cronTime, job.started);
    /**
     * job exist and is deployed
     * stop it first
     */
    if(cronJobs[job.id]) {
        logger.info('Stop running instance for ', job.id, job.name, job.cronTime, job.started);
        cronJobs[job.id].stop();
        delete cronJobs[job.id];
    }
    neo4jdb.cron.update(job.id, job.name, job.cronTime, job.plugin, job.params, new Date(), true, function(job) {
        /**
         * nothing to do
         */
    });
    /**
     * create entry
     * @type {*|CronJob}
     */
    cronJobs[job.id] = new cron.CronJob({
        cronTime: job.cronTime,
        onTick: function () {
            /**
             * recover last version of job from database
             */
            logger.info('Activate with cron ' + job.name);
            var that = this;
            that.id = job.id;
            that.name = job.name;
            that.started = true;
            that.timestamp = new Date();
            that.plugin = job.plugin;
            that.params = job.params;
            neo4jdb.cron.update(that.id, that.name, that.cronTime, that.plugin, that.params, new Date(), true, function(job) {
                /**
                 * execute script
                 */
                kernel.xmppcliForkScript(job);
            });
        },
        start: false,
        timeZone: "Europe/Paris",
        context: job
    });
    cronJobs[job.id].start();
    job.started = true;
    callback(job);
}

/**
 * create a new crontab job
 * @param job
 */
var removeCrontabEntry = function (job, callback) {
    logger.info('Remove crontab entry for ', job.id, job.name, job.cronTime, job.started);
    /**
     * clean any crontab entry
     */
    if(cronJobs[job.id]) {
        cronJobs[job.id].stop();
        delete cronJobs[job.id];
    }
    job.started = false;
    neo4jdb.cron.update(job.id, job.name, job.cronTime, job.plugin, job.params, job.timestamp, false, function(updatedJob) {
        callback(updatedJob);
    });
}

var _crontabs = {
    /**
     * get all jobs
     * @param callback
     */
    get : function(callback) {
        var filtered = [];
        for(var key in cronJobs) {
            filtered.push({
                key : key,
                context : cronJobs[key].context,
                cronTime : cronJobs[key].cronTime,
                running : cronJobs[key].running
            });
        }
        callback(filtered);
    },
    /**
     * get by id
     * @param id
     * @param callback
     */
    getById: function (id, callback) {
        var result = {};
        for(var key in cronJobs) {
            if(key+'' == id+'') {
                result = {
                    context : cronJobs[key].context,
                    cronTime : cronJobs[key].cronTime,
                    running : cronJobs[key].running
                }
            }
        }
        callback(result);
    }
}

/**
 * jobs resource
 *
 * @type {
 *  {
 *  put: Function,
 *  post: Function,
 *  deleteById: Function,
 *  deleteByName: Function,
 *  getById: Function,
 *  getByName: Function,
 *  get: Function,
 *  head: Function,
 *  post: Function,
 *  delete: Function,
 *  patch: Function,
 *  test: Function}}
 * @private
 */
var _jobs = {
    /**
     * update existing job
     * @param job
     * @param callback
     */
    put: function (job, callback) {
        /**
         * raw cypher query to find any existing job with this name
         * or its id if defined
         */
        var filter;
        if(job.id) {
            filter = {filter: "id(n) = " + job.id};
        } else {
            filter = {filter: "n.name = '" + job.name + "'"};
        }
        neo4jdb.raw.cypher('crontab', filter, function (existingJobs) {
            var existingJob = existingJobs[0];
            if (existingJob) {
                /**
                 * job exist just update it
                 * synchronize this new version with active crontab
                 */
                var that = job;
                neo4jdb.cron.update(job.id, job.name, job.cronTime, job.plugin, job.params, new Date(), false, function (response) {
                    if(job.started != undefined) {
                        /**
                         * handle crontab status
                         */
                        _jobs.patch(that.id, {started: that.started}, callback);
                    } else {
                        callback(response);
                    }
                });
            } else {
                /**
                 * create a new one
                 */
                neo4jdb.cron.create(job.name, job.cronTime, job.plugin, job.params, function (response) {
                    callback(response);
                });
            }
        });
    },
    /**
     * post resource
     *
     * @param body
     * @param callback
     */
    post: function (body, callback) {
        if(body.id) {
            delete body.id;
        }
        /**
         * create this object
         */
        neo4jdb.cron.create(body.name, body.cronTime, body.plugin, body.params, callback);
    },
    /**
     * delete resource by id
     * @param id
     * @param callback
     */
    deleteById: function (id, callback) {
        neo4jdb.cron.deleteById(id, function (response) {
            callback(response);
        });
    },
    /**
     * delete resource by name
     * @param id
     * @param callback
     */
    deleteByName: function (name, callback) {
        neo4jdb.cron.deleteByName(name, function (response) {
            callback(response);
        });
    },
    /**
     * find it by id
     * @param jobId
     * @param callback
     */
    getById: function (jobId, callback, notfound) {
        neo4jdb.raw.cypher('crontab', {filter: "id(n) = " + jobId + ""}, function (existingJobs) {
            var job = existingJobs[0];
            if (job) {
                neo4jdb.raw.relationship(jobId, 'params', 0, function (params) {
                    job.params = params;
                    /**
                     * produce result
                     */
                    callback(job);
                });
            }
            else notfound();
        }, notfound);
    },
    /**
     * find it by name
     * @param jobName
     * @param callback
     */
    getByName: function (jobName, callback) {
        neo4jdb.raw.cypher('crontab', {filter: "n.name = '" + jobName + "'"}, function (existingJobs) {
            var job = existingJobs[0];
            if (job) {
                neo4jdb.raw.relationship(job.id, 'params', 0, function (params) {
                    job.params = params;
                    /**
                     * produce result
                     */
                    callback(job);
                });
            }
            else callback(undefined);
        });
    },
    /**
     * get all jobs
     * @param callback
     */
    get : function(callback) {
        neo4jdb.cron.get(undefined, function(crons) {
            callback(crons);
        });
    },
    /**
     * patch resource
     *
     * @param id
     * @param body
     * @param callback
     */
    patch: function (id, body, callback) {
        _jobs.getById(id, function(job) {
            /**
             * patch it
             */
            delete job.job;
            if(body.cronTime != undefined) {
                job.cronTime = body.cronTime;
            }
            if(body.plugin != undefined) {
                job.plugin = body.plugin;
            }
            if(body.name != undefined) {
                job.name = body.name;
            }
            if(body.params != undefined) {
                job.params = body.params;
            }
            /**
             * and modify status
             */
            if(body.started != undefined) {
                if(body.started) {
                    /**
                     * create, or re-create entry
                     */
                    createCrontabEntry(job, function(updatedJob) {
                        callback(updatedJob);
                    });
                } else {
                    /**
                     * remove any crontab entry
                     */
                    removeCrontabEntry(job, function(updatedJob) {
                        callback(updatedJob);
                    });
                }
            }
        }, callback);
    },
    /**
     * test resource
     *
     * @param id
     * @param body
     * @param callback
     */
    test: function (id, callback) {
        _jobs.getById(id, function(job) {
            /**
             * execute script
             */
            kernel.xmppcliForkScript(job);
            /**
             * callback notifications
             */
            callback(job);
        }, callback);
    }
}

/**
 * jobs restfull api
 * @type {
 *  {job: {get: Function, head: Function, put: Function, patch: Function, delete: Function},
 *  jobs: {get: Function, head: Function, post: Function, delete: Function}}
 * }
 */
module.exports = {
    services: {
        /**
         * clear started status in current database set all them to not started
         */
        clear: function () {
            /**
             * retrieve active jobs
             */
            var jobs = neo4jdb.cron.get(undefined, function(jobs) {
                /**
                 * iterate on jobs to clear database status
                 */
                for (index in jobs) {
                    var job = jobs[index];
                    neo4jdb.cron.update(job.name, job.cronTime, job.plugin, job.params, new Date(), false, function(){
                        /**
                         * null callback
                         */
                    });
                }
            });
        },
        /**
         * find all jobs
         * @param callback
         */
        start : function(callback) {
            _jobs.get(function(jobs) {
                var index = 0;
                for(;index < jobs.length; index ++) {
                    var job = jobs[index];
                    /**
                     * patch resource
                     */
                    if(job.started) {
                        logger.info('[JOB] starting ', job.id, job.name, job.cronTime, job.started);
                        _jobs.patch(job.id, {started: true}, callback);
                    } else {
                        logger.info('[JOB] ignored ', job.id, job.name, job.cronTime, job.started);
                    }
                }
            });
        },
    },
    crontabs : {
        /**
         * get all internal crontab (not neo4jdb but in memory crontab)
         * @param req
         * @param res
         */
        get : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * core get functions
             * if any id or name then it's a single resource request
             * else recover all exiting resources
             */
            if(req.params.id) {
                _crontabs.getById(req.params.id, callback);
            } else {
                _crontabs.get(callback);
            }
        },
    },
    job : {
        /**
         * path resource
         * @param req
         * @param res
         * @returns {ServerResponse|*}
         */
        patch : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * patch resource
             */
            _jobs.patch(req.params.id, req.body, callback);
        }
    },
    jobs : {
        put : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            _jobs.put(req.body, callback);
        },
        /**
         * get all jobs
         * @param req
         * @param res
         */
        get : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * core get functions
             * if any id or name then it's a single resource request
             * else recover all exiting resources
             */
            if(req.params.id) {
                _jobs.getById(req.params.id, callback, callback);
            } else {
                if (req.query.name) {
                    _jobs.getByName(req.query.name, callback, callback);
                } else {
                    _jobs.get(callback);
                }
            }
        },
        /**
         * head on jobs
         * @param req
         * @param res
         */
        execute : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * core get functions
             */
            var _result;
            if(req.query.id && req.query.method) {
                if(req.query.method == 'test') {
                    _result = _jobs.test(req.query.id, callback);
                    return;
                }
                return res.status(400).json({});
            } else {
                return res.status(400).json({});
            }
        },
        post : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            _jobs.post(req.body, callback);
        },
        delete : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * core get functions
             */
            var _result;
            if(req.params.id) {
                _result = _jobs.deleteById(req.params.id, callback);
            } else {
                if(req.query.name) {
                    _result = _jobs.deleteByName(req.query.name, callback);
                }
            }
        },
        patch : function(req, res) {
            /**
             * simple call back to handle result
             * @param _result
             * @returns {*}
             */
            function callback(_result) {
                if(!_result) {
                    return res.status(404).json({});
                } else return res.json(_result);
            }

            /**
             * core get functions
             */
            var _result;
            if(req.params.id) {
                _result = _jobs.patch(req.params.id, req.body, callback);
            } else {
                return res.status(400).json({});
            }
        }
    }
}
