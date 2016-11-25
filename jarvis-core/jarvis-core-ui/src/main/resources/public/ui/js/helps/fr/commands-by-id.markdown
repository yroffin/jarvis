### Actions

| Action                                 | Description               |
| -------------------------------------- |:-------------------------:|
| <i class="help-icons">clear</i>        | Retour au menu précédent  |
| <i class="help-icons">content_copy</i> | Dupliquer la ressource    |
| <i class="help-icons">delete</i>       | Supprimer la ressource    |
| <i class="help-icons">save</i>         | Sauver la ressource       |
| <i class="help-icons">play_arrow</i>   | Tester la commande        |
| <i class="help-icons">loop</i>         | Effacer le résultat et rafraichir l'entrée |

### Données

Un snapshot contient les champs suivants :

#### Général

- Nom de la commande
- Type de commande (shell, groovy ...)
- Icon du snapshot
	
Note : seul le scripting groovy est disponible.

#### Input

- Donnée de test en entrée de la commande

#### Script

- Script de la commande

A noter
- l'argument d'entrée est mapper sur une variable 'input'
- les propriétés sont disponible dans le script (via leur clé)

#### Résultat

- Résultat de l'execution de la commande

### Données de démonstration

Un [fichier de données](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) de démonstration est disponible sur [github](https://github.com/yroffin/jarvis).

Ce fichier de donnée exemple peut être chargé via la ressource [snapshot](#/snapshots).

### Exemples

#### Remote APIs

	import groovy.json.*
	import groovyx.net.http.RESTClient
	import static groovyx.net.http.ContentType.*
	
	def dio = new RESTClient( 'http://raspberrypi-slave-1.home:7001/api/' )
	def resp = dio.post( path: 'dio', body: '{"pin":0,"interruptor":'+input.interruptor+',"sender":'+input.sender+',"on":'+input.on+'}', requestContentType : JSON )
	
	if(resp.status == 200) {
	    return resp.data
	} else {
	    return new JsonSlurper().parseText('{}')
	}
	return resp

#### Hue

	import groovy.json.*
	import groovyx.net.http.RESTClient
	import static groovyx.net.http.ContentType.*
	
	def hue = new RESTClient( 'http://hue/api/********/' )
	def resp = hue.put( path: 'lights/'+input.light+'/state', body: '{"on":'+input.state+'}', requestContentType : JSON )
	
	if(resp.status == 200) {
	    return resp.data
	} else {
	    return new JsonSlurper().parseText('{}')
	}
	return resp

#### ZWave API

	import groovy.json.*
	import groovyx.net.http.RESTClient
	import static groovyx.net.http.ContentType.*
	
	/**
	 * simple api call to simulate data
	*/
	def zwave = new RESTClient( 'http://192.168.1.111:8083' )
	def resp = zwave.get( path: '/ZWaveAPI/Run/devices[3].instances[0].commandClasses[49]', headers: ["Authorization": "Basic ********"] )
	
	if(resp.status == 200) {
	    return resp.data
	} else {
	    return new JsonSlurper().parseText('{}')
	}
	return resp

#### ZWave Plugin API

	ZWayVDev_zway_6-0-49-1



