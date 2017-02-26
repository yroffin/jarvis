1 jarvis
========

Just Another Ridiculous Very Inteligent System

- snap-ci for java modules [![Build Status](https://snap-ci.com/yroffin/jarvis/branch/master/build_image)](https://snap-ci.com/yroffin/jarvis/branch/master)
- travis for java modules [![Build Status](https://travis-ci.org/yroffin/jarvis.svg?branch=master)](https://travis-ci.org/yroffin/jarvis)
- travis for golang modules [![Build Status](https://travis-ci.org/yroffin/jarvis-go-ext.svg?branch=master)](https://travis-ci.org/yroffin/jarvis-go-ext)

Last release [here](https://github.com/yroffin/jarvis/releases/latest) for java modules
And [here](https://github.com/yroffin/jarvis-go-ext/releases) for golang modules

1.1 Introduction
----------------

Jarvis system aim to introduce a human to machine interface capability with :
- marytts
- and a custom aiml renderer (in java)

1.2 Technology
--------------

- angularjs / angularmd for ui
- Spark + Spring Boot on server side
- pac4j integration (security) (Cf. http://www.pac4j.org and https://github.com/pac4j/spark-pac4j)
- Neo4j for database (graphdb)
- MongoDB for collections

1.3 Security
------------

Oauth2 integration (pac4j)
- Cf. https://accounts.google.com/.well-known/openid-configuration

2 Components
============

TODO

Roadmap
=======

- integrate store in client side for data lookup (threw websocket)
- integrate swagger in project (auto documentation)
- integrate lightify (Cf. https://github.com/arubenis/golightify/blob/master/http_json_server.go)
  Cf. http://www.homautomation.org/2013/10/09/how-to-control-di-o-devices-with-a-raspberry
- integrate pac4j
- more tests ... for devops target

3 Setup
=======

3.1 pre-requisites
------------------

Jarvis use Java and Neo4J technologies
- Neo4J database is a pre-requisite (http://neo4j.com/download), and can start with empty database
- Java [8 u25](https://www.java.com/fr/download) min (and JAVA_HOME set)
- Golang for building Golang components (not at runtime)

3.2 platforms
-------------

Any Java plateform can run Jarvis
- windows
- linux, and also raspberry pi :)

3.3 raspberry setup
-------------------

    pi@raspberrypi:~ $ sudo useradd -m -b /home/jarvis jarvis
    pi@raspberrypi:~ $ export GITHUB=https://github.com/yroffin/jarvis/releases/download/v1.01b
    pi@raspberrypi:~ $ sudo wget ${GITHUB}/jarvis-core-server-0.0.1-SNAPSHOT.jar -O /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar
    pi@raspberrypi:~ $ sudo chmod 755 /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar
    pi@raspberrypi:~ $ sudo chown jarvis:jarvis /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar
    pi@raspberrypi:~ $ sudo wget ${GITHUB}/jarvis-service -O /etc/init.d/jarvis-service
    pi@raspberrypi:~ $ sudo chmod 755 /etc/init.d/jarvis-service
    pi@raspberrypi:~ $ sudo update-rc.d jarvis-service defaults
    pi@raspberrypi:~ $ sudo service jarvis-service restart


3.4 sample configuration
------------------------

This simple paragraph is a simple way to have a little configuration, just create a snapshot and then
![STEP-0](http://yroffin.github.io/jarvis/images/init/step-0.PNG)

Then upload [sample.json](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) with the 'attachment' icon
![STEP-1](http://yroffin.github.io/jarvis/images/init/step-1.PNG)

After store this configuration with the 'floppy' icon
and then restore this resource (with the last icon)
![STEP-2](http://yroffin.github.io/jarvis/images/init/step-3.PNG)

4 raspberry pi network misc
----------------------------

4.1 losf network wifi link
--------------------------

Sometime RT5370 Wireless Adapter are not well configurated

Create new file:

sudo nano /etc/modprobe.d/8192cu.conf
and paste this:

options 8192cu rtw_power_mgnt=0 rtw_enusbss=1 rtw_ips_mode=1
then reboot

sudo reboot
