# jaws-libj [![Java CI with Gradle](https://github.com/JeffersonLab/jaws-libj/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JeffersonLab/jaws-libj/actions?query=workflow%3A%22Java+CI+with+Gradle%22)
Reusable Java Classes for [JAWS](https://github.com/JeffersonLab/jaws).  Java classes generated to match the AVRO schema are included as well as classes for handling Event Sourcing in Kafka (when Kafka Streams KTable is not a good fit).

---
 - [Usage](https://github.com/JeffersonLab/jmyapi#usage)
   - [API](https://github.com/JeffersonLab/jmyapi#api)  
 - [Build](https://github.com/JeffersonLab/jmyapi#build)
---

## Usage
The library is a jar file plus dependencies on Kafka libraries and the Java 8+ JVM and standard library.  You can obtain the jaws-libj jar file from the [Maven Central repository](https://repo1.maven.org/maven2/org/jlab/jaws-libj/) directly or from a Maven friendly build tool with the following coordinates (Gradle example shown):
```
implementation 'org.jlab:jaws-libj:1.0.0'
```
You can check the [Release Notes](https://github.com/JeffersonLab/jaws-libj/releases) to see what has changed in each version.  

### API
[Javadocs](https://jeffersonlab.github.io/jaws-libj)

## Build
This [Java 8+](https://adoptopenjdk.net/) project uses the [Gradle 6](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-libj
cd jaws-libj
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)
