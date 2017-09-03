### Actions

| Action                                 | Description               |
| -------------------------------------- |:-------------------------:|
| <i class="fa fa-reply" aria-hidden="true"></i> | Retour au menu précédent  |
| <i class="fa fa-copy" aria-hidden="true"></i> | Dupliquer la ressource    |
| <i class="fa fa-trash" aria-hidden="true"></i>       | Supprimer la ressource    |
| <i class="fa fa-save" aria-hidden="true"></i>         | Sauver la ressource       |
| <i class="fa fa-play" aria-hidden="true"></i>   | Simuler (execution) |
| <i class="fa fa-bolt" aria-hidden="true"></i>   | Ajouter une crontab |

### Données

Un déclencheur contient les champs suivants :

#### Entête

- Nom : Nom du déclencheur
- Icône : icône du déclencheur

#### Broker

- Topic : le topic de souscription
- Body
	- le texte groovy a executer pour valider le body (doit retourner un booleen)
	- l'objet mqtt reçu est stocker dans un champ 'json', ex: json.hypothesis == "true"

#### Périodicité (liste)

- Identifiant : identifiant de l'association avec la crontab
- Ordre : ordre d'affichage
- Nom : nom de la crontab

#### Devices liés

Les devices déclenchés pas ce trigger

#### Scenarii liés

Les scénarii déclenchés pas ce trigger

### Données de démonstration

Un [fichier de données](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) de démonstration est disponible sur [github](https://github.com/yroffin/jarvis).

Ce fichier de donnée exemple peut être chargé via la ressource [snapshot](#/snapshots).

