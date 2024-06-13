# jaws-libj [![CI with Docker and Gradle](https://github.com/JeffersonLab/jaws-libj/actions/workflows/ci.yaml/badge.svg?branch=main)](https://github.com/JeffersonLab/jaws-libj/actions/workflows/ci.yaml) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/jaws-libj)](https://repo1.maven.org/maven2/org/jlab/jaws-libj/)
Reusable Java Classes for [JAWS](https://github.com/JeffersonLab/jaws).  Entity classes generated to match the JAWS AVRO schemas are included as well as consumer client classes.

---
 - [Install](https://github.com/JeffersonLab/jaws-libj#install)   
 - [API](https://github.com/JeffersonLab/jaws-libj#api)
 - [Configure](https://github.com/JeffersonLab/jaws-libj#configure)       
 - [Build](https://github.com/JeffersonLab/jaws-libj#build)
 - [Develop](https://github.com/JeffersonLab/jaws-libj#develop) 
 - [Test](https://github.com/JeffersonLab/jaws-libj#test)
 - [Release](https://github.com/JeffersonLab/jaws-libj#release)
 - [See Also](https://github.com/JeffersonLab/jaws-libj#see-also)
---

## Install

This library requires a Java 11+ JVM and standard library at run time.

You can obtain the library jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/jaws-libj/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:jaws-libj:<version>'
```
Check the [Release Notes](https://github.com/JeffersonLab/jaws-libj/releases) to see what has changed in each version.  

## API
[Javadocs](https://jeffersonlab.github.io/jaws-libj)

## Configure
Each client class requires a Java Properties object in the constructor to indicate configuration.   The expected properties match those in the [kakfa-common](https://github.com/JeffersonLab/kafka-common#configure) lib.

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-libj
cd jaws-libj
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

## Develop
Set up the build environment following the [Build](https://github.com/JeffersonLab/jaws-libj#build) instructions.

In order to iterate rapidly when making changes it's often useful to create new tests and run them directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with Docker Compose:
```
docker compose -f deps.yaml up
```

## Test
The integration tests depend on a running Kafka instance, generally in Docker. The tests run automatically via the [CI](https://github.com/JeffersonLab/jaws-libj/actions/workflows/ci.yaml) GitHub Action on every commit (unless [no ci] is included in the commit message). The tests can be run locally during development. Set up the development environment following the [Develop](https://github.com/JeffersonLab/jaws-libj#develop) instructions. Then with the deps.yaml Docker containers running execute:
```
gradlew integrationTest
```

## Release
1. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. The [CD](https://github.com/JeffersonLab/jmyapi/blob/main/.github/workflows/cd.yml) GitHub Action should run automatically invoking:
   - The [Create release](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-release.yaml) GitHub Action to tag the source and create release notes summarizing any pull requests.   Edit the release notes to add any missing details.
   - The [Publish artifact](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/maven-publish.yaml) GitHub Action to create a deployment artifact on maven central.
   - The [Publish docs](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-pages-publish.yaml) GitHub Action to create javadocs.


## See Also
- [jaws-libp (Python)](https://github.com/JeffersonLab/jaws-libp)
- [kafka-common](https://github.com/JeffersonLab/kafka-common)
