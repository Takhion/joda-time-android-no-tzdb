# Joda-Time, with time zones provided by Android
[![version]](#download)

#### _NOTE: requires Kotlin!_

## Download

### Gradle

```gradle
dependencies {
    compile("me.eugeniomarletti:joda-time-android-no-tzdb:$version")
}
```

## Usage

Create a custom Android `Application` (if you don't have one already):
```kotlin
package com.example

import android.app.Application
import me.eugeniomarletti.android.joda.notzdb.initJodaTimeForAndroid

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initJodaTimeForAndroid() // that's it!
    }
}
```

Remember to have the correct `android:name` inside the `application` tag of your `AndroidManifest.xml`:
```xml
<manifest
    ...>
    <application
        android:name=".MyApplication"
        ...
```

[version]:
https://img.shields.io/badge/dynamic/xml.svg?label=version&style=flat-square&colorB=blue&query=%2F%2Fmetadata%2Fversioning%2Frelease&url=http%3A%2F%2Fcentral.maven.org%2Fmaven2%2Fme%2Feugeniomarletti%2Fjoda-time-android-no-tzdb%2Fmaven-metadata.xml "version"
