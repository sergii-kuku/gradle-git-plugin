plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.10.1"
    id("maven")
}

group = "lazy.zoo.gradle"
version = "1.0.0"
val junitVersion = "4.12"
val javaVersion = JavaVersion.VERSION_1_8

repositories {
    jcenter()
    mavenCentral()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

dependencies {
    testImplementation("junit:junit:$junitVersion")
}

pluginBundle {
    website = "https://github.com/sergii-kuku/gradle-git-plugin"
    vcsUrl = "https://github.com/sergii-kuku/gradle-git-plugin.git"
    tags = listOf("git", "versioning")
}

gradlePlugin {
    plugins {
        create("gitDataPlugin") {
            id = "lazy.zoo.gradle.git-data-plugin"
            displayName = "Git Data Plugin"
            description = "Lightweight plugin to get current git branch information and help with project versioning based on it"
            implementationClass = "lazy.zoo.gradle.GitDataPlugin"
        }
    }
}

// TASKS

tasks {
    validateTaskProperties {
        failOnWarning = true
    }

    jar {
        from(sourceSets.main.map { it.allSource })
        manifest {
            attributes["Built-By"] = System.getProperty("user.name")
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Build-Jdk"] = System.getProperty("java.version")
        }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.debugOptions.debugLevel = "source,lines,vars"
    }

    clean {
        delete("out")
    }
}

// PLUGIN FUNCTIONAL TESTS

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

val functionalTest by tasks.creating(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}