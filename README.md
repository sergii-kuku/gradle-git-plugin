# Gradle Git Plugin

### Current plugin version: 1.0.0-SNAPSHOT

## Description
### Artifact version
Gradle plugin to get artifact's version based on current git branch information.
The plugin follows Maven/SemVer2 version conventions.
Can be helpful when following the branching development model.
It is aimed to help to avoid manually changing the version not to mistakenly override existing uploaded artifacts.
### Branch type
Based on the current branch name, the plugin also defines the branch type. 
Either of:
- MASTER: if being on master branch
- RELEASE: corresponds to any of the following name patters
```
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)"
```
- DEV: all other branches

Accordingly, this can be useful to automatically detect release branches if they follow certain naming conventions.

## Parameters
`-PbranchName` - git branch name override to use instead of the current git branch

## Usage
Clone this repo, run `./gradlew clean install`. After you have the plugin jar in your local maven repository, add the below code to the build.gradle file:
```
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("coding.zoo:gradle-git-plugin:1.0.0-SNAPSHOT")
    }
}

apply plugin: 'coding.zoo.gradle-git-plugin'
```
You can also upload the plugin artifact to you registry (e.g. artifactory). Then add the reference to it the buildscript section.

## Get data from the plugin
The plugin data is accessible via it's extension: gradleGit
```
```