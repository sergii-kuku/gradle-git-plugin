plugins {
    id("java-gradle-plugin")
    id("maven")
}

group = "lazy.zoo.gradle"
version = "1.0.0-SNAPSHOT"
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

gradlePlugin {
    val scaffold by plugins.creating {
        id = "lazy.zoo.gradle.git-data-plugin"
        implementationClass = "lazy.zoo.gradle.GitDataPlugin"
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