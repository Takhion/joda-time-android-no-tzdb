package me.eugeniomarletti.android.joda.notzdb.app

import android.app.Application
import me.eugeniomarletti.android.joda.notzdb.initJodaTimeForAndroid

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initJodaTimeForAndroid()
    }
}
