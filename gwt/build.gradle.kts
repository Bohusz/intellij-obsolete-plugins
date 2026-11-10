// Copyright 2000-2026 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.PrepareSandboxTask

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "com.intellij.gwt"
version = properties("pluginVersion")

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(properties("platformVersion"))

        // hard dependencies (see resources/META-INF/plugin.xml <dependencies>)
        bundledPlugin("JavaScript")
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.java-i18n")
        bundledPlugin("com.intellij.javaee")

        // optional dependencies
        // Compile the optional Web/Servlets and run integration; these are not runtime requirements.
        bundledPlugin("com.intellij.javaee.app.servers.integration")
        bundledPlugin("com.intellij.javaee.web")
        bundledPlugin("org.jetbrains.idea.maven")
        bundledPlugin("com.intellij.gradle")
        bundledPlugin("org.jetbrains.plugins.gradle")
        bundledPlugin("JavaScriptDebugger")
        bundledModule("intellij.platform.scriptDebugger.backend")

        // plugin content modules
        pluginModule(project(":spring"))
        pluginModule(project(":dev"))

        // additional plugins required for testing
        // (org.jetbrains.idea.maven is already provided by the bundledPlugin declaration above)
        testBundledPlugin("com.intellij.spring")
        testBundledPlugin("com.intellij.spring.mvc")
        testBundledPlugin("com.intellij.javaee")

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
        testFramework(TestFrameworkType.Plugin.Maven)
        testFramework(TestFrameworkType.Plugin.ExternalSystem)
        testFramework(TestFrameworkType.JUnit5)
    }

    // protobuf-lite runtime for the generated RemoteMessageProto sources
    implementation("com.google.protobuf:protobuf-javalite:3.25.5")

    // main sources reference org.jetbrains.jps.gwt.* classes at compile time;
    // runtimeOnly ships gwt-jps into the distribution for the external build process.
    compileOnly(project(":jps-plugin"))
    runtimeOnly(project(":jps-plugin"))
    implementation(project(":runtime"))

    testImplementation("junit:junit:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

kotlin {
    jvmToolchain(25)
}

java.sourceSets["main"].java {
    srcDir("src")
    srcDir("remoteUi/generated-source")
}

java.sourceSets["main"].resources {
    srcDir("resources")
}

// Active test sources. Tests that depend on JetBrains test frameworks not distributed with the
// released IDE (JavaEE-web, JavaScript, Spring, Grazie, artifact-compiler test bases) live under
// test-quarantined/ and are intentionally excluded from compilation. See test-quarantined/README.md.
java.sourceSets["test"].java {
    srcDir("test")
}

intellijPlatform {
    // GUI Designer form code is inlined in the dialog constructors (bound fields are final),
    // so bytecode form instrumentation is neither needed nor compatible.
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
        }

        changeNotes = "Unofficial community build for IntelliJ IDEA 2026.3 (263). Not supported by JetBrains."
    }
}

// Exercise the coding-assistance plugin with the subscription-only modules disabled.
intellijPlatformTesting.testIde.register("testFreeEdition") {
    plugins {
        disablePlugins(listOf(
            "com.intellij.modules.ultimate",
            "com.intellij.modules.idea.ultimate",
            "com.intellij.javaee.web",
            "com.intellij.javaee.app.servers.integration",
            "JavaScriptDebugger",
        ))
    }
    task {
        useJUnitPlatform()
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = tasks.test.get().classpath
        systemProperty("gwt.test.data.path", layout.projectDirectory.dir("testData").asFile.absolutePath)
        filter {
            includeTestsMatching("com.intellij.gwt.GwtFreeEditionTest")
            includeTestsMatching("com.intellij.gwt.GwtAnnotationUtilTest")
            includeTestsMatching("com.intellij.gwt.model.GwtModuleTest")
            includeTestsMatching("com.intellij.gwt.model.GwtSourceTagTest")
            includeTestsMatching("com.intellij.gwt.model.GwtSuperSourceElementFinderTest")
            includeTestsMatching("com.intellij.gwt.inspections.GwtInspectionsTest")
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = properties("platformJavaVersion")
        targetCompatibility = properties("platformJavaVersion")
    }

    test {
        useJUnitPlatform()
        classpath += sourceSets["test"].runtimeClasspath
        exclude("**/GwtFreeEditionTest.class")
        providers.gradleProperty("test.idea.home.path").orNull?.takeIf { it.isNotBlank() }?.let {
            systemProperty("idea.home.path", it)
        }
        // GWT tests resolve fixtures relative to this directory (see GwtTestCase.getGwtTestDataPath)
        systemProperty("gwt.test.data.path", layout.projectDirectory.dir("testData").asFile.absolutePath)
    }

    // bundle the legacy SuperDevMode launcher jar into the plugin's lib/ directory
    withType<PrepareSandboxTask> {
        from(layout.projectDirectory.dir("lib")) {
            into(pluginName.map { "$it/lib/" })
            include("*.jar")
        }
    }
}
