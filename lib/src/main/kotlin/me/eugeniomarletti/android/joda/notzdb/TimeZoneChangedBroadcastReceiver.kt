package me.eugeniomarletti.android.joda.notzdb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.joda.time.DateTimeZone
import java.util.TimeZone

internal class TimeZoneChangedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        TimeZone.getDefault()
            .let(DateTimeZone::forTimeZone)
            .let(DateTimeZone::setDefault)
    }
}
