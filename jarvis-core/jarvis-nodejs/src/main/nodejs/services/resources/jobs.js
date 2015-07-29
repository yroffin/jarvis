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
    logger.info('Create crontab entry for ', job.id, job.name, job.cronTime);
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
            neo4jdb.cron.update(that.name, that.cronTime, that.plugin, that.params, that.timestamp, true, function() {
                /**
                 * no callback
                 */
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
 * job resource
 * @type {{get: Function, head: Function, put: Function, patch: Function, delete: Function}}
 * @private
 */
var _job = {
    /**
     * find it by id
     * @param jobId
     * @param callback
     */
    getById: function (jobId, callback) {
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
            else callback(undefined);
        });
    },
    /**
     * find it by name
     * @param jobName
     * @param callback
     */
    getByName: function (jobName, callback) {
        neo4jdb.raw.cypher('crontab', {filter: "n.job = '" + jobName + "'"}, function (existingJobs) {
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
     * update (or create) a new job
     * @param job
     * @param callback
     */
    put: function (job, callback) {
        /**
         * raw cypher query to find any existing job with this name
         */
        neo4jdb.raw.cypher('crontab', {filter: "n.name = '" + job.name + "'"}, function (existingJobs) {
            var existingJob = existingJobs[0];
            if (existingJob) {
                /**
                 * job exist just update it
                 *
                 * TODO
                 * synchronize this new version with active crontab
                 */
                neo4jdb.cron.update(job.name, job.cronTime, job.plugin, job.params, new Date(), false, function (response) {
                    callback(response);
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
     * patch resource
     * @param id
     * @returns {{}}
     */
    patch: function (id, body, callback) {
        this.getById(id, function(job) {
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
                    if(cronJobs[job.id]) {
                        /**
                         * job exist and is deployed
                         * stop it first
                         */
                        cronJobs[job.id].stop();
                        createCrontabEntry(job, function(updatedJob) {
                            callback(updatedJob);
                        });
                    } else {
                        /**
                         * new deployment
                         */
                        createCrontabEntry(job, function(updatedJob) {
                            callback(updatedJob);
                        });
                    }
                } else {
                    /**
                     * clean any crontab entry
                     */
                    if(cronJobs[job.id]) {
                        cronJobs[job.id].stop();
                        delete cronJobs[job.id];
                    }
                    job.started = false;
                    neo4jdb.cron.update(job.name, job.cronTime, job.plugin, job.params, job.timestamp, false, function(updatedJob) {
                        callback(updatedJob);
                    });
                }
            }
        });
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
    }
}

/**
 * jobs resource
 * @type {{get: Function, head: Function, post: Function, delete: Function}}
 * @private
 */
var _jobs = {
    /**
     * get all jobs
     * @param callback
     */
    get : function(callback) {
        neo4jdb.cron.get(undefined, function(crons) {
            callback(crons);
        });
    },
    head : function(req, res) {
        neo4jdb.cron.get(undefined, function(crons) {
            callback(crons);
        });
    },
    post : function(req, res) {
        return [];
    },
    delete : function(req, res) {
        return [];
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
         * start jobs
         *
         * @param callback
         */
        start: function (callback) {
            var jobs = neo4jdb.cron.get(undefined, function(jobs) {
                /**
                 * iterate on jobs
                 */
                for (index in jobs) {
                    var job = jobs[index];
                    if (!job.started) {
                        try {
                            /**
                             * set status to started, update job and create
                             * associated entry
                             */
                            neo4jdb.cron.update(job.name, job.cronTime, job.plugin, job.params, new Date(), true,
                                function(entity) {
                                    createCrontabEntry(entity, callback);
                                });
                        } catch (e) {
                            /**
                             * mark job in error (not started)
                             */
                            neo4jdb.cron.update(job.name, job.cronTime, job.plugin, job.params, new Date(), false, function() {
                                /**
                                 * handle error
                                 */
                            });
                            logger.warn('' + e);
                        }
                    }
                }
            });
        }
    },
    job : {
        /**
         * get this resource
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
             */
            if(req.params.id) {
                _job.getById(req.params.id, callback);
            } else {
                if(req.query.name) {
                    _job.getByName(req.query.name, callback);
                }
            }
        },
        head : function(req, res) {
            /**
             * no HEAD handle by REST api
             */
        },
        put : function(req, res) {
            _job.put(req.body, function(_result) {
                res.json(_result);
            });
        },
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
            _job.patch(req.params.id, req.body, callback);
        },
        /**
         * delete this resource
         * @param req
         * @param res
         */
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
                _result = _job.deleteById(req.params.id, callback);
            } else {
                if(req.query.name) {
                    _result = _job.deleteByName(req.query.name, callback);
                }
            }
        }
    },
    jobs : {
        get : function(req, res) {
            _jobs.get(function(_result) {
                res.json(_result);
            });
        },
        head : function(req, res) {
            var _result = _jobs.head();
            return res.json(_result);
        },
        post : function(req, res) {
            var _result = _jobs.post();
            return res.json(_result);
        },
        delete : function(req, res) {
            var _result = _jobs.delete();
            return res.json(_result);
        }
    }
}
