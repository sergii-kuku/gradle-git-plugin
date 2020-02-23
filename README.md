# Gradle Git Plugin
### Latest release version: 1.1.0

## Description
Lightweight plugin to get specified (or current) git branch information.

### Tasks
`gitData` task is registered to display the git branch info.

### Available data
The plugin data is accessible via it's extension: gitData
Call the below from your gradle build:
```
gitData.getInputBranchName() // git branch parameter value if present (-PbranchName). "HEAD" otherwise (points to current checked out branch)
gitData.isValidGitBranch() // true if "git rev-parse --verify $inputBranchName" returns with 0 exit code
gitData.getBranchType() // one of: MASTER/DEV/RELEASE
gitData.getShortBranchName() // name of the branch, without the grouping/folder prefixes (e.g. some/feature/foobar becomes foobar)
gitData.getFullBranchName() // full name of the branch (e.g. some/feature/foobar stays some/feature/foobar)
gitData.getLastCommitHash() // short hash of the last commit on the specified branch
gitData.getNumberOfCommits() // number of commits in the specified branch
```

### Branch type
Based on the current branch name, the plugin defines the branch type. 
It can be one of:
- MASTER: if being on master branch.
- RELEASE: by default, if the branch name matches any of the following patters:
```
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]"
projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)"
```
- DEV: all other branches.

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
        classpath("lazy.zoo.gradle:git-data-plugin:1.1.0")
    }
}

apply plugin: 'lazy.zoo.gradle.git-data-plugin'
```
You can also upload the plugin jar artifact to you personal/company registry (e.g. artifactory). 
Then add the reference to it in the buildscript section from the above.
### From gradle plugins repository
Add the below code to the build.gradle file:
```
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath("lazy.zoo.gradle:git-data-plugin:1.1.0")
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

