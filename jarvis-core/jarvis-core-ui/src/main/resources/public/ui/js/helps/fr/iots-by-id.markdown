### Actions

| Action                                 | Description               |
| -------------------------------------- |:-------------------------:|
| <i class="help-icons">clear</i>        | Retour au menu précédent  |
| <i class="help-icons">content_copy</i> | Dupliquer la ressource    |
| <i class="help-icons">delete</i>       | Supprimer la ressource    |
| <i class="help-icons">save</i>         | Sauver la ressource       |
| <i class="help-icons">play_arrow</i>   | Tester l'objet            |
| <i class="help-icons">local_offer</i>  | Ajouter un container  |
| <i class="help-icons">settings_remote</i> | Ajouter un déclencheur |
| <i class="help-icons">library_add</i>     | Ajouter un plugin |

### Données

Un snapshot contient les champs suivants :

#### Général

- Nom de la commande
- Paramétrage de l'instanciation des objets connectés
- Objet actif ?
- Icon du snapshot
- Couleur de fond de la tuile d'affichage
- Opacité de la tuile d'affichage
- Couleur du texte
- Taille en ligne de la tuile sur la grille
- Taille en colonne de la tuile sur la grille 

#### Plugins

- Liste des plugins associés à cet objet

#### Rendu

- Template de l'objet
- Rendu HTML (en lecture seule)
- Rendu JSON (en lecture seule)

### Template

Dans le template de rendu utiliser la fonction javascript "execute" pour executer les actions liées
à l'iot

<code>
&lt;md-button ng-click="execute(iot)"&gt;...&lt;/md-tooltip&gt;&lt;/md-button&gt
</code>

### Données de démonstration

Un [fichier de données](https://snap-ci.com/buildartifacts/green/52740/defaultPipeline/106/install/1/jarvis-core/jarvis-core-server/src/test/resources/sample.json?archived=true) de démonstration est disponible sur [github](https://github.com/yroffin/jarvis).

Ce fichier de donnée exemple peut être chargé via la ressource [snapshot](#/snapshots).
