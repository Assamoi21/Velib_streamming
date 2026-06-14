# Velib Streaming

Ce projet met en place un flux de données Velib en temps réel avec Scala/Spark et un tableau de bord Streamlit pour visualiser la disponibilité des vélos dans les stations.

## Fonctionnalités

- Récupération des données Velib depuis l’API OpenDataSoft
- Streaming en temps réel avec Spark Structured Streaming
- Écriture des données dans un dossier JSON local
- Tableau de bord interactif pour visualiser les stations et leur disponibilité

## Prérequis

- Java 11 ou plus
- Scala / sbt
- Python 3
- Les dépendances Python suivantes :
  - streamlit
  - pandas
  - plotly
  - jsonlines

## Démarrage

### 1. Lancer le flux Scala

Sur Windows, utilisez le lanceur fourni pour forcer JDK 11, qui est compatible avec Spark 3.3.1 :

```bat
run.bat
```

Sinon, vous pouvez lancer manuellement avec la bonne version Java :

```bash
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.31.11-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
sbt run
```

### 2. Lancer le tableau de bord

```bash
streamlit run src/main/scala/velib.py
```

## Structure du projet

- src/main/scala/VelibUtils.scala : logique de récupération et transformation des données
- src/main/scala/VelibProducer.scala : production et écriture du flux
- src/main/scala/velib.py : dashboard Streamlit

## Remarques

Le dossier de sortie des données JSON et le dossier de checkpoint peuvent être configurés dans le code du producteur.
