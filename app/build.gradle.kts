plugins {
    android("application")
    kotlin("android")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.2")
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }
    sourceSets.main.java.srcDir("src/main/kotlin")
}

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(project(":lib"))
}
