# jaws-libj [![CI with Docker and Gradle](https://github.com/JeffersonLab/jaws-libj/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/JeffersonLab/jaws-libj/actions/workflows/ci.yml) [![Maven Central](https://badgen.net/maven/v/maven-central/org.jlab/jaws-libj)](https://repo1.maven.org/maven2/org/jlab/jaws-libj/)
Reusable Java Classes for [JAWS](https://github.com/JeffersonLab/jaws).  Entity classes generated to match the JAWS AVRO schemas are included as well as consumer client classes.

---
 - [Install](https://github.com/JeffersonLab/jaws-libj#install)   
 - [API](https://github.com/JeffersonLab/jaws-libj#api)    
 - [Build](https://github.com/JeffersonLab/jaws-libj#build)
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

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-libj
cd jaws-libj
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

## Test
Continuous Integration (CI) is setup using GitHub Actions, so on push tests are automatically run unless `[no ci]` is included in the commit message.   Tests can be manually run on a local workstation using:
```
docker compose -f test.yml up
```
Wait for containers to start then:
```
gradlew integrationTest
```

## Release
1. Bump the version number and release date in [build.gradle](https://github.com/JeffersonLab/jaws-libj/blob/66188b79063c2223daa1800eb745b4f31d93193a/build.gradle#L12-L14) and commit and push to GitHub (using Semantic Versioning).   
1. Create a new release on the GitHub [Releases](https://github.com/JeffersonLab/jaws-libj/releases) page corresponding to same version in build.gradle (Enumerate changes and link issues)
1. Publish new artifact on maven central with:
```
gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

## See Also
- [jaws-libp (Python)](https://github.com/JeffersonLab/jaws-libp)
