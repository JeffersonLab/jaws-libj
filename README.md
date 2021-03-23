# jaws-libj [![Java CI with Gradle](https://github.com/JeffersonLab/jaws-libj/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/JeffersonLab/jaws-libj/actions?query=workflow%3A%22Java+CI+with+Gradle%22)
Reusable Java Classes for [JAWS](https://github.com/JeffersonLab/jaws).

## API
[Javadocs](https://jeffersonlab.github.io/jaws-libj)

## Build
This [Java 11](https://adoptopenjdk.net/) project uses the [Gradle 6](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-libj
cd jaws-libj
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)
