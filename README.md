# Gradle Git Plugin

### Current plugin version: 1.0.0-SNAPSHOT

## Description
### Project version
- Gradle plugin to get project version based on current git branch information.
- It is aimed to help to avoid manually changing the project version not to mistakenly override existing uploaded artifacts (e.g. to be used for jars names).

- The version is transformed only for DEV branch types (see below).
- The plugin follows Maven/SemVer2 version naming conventions.
- Can be helpful when following the branching development model.

- NOTE: project version should be set, otherwise the plugin will throw an exception!
### Branch type
Based on the current branch name, the plugin also defines the branch type. 
One of:
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
`-PbranchName` - git branch name override to use instead of the current git branch.
Can be useful if executed outside git repos, within build systems (e.g. Jenkins), etc.

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
You can also upload the plugin jar artifact to you personal/company registry (e.g. artifactory). 
Then add the reference to it the buildscript section of your project.

## Tasks
`gitInfo` task is registered to display the current git info 

## Get data from the plugin
The plugin data is accessible via it's extension: gitInfo
Just call the below from your gradle build:
```
gitInfo.getCurrentBranchType()
gitInfo.getCurrentBranchName()
gitInfo.getProjectVersionWithBranch()
```