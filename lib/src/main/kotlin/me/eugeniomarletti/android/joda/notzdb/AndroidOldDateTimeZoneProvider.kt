package me.eugeniomarletti.android.joda.notzdb

import org.joda.time.DateTimeZone
import org.joda.time.tz.Provider
import java.lang.reflect.Field
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

private typealias UnixSeconds = Long
private typealias UnixSecondsArray = LongArray

private typealias TransitionIndex = Int

internal class AndroidOldDateTimeZoneProvider(
    private val logReflectiveException: ExceptionLogger
) : Provider {
    override fun getAvailableIDs(): Set<TimeZoneId> = TimeZone.getAvailableIDs().toSet()
    override fun getZone(id: TimeZoneId?): DateTimeZone? =
        when (id) {
            null -> null
            "UTC" -> DateTimeZone.UTC
            else -> AndroidOldDateTimeZone(id, logReflectiveException)
        }
}

private class AndroidOldDateTimeZone(
    id: TimeZoneId,
    private val logReflectiveException: ExceptionLogger
) : DateTimeZone(id) {
    private val tz: TimeZone by threadLocal { TimeZone.getTimeZone(id) }

    private val calendar: Calendar by threadLocal { GregorianCalendar.getInstance(tz) }

    private val transitions: UnixSecondsArray by threadLocal {
        val tzClass = tz.javaClass
        try {
            // see ZoneInfo.java (libcore)
            tzClass
                .let { `class` ->
                    fieldCache.getOrPut(
                        key = `class`,
                        defaultValue = { `class`.getDeclaredField("mTransitions").apply { isAccessible = true } }
                    )
                }
                .get(tz)
                .let { it as? LongArray }
                ?: LongArray(0)
        }
        catch (e: Exception) {
            if (tzClass.canonicalName == "libcore.util.ZoneInfo") {
                // unexpected exception!
                when (e) {
                    is NoSuchFieldException,
                    is IllegalAccessException,
                    is SecurityException
                    -> logReflectiveException(e)
                }
            }
            LongArray(0)
        }
    }

    override fun previousTransition(instant: UnixMillis): UnixMillis =
        when {
            transitions.isEmpty() -> instant
            else -> transitions.findTransitionIndex(instant).let { index ->
                val firstIndex = 0
                val lastIndex = transitions.lastIndex
                when {
                    index <= firstIndex -> instant
                    else -> transitions[index.coerceIn(firstIndex + 1, lastIndex + 1) - 1] * 1000
                }
            }
        }

    override fun nextTransition(instant: UnixMillis): UnixMillis =
        when {
            transitions.isEmpty() -> instant
            else -> transitions.findTransitionIndex(instant).let { index ->
                val firstIndex = 0
                val lastIndex = transitions.lastIndex
                when {
                    index >= lastIndex -> instant
                    else -> transitions[index.coerceIn(firstIndex - 1, lastIndex - 1) + 1] * 1000
                }
            }
        }

    override fun isFixed() = transitions.isEmpty() &&
        calendar.run {
            Calendar.DST_OFFSET.let { getMinimum(it) == getMaximum(it) } &&
                Calendar.ZONE_OFFSET.let { getMinimum(it) == getMaximum(it) }
        }

    override fun isStandardOffset(instant: UnixMillis) =
        calendar.run {
            timeInMillis = instant
            get(Calendar.DST_OFFSET) == 0
        }

    override fun getStandardOffset(instant: UnixMillis): OffsetMillis =
        calendar.run {
            timeInMillis = instant
            get(Calendar.ZONE_OFFSET)
        }

    override fun getOffset(instant: UnixMillis): OffsetMillis = tz.getOffset(instant)

    override fun getShortName(instant: Long, locale: Locale?) = getName(instant, locale, short = true)

    override fun getName(instant: UnixMillis, locale: Locale?) = getName(instant, locale, short = false)

    private fun getName(instant: UnixMillis, locale: Locale?, short: Boolean): String =
        tz.getDisplayName(
            !isStandardOffset(instant),
            when (short) {
                true -> TimeZone.SHORT
                false -> TimeZone.LONG
            },
            locale ?: Locale.getDefault()
        )

    override fun getNameKey(instant: UnixMillis): String? = null

    override fun toTimeZone() = tz.clone() as TimeZone

    override fun toString() = "${javaClass.simpleName}($tz)"

    override fun equals(other: Any?) =
        when {
            other === this -> true
            other !is AndroidOldDateTimeZone -> false
            other.tz != tz -> false
            else -> true
        }

    override fun hashCode() = 31 * super.hashCode() + tz.hashCode()

    companion object {
        private val fieldCache: MutableMap<Class<*>, Field> by threadLocal(::mutableMapOf)

        /**
         * Find the transition in the timezone in effect at [instant].
         *
         * Returns an index in the range `-1..transitions.lastIndex`.
         * `-1` is used to indicate the time is before the first transition.
         * Other values are an index into [transitions].
         */
        // adapted from ZoneInfo.java (libcore)
        private fun UnixSecondsArray.findTransitionIndex(instant: UnixMillis): TransitionIndex =
            binarySearch(roundDownMillisToSeconds(instant))
                .let { index -> index.takeIf { it >= 0 } ?: (index.inv() - 1).takeIf { it >= 0 } ?: -1 }

        /**
         * Converts time in milliseconds into a time in seconds, rounding down to the closest time
         * in seconds before the time in milliseconds.
         *
         * It's not sufficient to simply divide by 1000 because that rounds towards 0 and so while
         * for positive numbers it produces a time in seconds that precedes the time in milliseconds
         * for negative numbers it can produce a time in seconds that follows the time in milliseconds.
         *
         * This basically does the same as `(long) Math.floor(millis / 1000.0)` but should be faster.
         *
         * @param millis the time in milliseconds, may be negative.
         * @return the time in seconds.
         */
        // adapted from ZoneInfo.java (libcore)
        private fun roundDownMillisToSeconds(millis: UnixMillis): UnixSeconds =
            when {
                // If the time is less than zero then subtract 999 and then divide by 1000 rounding
                // towards 0 as usual, e.g.
                // -12345 -> -13344 / 1000 = -13
                // -12000 -> -12999 / 1000 = -12
                // -12001 -> -13000 / 1000 = -13
                millis < 0 -> (millis - 999) / 1000
                else -> millis / 1000
            }
    }
}
