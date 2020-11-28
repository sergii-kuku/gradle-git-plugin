# Gradle Git Plugin
### Latest release version: 1.2.0

## Description
Lightweight plugin to get specified (or current) git branch information.

### Tasks
`gitData` task is registered to display the git branch info.

### Available data
The plugin data is accessible via it's extension: gitData
Call the below from your gradle build script:
```
gitData.isValidGitBranch() // false if unidentified-git-branch, otherwise true
gitData.getInputBranchName() // git branch parameter value if present (-PbranchName). null otherwise (the logic will point to current HEAD)
gitData.getBranchType() // one of: MASTER/DEV_BRANCH/RELEASE_BRANCH. DEV_BRANCH for unidentified-git-branch
gitData.getShortBranchName() // short name of the branch, without the grouping/folder prefixes (e.g. some/feature/foobar becomes foobar). unidentified-git-branch if  invalid git rev
gitData.getFullBranchName() // full name of the branch (e.g. some/feature/foobar becomes origin/some/feature/foobar, some-tag becomes refs/tags/some-tag). unidentified-git-branch if invalid git rev
gitData.getLastCommitHash() // short hash of the current commit. null if unidentified-git-branch
gitData.getNumberOfCommits() // number of commits in the specified branch. null if unidentified-git-branch
gitData.getTags() // tags pointing to current commit as in git tag -l --points-at rev. empty list if unidentified-git-branch
```

### Branch type
Based on the current branch name, the plugin defines the branch type. 
It can be one of:
- MASTER: if being on master branch.
- RELEASE_BRANCH: by default, if the branch name matches any of the following patters:
```
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)"
^([0-9]+\\.[0-9]+\\.[0-9]+)$
^([0-9]+\\.[0-9]+)$
^([0-9]+)$
^v([0-9]+\\.[0-9]+\\.[0-9]+)$
^v([0-9]+\\.[0-9]+)$
^v([0-9]+)$
```

`gitData.getShortBranchName()` is used with the above patterns.

NOTE: this logic may eventually fail if you are pointing to detached head commit with only numeric values in the hash (`^([0-9]+)$` pattern kicks in). 
This is a corner-case which is consciously not handled :)

- DEV_BRANCH: all other branches.

### Release branch additional patterns
Patterns to define release branches can be extended using the plugin extension method: `gitData.setReleaseBranchPatterns(List<String>)`
Example: `gitData.setReleaseBranchPatterns(["(.*)foobar(.*)", "^release-[0-9]+"])`
This can be useful to automatically detect release branches if they follow certain naming conventions.

## Parameters
`-PbranchName` - git branch name override to use instead of the current git branch (if none specified, the plugin points to `HEAD`).

## Usage
### Local 
Clone this repo and install the plugin jar into your local maven repo.
After you have the plugin jar in your local maven repository, add the below code to the build.gradle file:
```
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("lazy.zoo.gradle:git-data-plugin:1.2.0")
    }
}

apply plugin: 'lazy.zoo.gradle.git-data-plugin'
```
You can also upload the plugin jar artifact to you personal/company registry (e.g. artifactory). 
Then add the reference to it in the buildscript section from the above.
### From gradle plugins repository
Add the below code to the build.gradle file.
- Using plugins DSL:
```
plugins {
  id "lazy.zoo.gradle.git-data-plugin" version "1.2.0"
}
```
- Using legacy plugin application:
```
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath("lazy.zoo.gradle:git-data-plugin:1.2.0")
    }
}

apply plugin: 'lazy.zoo.gradle.git-data-plugin'
```

# License
The MIT License

Copyright (c) 2020 sergii-kuku

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

