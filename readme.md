# 🎨 Paint Java

## 1. Présentation du projet

**PaintJava** est une application de dessin de bureau développée en Java à l'aide des bibliothèques Swing et AWT.

Elle propose une interface graphique interactive permettant de créer, modifier et gérer des dessins à l'aide de différents outils, formes et options de personnalisation.

L'application intègre également des fonctionnalités avancées telles que la gestion des fichiers, l'historique des opérations, la manipulation des images, la sauvegarde automatique, la communication réseau entre clients et serveur ainsi que l'enregistrement et le chargement des dessins depuis une base de données.

## 📑 Table des matières

1. [Présentation du projet](#1-présentation-du-projet)
2. [Fonctionnalités de l'application](#2-fonctionnalités-de-lapplication)
   - [Outils de dessin](#21-outils-de-dessin)
   - [Sélection et manipulation des images](#22-sélection-et-manipulation-des-images)
   - [Formes géométriques](#23-formes-géométriques)
   - [Gestion des couleurs](#24-gestion-des-couleurs)
   - [Personnalisation de l'épaisseur et de l'opacité](#25-personnalisation-de-lépaisseur-et-de-lopacité)
   - [Gestion de l'historique](#26-gestion-de-lhistorique)
   - [Importation et exportation](#27-importation-et-exportation)
   - [Sauvegarde automatique](#28-sauvegarde-automatique)
   - [Communication réseau](#29-communication-réseau)
   - [Intégration d'une base de données](#210-intégration-dune-base-de-données)
   - [Interface multilingue](#211-interface-multilingue)
3. [Description du menu principal](#3-description-du-menu-principal)
4. [Technologies utilisées](#4-technologies-utilisées)
5. [Structure du projet](#5-structure-du-projet)
6. [Prérequis](#6-prérequis)
7. [Installation et exécution](#7-installation-et-exécution)
8. [Configuration de la base de données](#8-configuration-de-la-base-de-données)
9. [Communication client-serveur](#9-communication-client-serveur)
10. [Captures d'écran](#10-captures-décran)
11. [Raccourcis clavier](#11-raccourcis-clavier)
12. [Perspectives d'amélioration](#12-perspectives-damélioration)
13. [Informations sur le projet](#13-informations-sur-le-projet)

## 2. Fonctionnalités de l'application

### 2.1. Outils de dessin

L'application met à disposition plusieurs outils permettant de réaliser différents types de dessins.

| Outil | Description |
|---|---|
| **Pencil (Crayon)** | Permet de dessiner des traits à main levée. |
| **Brush (Pinceau)** | Permet de réaliser des traits avec un effet de pinceau. |
| **Marker (Marqueur)** | Permet de dessiner avec un effet de transparence. |
| **Airbrush (Aérographe)** | Permet de créer un effet de pulvérisation de couleur. |
| **Eraser (Gomme)** | Permet d'effacer les éléments dessinés. |
| **Text (Texte)** | Permet d'insérer du texte dans la zone de dessin. |
| **Fill with colour (Remplissage)** | Permet de remplir une zone avec une couleur sélectionnée. |
| **Colour picker (Pipette)** | Permet de récupérer une couleur directement à partir du dessin. |
| **Magnifier (Loupe)** | Permet d'utiliser l'outil de grossissement. |

### 2.2. Sélection et manipulation des images

L'application propose des fonctionnalités permettant de sélectionner et de modifier les éléments graphiques.

- Sélection d'une zone du dessin.
- Suppression de la sélection.
- Recadrage d'une image ou d'une zone sélectionnée.
- Redimensionnement d'une image.
- Rotation de l'image vers la droite.
- Retournement horizontal de l'image.

### 2.3. Formes géométriques

L'application dispose d'une catégorie dédiée aux formes géométriques.

Les options de personnalisation comprennent :

- **Outline (Contour) :** permet de définir l'affichage du contour d'une forme.
- **Fill (Remplissage) :** permet de remplir une forme avec une couleur.

Ces options permettent de personnaliser l'apparence des formes dessinées.

### 2.4. Gestion des couleurs

L'application propose plusieurs fonctionnalités de gestion des couleurs :

- Sélection d'une couleur principale.
- Sélection d'une couleur secondaire.
- Utilisation d'une palette de couleurs.
- Aperçu des couleurs sélectionnées.
- Création ou sélection d'une nouvelle couleur avec l'option **New colour...**.

### 2.5. Personnalisation de l'épaisseur et de l'opacité

Deux curseurs permettent de personnaliser les propriétés du dessin :

**Stroke size (Épaisseur du trait)**

Permet d'ajuster l'épaisseur du trait utilisé par les outils de dessin. L'utilisateur peut ainsi réaliser des traits fins ou épais selon ses besoins.

**Opacity (Opacité)**

Permet de régler le niveau de transparence des opérations de dessin. Une opacité élevée produit un rendu plus opaque, tandis qu'une opacité faible produit un rendu plus transparent.

### 2.6. Gestion de l'historique

L'application intègre un système de gestion des opérations permettant de modifier l'état du dessin.

- **Undo :** annule la dernière opération.
- **Redo :** rétablit une opération précédemment annulée.
- **Select all :** sélectionne l'ensemble des éléments concernés.
- **Delete selection :** supprime la sélection.
- **Crop to selection :** recadre le dessin selon la zone sélectionnée.

L'historique est limité à un nombre défini d'états afin de maîtriser l'utilisation de la mémoire.

### 2.7. Importation et exportation

L'application permet de gérer les projets et les images à travers différentes opérations :

- Création d'un nouveau projet.
- Ouverture d'un projet existant.
- Importation d'une image.
- Enregistrement d'un projet.
- Exportation du dessin au format PNG.
- Exportation du dessin au format JPG.

### 2.8. Sauvegarde automatique

L'application dispose d'un mécanisme de sauvegarde automatique permettant de conserver périodiquement l'état du dessin.

Le fichier `autosave.paint` est utilisé pour stocker les données de sauvegarde automatique etconserver périodiquement une copie de l'état du projet dans le fichier autosave.paint.
### 2.9. Communication réseau

L'application intègre une architecture client-serveur basée sur les sockets Java.

Elle permet notamment :

- Le démarrage d'un serveur local.
- La connexion à un serveur.
- La déconnexion du serveur.
- La communication entre plusieurs clients.
- L'échange d'opérations de dessin.
- La diffusion des opérations reçues aux clients connectés.

Cette fonctionnalité permet de mettre en place un mécanisme de partage des opérations de dessin entre plusieurs utilisateurs.

### 2.10. Intégration d'une base de données

L'application utilise JDBC pour communiquer avec une base de données MySQL.

Elle propose deux opérations principales :

- **Save to database :** enregistre un dessin dans la base de données.
- **Open from database :** permet de charger un dessin précédemment enregistré.

La classe `DessinDAO.java` assure la gestion des opérations d'accès aux données.

### 2.11. Interface multilingue

L'application intègre un menu **Language** permettant de gérer la langue de l'interface.

Les ressources linguistiques sont organisées dans deux fichiers :

- `Messages_fr.properties` : contient les textes en français.
- `Messages_en.properties` : contient les textes en anglais.

Cette organisation facilite la traduction et la maintenance de l'interface multilingue.

## 3. Description du menu principal

La barre de menus regroupe les principales fonctionnalités de l'application.

### 3.1. Menu File (Fichier)

Ce menu permet de gérer les projets et les fichiers.

| Commande | Description |
|---|---|
| **New** | Crée un nouveau projet de dessin. |
| **Open project...** | Ouvre un projet existant. |
| **Open image...** | Importe une image dans l'application. |
| **Save project...** | Enregistre le projet. |
| **Export as image (PNG/JPG)...** | Exporte le dessin sous forme d'image PNG ou JPG. |
| **Quit** | Ferme l'application. |

### 3.2. Menu Edit (Édition)

Ce menu permet de modifier le dessin et de gérer les opérations.

| Commande | Description |
|---|---|
| **Undo** | Annule la dernière opération. |
| **Redo** | Rétablit la dernière opération annulée. |
| **Select all** | Sélectionne l'ensemble des éléments concernés. |
| **Delete selection** | Supprime la zone sélectionnée. |
| **Crop to selection** | Recadre le dessin selon la sélection. |

### 3.3. Menu View (Affichage)

Ce menu permet de modifier le niveau de zoom de la zone de travail.

| Commande | Description |
|---|---|
| **Zoom in** | Augmente le niveau de zoom. |
| **Zoom out** | Diminue le niveau de zoom. |
| **Zoom 100%** | Rétablit le zoom à sa taille normale de 100 %. |

### 3.4. Menu Network (Réseau)

Ce menu regroupe les fonctionnalités de communication réseau.

| Commande | Description |
|---|---|
| **Start a local server** | Démarre un serveur local pour accepter les connexions des clients. |
| **Connect to a server...** | Permet de se connecter à un serveur. |
| **Disconnect** | Ferme la connexion réseau en cours. |

### 3.5. Menu Database (Base de données)

Ce menu permet d'interagir avec la base de données.

| Commande | Description |
|---|---|
| **Save to database...** | Enregistre le dessin dans la base de données. |
| **Open from database...** | Charge un dessin depuis la base de données. |

### 3.6. Menu Language (Langue)

Ce menu est dédié à la gestion de la langue de l'interface.

Les ressources de traduction sont définies dans les fichiers `Messages_fr.properties` et `Messages_en.properties`.

## 4. Technologies utilisées

| Technologie | Utilisation |
|---|---|
| **Java** | Langage principal de développement. |
| **Java Swing** | Création de l'interface graphique. |
| **Java AWT** | Gestion des composants et des opérations graphiques. |
| **Java 2D** | Dessin et rendu des formes. |
| **JDBC** | Connexion et interaction avec la base de données. |
| **MySQL** | Système de gestion de base de données. |
| **Java Sockets** | Communication réseau entre clients et serveur. |
| **Java Serialization** | Échange d'objets entre les applications. |
| **ImageIO** | Lecture et écriture des images. |
| **Threads** | Exécution des traitements en arrière-plan. |
| **ResourceBundle / Properties** | Gestion des ressources multilingues. |

## 5. Structure du projet

Le projet est organisé dans un répertoire unique contenant les classes Java, les ressources linguistiques, les scripts de compilation et les fichiers nécessaires à son fonctionnement.

```
PaintJava/
│
├── screenshots/
│   ├── Screenshot 2026-11-23 020750.png
│   ├── Screenshot 2026-11-23 030956.png
│   ├── Screenshot 2026-11-23 031030.png
│   ├── Screenshot 2026-11-23 031054.png
│   └── Screenshot 2026-11-23 031139.png
│
├── ApercuCouleurs.java
├── autosave.paint
├── BoutonCouleur.java
├── BoutonRuban.java
├── build.bat
├── build.sh
├── ClientReseau.java
├── DessinDAO.java
├── Forme.java
├── Icones.java
├── Messages_en.properties
├── Messages_fr.properties
├── Paint.jar
├── PaintApp.java
├── ProjetPaint.java
├── readme.md
├── ServeurPaint.java
└── ZoneDessin.java
```

Le dossier `screenshots/` regroupe les captures d'écran illustrant les différentes interfaces et fonctionnalités de l'application.


### Description des fichiers

| Fichier | Description |
|---|---|
| `PaintApp.java` | Classe principale : initialisation et gestion de l'interface graphique. |
| `ZoneDessin.java` | Gestion de la zone de dessin, des interactions et du rendu graphique. |
| `Forme.java` | Représentation et application des formes et opérations graphiques. |
| `ProjetPaint.java` | Gestion des opérations liées aux projets et aux images. |
| `Icones.java` | Création et gestion des icônes. |
| `BoutonRuban.java` | Personnalisation des boutons de la barre d'outils. |
| `BoutonCouleur.java` | Création des boutons de sélection des couleurs. |
| `ApercuCouleurs.java` | Affichage des couleurs sélectionnées. |
| `ClientReseau.java` | Gestion de la communication côté client. |
| `ServeurPaint.java` | Gestion du serveur et des connexions réseau. |
| `DessinDAO.java` | Gestion de l'accès aux dessins dans la base de données. |
| `Messages_fr.properties` | Ressources linguistiques en français. |
| `Messages_en.properties` | Ressources linguistiques en anglais. |
| `autosave.paint` | Fichier utilisé pour la sauvegarde automatique. |
| `Paint.jar` | Archive Java contenant une version compilée de l'application. |
| `build.bat` | Script de compilation pour Windows. |
| `build.sh` | Script de compilation pour Linux et macOS. |
| `readme.md` | Documentation du projet. |

## 6. Prérequis

Pour compiler et exécuter le projet, les éléments suivants sont nécessaires :

- Java Development Kit (JDK) compatible avec le projet.
- Un système d'exploitation compatible avec Java.
- MySQL Server pour les fonctionnalités nécessitant la base de données.
- MySQL Connector/J pour la connexion JDBC.
- Un environnement réseau local pour tester les fonctionnalités client-serveur.

## 7. Installation et exécution

### 7.1. Cloner le dépôt


git clone https://github.com/Hibabg21/PaintJava.git

Accéder au répertoire du projet :

```
cd PaintJava
```

### 7.2. Vérifier l'installation de Java

```
java -version
javac -version
```

### 7.3. Compiler le projet

**Sous Windows :**

```
build.bat
```

**Sous Linux / macOS :**

```
chmod +x build.sh
./build.sh
```

### 7.4. Lancer l'application

Si le fichier JAR contient un manifeste configuré avec la classe principale :

```
java -jar Paint.jar
```

Sinon, utiliser la commande correspondant au classpath et au répertoire de compilation configurés dans les scripts du projet.

## 8. Configuration de la base de données

Pour utiliser les fonctionnalités de persistance, une base de données MySQL doit être disponible.

Les étapes générales sont les suivantes :

1. Installer et démarrer MySQL Server.
2. Créer la base de données attendue par l'application.
3. Créer les tables correspondant à la structure utilisée dans `DessinDAO.java`.
4. Configurer les paramètres de connexion JDBC.
5. Ajouter MySQL Connector/J au classpath du projet.

Les paramètres de connexion doivent correspondre à l'environnement utilisé :

- Adresse du serveur.
- Port MySQL.
- Nom de la base de données.
- Nom d'utilisateur.
- Mot de passe.

**Important :** les identifiants de connexion ne doivent pas être publiés dans un dépôt GitHub public.

## 9. Communication client-serveur

La communication réseau repose sur une architecture client-serveur.

Le fonctionnement général est le suivant :

1. Le serveur local est démarré.
2. Les clients établissent une connexion avec le serveur.
3. Les opérations de dessin sont échangées à travers les sockets Java.
4. Le serveur reçoit les opérations transmises.
5. Les opérations peuvent être diffusées aux clients connectés.

Cette architecture permet de mettre en place un mécanisme de partage des opérations de dessin entre plusieurs utilisateurs.

## 10. Captures d'écran

Les captures d'écran suivantes permettent de présenter les principales fonctionnalités de l'application.

### Interface principale

![Interface principale](screenshots/Screenshot%202026-11-23%20020750.png)

### Outils de dessin et palette de couleurs

![Outils de dessin](screenshots/Screenshot%202026-11-23%20031054.png)

### Manipulation des images

![Manipulation des images](screenshots/Screenshot%202026-11-23%20031030.png)

## 11. Raccourcis clavier

| Raccourci | Action |
|---|---|
| `Ctrl + N` | Créer un nouveau projet |
| `Ctrl + O` | Ouvrir un projet Paint |
| `Ctrl + S` | Enregistrer le projet |
| `Ctrl + E` | Exporter l'image |
| `Ctrl + Z` | Annuler la dernière action |
| `Ctrl + Y` | Rétablir la dernière action annulée |
| `Ctrl + A` | Sélectionner toute la zone de dessin |

## 12. Perspectives d'amélioration

Plusieurs évolutions peuvent être envisagées pour enrichir l'application :

- Ajouter de nouveaux outils de dessin.
- Améliorer les options de personnalisation.
- Enrichir les fonctionnalités collaboratives.
- Ajouter de nouveaux formats d'importation et d'exportation.
- Améliorer la gestion des projets et des fichiers.
- Optimiser les performances de l'application.

## 13. Informations sur le projet

- **Nom du projet :** PaintJava
- **Type :** Application de dessin de bureau
- **Domaine :** Développement logiciel
- **Langage principal :** Java
- **Interface graphique :** Swing / AWT
- **Base de données :** MySQL
- **Communication réseau :** Architecture client-serveur

**PaintJava — Une application de dessin développée avec Java ☕🎨**