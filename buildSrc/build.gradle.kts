import java.util.Properties

val kotlinVersion: String by project
val androidPluginVersion: String by project
val androidMavenPublishPluginVersion: String by project
val bintrayPluginVersion: String by project

plugins {
    `kotlin-dsl`
    idea
}

repositories {
    google()
    jcenter()
}

dependencies {
    compile(kotlin("gradle-plugin", kotlinVersion))
    compile("com.android.tools.build:gradle:$androidPluginVersion")
    compile("digital.wup:android-maven-publish:$androidMavenPublishPluginVersion")
    compile("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayPluginVersion")
}

val gradlePropertiesFile = projectDir.resolve("gradle.properties")
val generatedPropertiesDir = buildDir.resolve("generated-properties")
val generatedPropertiesFile = generatedPropertiesDir.resolve("GradleProperties.kt")

val gradleProperties = Properties().apply { gradlePropertiesFile.inputStream().use(::load) }

generatedPropertiesDir.let { dir ->
    dir.mkdirs()
    sourceSets["main"].java.srcDir(dir)
    idea.module { generatedSourceDirs = generatedSourceDirs + dir }
}

generatedPropertiesFile.bufferedWriter().use {
    gradleProperties.entries.forEach { (propertyName, propertyValue) ->
        it.appendln("const val `$propertyName` = \"$propertyValue\"")
    }
}
