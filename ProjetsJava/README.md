# EN'QUETE DE LUMIERE
### Jeu multijoueur en ligne écrit en Java

## Outils utilisés
* Le framework coeur du jeu est [Libgdx](https://libgdx.badlogicgames.com/), c'est un bibliothèque Java
fournissant des outils de bases à la création d'un jeu (rendu, sprite, gestion des inputs, etc ...)
* [Box2D](http://box2d.org/) est un moteur physiqu epur jeux videos qui permet la gestion des mouvements et des collisions
* [Gdx-AI](https://github.com/libgdx/gdx-ai/wiki) est un extension qui fournit de nombreuses outils pour l'intelligence artificielle (behavior trees, A* pathfinding)
* [Kryonet](https://github.com/EsotericSoftware/kryonet) est une bibliothèque Java fournissant les outils nécessaires à la partie réseau du projet. 

## Mettre en place le projet
Le project fonctionne avec le gestionnaire de dépendance Gradle. Il suffit d'importer un projet Graddle sur Eclipse et de
configurer dans les paramètres d'exécution le dossier `..\PRO3600-18-CON-5\core\assets` en tant que working directory dans l'onglet "argument" afin que Libgdx sache ou chercher les fichiers externes (sprites, cartes, sons, etc ...).
Pour éxécuter le jeu, il faut lancer la classe `Game-desktop.src.com.mygdx.game.desktop.DesktopLauncher`

## Découpage du code
LibGdx fonctionne par "fenêtre" qui sont en réaltion des implémentations de la classe `Screen`, ainsi nous découpons nos
différents tests en différents screen. Pour définir le screen éxécuté, il suffit d'ecrire `setScreen(new NomDuSCreen(this));`dans la classe `gdx.mygdx.game.Game`.
Actuellement, deux fenêtres sont disponibles :
* `PlayScreen` qui est une carte simple avec collision ou un joueur peut se déplacer avec les touches ZQSD.
* `MutliPlayScreen` qui est une carte simple avec collision ou deux joueurs peuvent se déplacer avec les touches ZQSD et les flèches.
