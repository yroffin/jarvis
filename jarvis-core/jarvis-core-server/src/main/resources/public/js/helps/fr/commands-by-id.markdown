### Actions

| Action                                 | Description               |
| -------------------------------------- |:-------------------------:|
| <i class="help-icons">clear</i>        | Retour au menu précédent  |
| <i class="help-icons">content_copy</i> | Dupliquer la ressource    |
| <i class="help-icons">delete</i>       | Supprimer la ressource    |
| <i class="help-icons">save</i>         | Sauver la ressource       |
| <i class="help-icons">play_arrow</i>   | Tester la commande        |
| <i class="help-icons">loop</i>         | Effacer le résultat       |

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

#### Résultat

- Résultat de l'execution de la commande

### Données de démonstration

Un [fichier de données](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) de démonstration est disponible sur [github](https://github.com/yroffin/jarvis).

Ce fichier de donnée exemple peut être chargé via la ressource snapshot.
