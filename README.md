# Gradle Git Plugin
### Current plugin version: 1.0.0-SNAPSHOT

## Description
Lightweight plugin to get current git branch information and help with project versioning based on it.

### Branch type
Based on the current branch name, the plugin defines the branch type. 
It can be one of:
- MASTER: if being on master branch.
- RELEASE: if branch name matches any of the following patters:
```
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)"
```
- DEV: all other branches.
This can be useful to automatically detect release branches if they follow certain naming conventions.

### Project version
#### The plugin returns reconciled version for DEV branches by injecting or appending git branch name to the version, e.g.: 
- For DEV branch `feature-branch`, `2.1.0-SNAPSHOT` becomes `2.1.0-feature-branch-SNAPSHOT`).
- For DEV branch `feature-branch`, `2.1.0` becomes `2.1.0-feature-branch`).

- For DEV branch `staging/feature-branch`, `2.1.0` becomes `2.1.0-feature-branch`) if calling `gitData.getProjectVersionWithBranch()` 
- For DEV branch `staging/feature-branch`, `2.1.0` becomes `2.1.0-staging-feature-branch`) if calling `gitData.getProjectVersionWithFullBranch()` 

#### RELEASE and MASTER branches have their versions unchanged.

Use-cases:
- Can be useful if following branching development model and not willing to manually change the artifact/project version in every dev branch.
- Can be used to set jar version, to configure uploadArchives or install, etc.

- NOTE: if the project version is not set, it defaults to `unspecified` and so for DEV branch `feature-branch` it becomes `unspecified-feature-branch`!

## Parameters
`-PbranchName` - git branch name override to use instead of the current git branch.
Can be useful if executed outside git repos, in build systems (e.g. Jenkins), etc.

## Usage
Clone this repo and install the plugin jar into the local maven repo.
After you have the plugin jar in your local maven repository, add the below code to the build.gradle file:
```
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("lazy.zoo.gradle:git-data-plugin:1.0.0-SNAPSHOT")
    }
}

apply plugin: 'lazy.zoo.gradle.git-data-plugin'
```
You can also upload the plugin jar artifact to you personal/company registry (e.g. artifactory). 
Then add the reference to it in the buildscript section from the above.

## Tasks
`gitData` task is registered to display the current git branch info.

## Get data from the plugin
The plugin data is accessible via it's extension: gitData
Call the below from your gradle build:
```
gitData.getCurrentBranchType() // one of: MASTER/DEV/RELEASE
gitData.getCurrentBranchName() // name of the branch, without the grouping/folder prefixes (e.g. some/feature/foobar becomes foobar)
gitData.getCurrentBranchFullName() // full name of the branch (e.g. some/feature/foobar stays some/feature/foobar)
gitData.getProjectVersionWithBranch() // version with branch name
gitData.getProjectVersionWithFullBranch() // version with full branch name, replacing all slashes with dashes
```