package me.eugeniomarletti.android.joda.notzdb

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import org.joda.time.DateTimeZone
import org.joda.time.tz.Provider
import java.io.Closeable

typealias ExceptionLogger = (e: Throwable) -> Unit

internal typealias TimeZoneId = String
internal typealias UnixMillis = Long
internal typealias OffsetMillis = Int

class AndroidDateTimeZoneProvider(
    logReflectiveException: ExceptionLogger
) : Provider by when {
    //SDK_INT >= 26 -> AndroidNewDateTimeZoneProvider() //TODO
    else -> AndroidOldDateTimeZoneProvider(logReflectiveException)
}

fun Application.initJodaTimeForAndroid(
    logReflectiveException: ExceptionLogger = Throwable::printStackTrace
): Closeable {
    DateTimeZone.setProvider(AndroidDateTimeZoneProvider(logReflectiveException))
    val receiver = TimeZoneChangedBroadcastReceiver()
    registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
    return Closeable { unregisterReceiver(receiver) }
}
