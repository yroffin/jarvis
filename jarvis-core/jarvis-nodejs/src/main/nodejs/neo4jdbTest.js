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

process.stdout.write("hello: ");

//var logger = require('blammo').LoggerFactory.getLogger('kernel');
var neo4j = require(__dirname + '/services/core/neo4jdb');

neo4j.init('http://neo4j:123456@localhost:7474', true);

neo4j.syncCronCreate('job #1','* * * * ', 'date', {a:2});
neo4j.syncStoreInCollectionByName('blob',{
	tab:[0,{a:2},2,3,new Date(),5],
	username : 'yroffin',
	firstname : new Date(),
	lastname : 'roffin',
	profil : {
		option : 'a',
		option1 : {
			value : 'xxx',
			option1 : {
				value : 'xxx',
				value1 : 'xxx',
				value2 : 'xxx'
			},
		},
		option2 : {
			value : 'yyy'
		}
	},
	adress : {
		street : '37',
		code : '35440'
	}
});

neo4j.syncPageCollectionByName('crontab',0,100);