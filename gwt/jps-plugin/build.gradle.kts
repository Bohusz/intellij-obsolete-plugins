// Copyright 2000-2026 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
import org.jetbrains.intellij.platform.gradle.tasks.ComposedJarTask

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij.platform.module")
}

group = "com.intellij.gwt.jps"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(properties("platformVersion"))

        // JPS build-process API: org.jetbrains.jps.builders / org.jetbrains.jps.incremental
        bundledModule("intellij.platform.jps.build")
        // org.jetbrains.idea.maven.aether.ArtifactRepositoryManager
        bundledModule("intellij.java.aetherDependencyResolver")
    }
}

java.sourceSets["main"].java {
    srcDir("src")
}

java.sourceSets["main"].resources {
    srcDir("resources")
}

tasks {
    withType<JavaCompile> {
        options.release = 11
    }

    // The root plugin.xml registers <compileServer.plugin classpath="gwt-jps.jar"/>, but the default composed-jar
    // name would be "GWT.jps-plugin.jar", so the IDE could not add it to the build process classpath.
    named<ComposedJarTask>("composedJar") {
        archiveBaseName = "gwt-jps"
    }
}
