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
    request.headers['Content-Type'] = 'application/json; charset=UTF-8';
    handle._client(request).then(cb).otherwise(err);
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
        prop : function(handle, id, prop, value, cb, cberr) {
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
