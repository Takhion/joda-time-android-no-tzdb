plugins {
    android("library")
    kotlin("android")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.2")
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = libVersionInt
        versionName = libVersion
    }
    sourceSets.main.java.srcDir("src/main/kotlin")
}

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    api("joda-time:joda-time:$jodaTimeVersion:no-tzdb")
}

afterEvaluate {
    configurePublications()
}
