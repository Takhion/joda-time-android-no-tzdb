# Joda-Time, with time zones provided by Android

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
