### Actions

| Action                                 | Description               |
| -------------------------------------- |:-------------------------:|
| <i class="help-icons">clear</i>        | Retour au menu précédent  |
| <i class="help-icons">content_copy</i> | Dupliquer la ressource    |
| <i class="help-icons">delete</i>       | Supprimer la ressource    |
| <i class="help-icons">save</i>         | Sauver la ressource       |
| <i class="help-icons">play_arrow</i>   | Tester les actions du plugin |
| <i class="help-icons">input</i>   | Tester l'affichage du plugin |
| <i class="help-icons">loop</i>   | Effacer le contexte |
| <i class="help-icons">playlist_add</i>   | Sélectionner une commande |
| <i class="help-icons">local_offer</i>   | Sélectionner un propriétaire |

### Données

Un snapshot contient les champs suivants :

#### Général

- Nom de la commande
- Catégorie : la catégorie du plugin
- Propriétaire : IOT parent
- Icône : icône du plugin
- Actif : plugin actif ?
- Visible : plugin visible ?

#### Script (liste)

- Identifiant : identifiant de l'association avec la commande
- Instance : instance physique du lien
- Ordre : ordre d'affichage
- Nom : nom de la variable recevant l'execution de la commande
- Nature : text, json ou xml
- Type : data ou action
- Script (en lecture seule)

#### Résultat

- Résultat du dernier test

### Données de démonstration

Un [fichier de données](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) de démonstration est disponible sur [github](https://github.com/yroffin/jarvis).

Ce fichier de donnée exemple peut être chargé via la ressource [snapshot](#/snapshots).

