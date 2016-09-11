jarvis
======

Just Another Ridiculous Very Inteligent System

[![Build Status](https://snap-ci.com/yroffin/jarvis/branch/master/build_image)](https://snap-ci.com/yroffin/jarvis/branch/master)

[![Build Status](https://travis-ci.org/yroffin/jarvis.svg?branch=master)](https://travis-ci.org/yroffin/jarvis)

[![Build Status](https://travis-ci.org/yroffin/jarvis-go-ext.svg?branch=master)](https://travis-ci.org/yroffin/jarvis-go-ext)

Last release [here](https://github.com/yroffin/jarvis/releases/latest) for java modules
And [here](https://github.com/yroffin/jarvis-go-ext/releases) for golang modules

Introduction
------------

Jarvis system aim to introduce a human to machine interface capability with :
- marytts
- and a custom aiml renderer (in java)

Technology
----------

- angularjs / angularmd for ui
- Spark + Spring Boot on server side
- pac4j integration (security) (Cf. http://www.pac4j.org and https://github.com/pac4j/spark-pac4j)
- Neo4j for database (graphdb)
- Elastic Search to store data (event, ...)


NFC-RC522
---------

Usefull article for RASPBERRY PI 3 rev 1.2
- https://www.raspberrypi.org/forums/viewtopic.php?f=37&t=147291

Python setup
------------

Install pip then
- pip install flask
- wget https://raw.githubusercontent.com/rasplay/MFRC522-python/master/MFRC522.py

Security
--------

Oauth2 integration (pac4j)
- Cf. https://accounts.google.com/.well-known/openid-configuration

Roadmap
-------

- integrate store in client side for data lookup (threw websocket)
- integrate swagger in project (auto documentation)
- integrate lightify (Cf. https://github.com/arubenis/golightify/blob/master/http_json_server.go)
- integrate rfxcom (better than fork unix binary ;))
  Cf. http://www.homautomation.org/2013/10/09/how-to-control-di-o-devices-with-a-raspberry
- integrate pac4j
- more tests ... for devops target

Setup
======

pre-requisites
--------------

Jarvis use Java and Neo4J technologies
- Neo4J database is a pre-requisite (http://neo4j.com/download), and can start with empty database
- Java [8 u25](https://www.java.com/fr/download) min (and JAVA_HOME set)

platforms
---------

Any Java plateform can run Jarvis
- windows
- linux, and also raspberry pi :)

sample configuration
--------------------

This simple paragraph is a simple way to have a little configuration, just create a snapshot and then
![STEP-0](http://yroffin.github.io/jarvis/images/init/step-0.PNG)

Then upload [sample.json](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) with the 'attachment' icon
![STEP-1](http://yroffin.github.io/jarvis/images/init/step-1.PNG)

After store this configuration with the 'floppy' icon
and then restore this resource (with the last icon)
![STEP-2](http://yroffin.github.io/jarvis/images/init/step-3.PNG)
