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
 * neo4j driver based on api rest
 */
var rest = require('rest');
var mime = require('rest/interceptor/mime');

/**
 * node create
 */
var restCall = function(handle, node, path, method, cb, err) {
    /**
     * build a default rest request
     * @type {{entity: *, path: *, method: *, headers: {}}}
     */
    var request = {
        entity: node,
        path: handle._url + path,
        method: method,
        headers: {} };
    var res = rest.wrap(mime, { mime: 'application/json' })(request).done(cb,err);
}

/**
 * public method
 * @type {{init: Function, cypher: {query: Function}, node: {create: Function, prop: Function}, relationship: {create: Function}}}
 */
module.exports = {
    /**
     * init driver
     * @param url
     */
    init : function(url) {
        return {
            _client: require('rest').wrap(require('rest/interceptor/mime')),
            _url: url
        }
    },
    cypher:{
        /**
         * node create
         * http://neo4j.com/docs/2.2.2/rest-api-transactional.html#rest-api-begin-and-commit-a-transaction-in-one-request
         *
         * @param handle
         * @param statement
         * @param cb
         * @param cberr
         */
        query: function (handle, statement, cb, cberr) {
            restCall(
                handle,
                {
                    "statements" : [ {
                        "statement" : statement
                    } ]
                },
                '/db/data/transaction/commit',
                'POST',
                function (response) {
                    cb(response.entity.results);
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
    },
    node : {
        /**
         * node create
         * Cf. http://neo4j.com/docs/2.2.2/rest-api-nodes.html#rest-api-create-node
         */
        create: function (handle, node, cb, cberr) {
            restCall(
                handle,
                node,
                '/db/data/node',
                'POST',
                function (response) {
                    cb(response.entity.metadata);
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * get node
         * http://neo4j.com/docs/2.2.2/rest-api-nodes.html#rest-api-get-node
         */
        get: function (handle, id, cb, cberr) {
            restCall(
                handle,
                {},
                '/db/data/node/' + id,
                'GET',
                function (response) {
                    cb(response.entity.metadata, response.entity.data);
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * label add
         * http://neo4j.com/docs/2.2.2/rest-api-node-labels.html#rest-api-adding-a-label-to-a-node
         *
         * @param handle
         * @param id
         * @param prop
         * @param value
         * @param cb
         * @param cberr
         */
        label : function(handle, id, value, cb, cberr) {
            restCall(
                handle,
                value,
                '/db/data/node/'+id+'/labels',
                'POST',
                function(response) {
                    if(response.entity.errors) {
                        if(cberr) cberr(response.entity.errors);
                        else throw response.entity.errors;
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * degrees list
         * http://neo4j.com/docs/2.2.2/rest-api-node-degree.html#rest-api-get-the-degree-of-a-node
         *
         * @param handle
         * @param id
         * @param cb
         * @param cberr
         */
        degrees : function(handle, id, cb, cberr) {
            restCall(
                handle,
                {},
                '/db/data/node/'+id+'/degree/all',
                'GET',
                function(response) {
                    if(response.entity.errors) {
                        if(cberr) cberr(response.entity.errors);
                        else throw response.entity.errors;
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * degrees out
         * http://neo4j.com/docs/2.2.2/rest-api-node-degree.html#rest-api-get-the-degree-of-a-node-by-direction
         *
         * @param handle
         * @param id
         * @param cb
         * @param cberr
         */
        degreesOut : function(handle, id, cb, cberr) {
            restCall(
                handle,
                {},
                '/db/data/node/'+id+'/degree/out',
                'GET',
                function(response) {
                    if(response.entity.errors) {
                        if(cberr) cberr(response.entity.errors);
                        else throw response.entity.errors;
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * degree
         * http://neo4j.com/docs/2.2.2/rest-api-node-degree.html#rest-api-get-the-degree-of-a-node-by-direction-and-types
         *
         * @param handle
         * @param id
         * @param name
         * @param cb
         * @param cberr
         */
        degree : function(handle, id, name, cb, cberr) {
            restCall(
                handle,
                {},
                '/db/data/node/'+id+'/degree/out/' + name,
                'GET',
                function(response) {
                    if(response.entity.errors) {
                        if(cberr) cberr(response.entity.errors);
                        else throw response.entity.errors;
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * get typed relationships
         * http://neo4j.com/docs/2.2.2/rest-api-relationships.html#rest-api-get-typed-relationships
         *
         * @param handle
         * @param id
         * @param name
         * @param cb
         * @param cberr
         */
        relationships : function(handle, id, name, cb, cberr) {
            restCall(
                handle,
                {},
                '/db/data/node/'+id+'/relationships/out/' + name,
                'GET',
                function(response) {
                    if(response.entity.errors) {
                        if(cberr) cberr(response.entity.errors);
                        else throw response.entity.errors;
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * update properties
         * Cf. http://neo4j.com/docs/2.2.2/rest-api-node-properties.html#rest-api-update-node-properties
         *
         * @param handle
         * @param id
         * @param properties
         * @param cb
         * @param cberr
         */
        update : function(handle, id, properties, cb, cberr) {
            restCall(
                handle,
                properties,
                '/db/data/node/'+id+'/properties',
                'PUT',
                function(response) {
                    if(response.entity.errors) {
                        cberr(response.entity.errors);
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        },
        /**
         * property add
         * Cf. http://neo4j.com/docs/2.2.2/rest-api-node-properties.html#rest-api-set-property-on-node
         *
         * @param handle
         * @param id
         * @param prop
         * @param value
         * @param cb
         * @param cberr
         */
        properties : function(handle, id, prop, value, cb, cberr) {
            restCall(
                handle,
                value,
                '/db/data/node/'+id+'/properties/' + prop,
                'PUT',
                function(response) {
                    if(response.entity.errors) {
                        cberr(response.entity.errors);
                    } else {
                        if(cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        }
    },
    relationship: {
        /**
         * relationship create
         * Cf. http://neo4j.com/docs/2.2.2/rest-api-relationships.html#rest-api-create-relationship
         *
         * @param handle, handle of neo4j client
         * @param from, node id
         * @param to, node id
         * @param type, relationship name
         * @param cb, callback
         * @param cberr, callback error
         */
        create: function (handle, from, to, type, cb, cberr) {
            restCall(
                handle,
                {
                    to: "/db/data/node/" + to,
                    type: type
                },
                '/db/data/node/' + from + '/relationships',
                'POST',
                function (response) {
                    if (response.entity.errors) {
                        cberr(response.entity.errors);
                    } else {
                        if (cb) {
                            cb(response.entity);
                        }
                    }
                },
                function (err) {
                    if(cberr) cberr(err);
                    else throw err;
                }
            );
        }
    }
}
