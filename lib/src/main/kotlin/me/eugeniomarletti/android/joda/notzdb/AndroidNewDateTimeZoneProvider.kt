package me.eugeniomarletti.android.joda.notzdb

//package com.helloclue.android.joda
//
//import android.annotation.TargetApi
//import org.joda.time.DateTimeZone
//import org.joda.time.tz.Provider
//import java.util.Locale
//
//@TargetApi(26)
//internal class AndroidNewDateTimeZoneProvider : Provider {
//    override fun getAvailableIDs(): Set<TimeZoneId> = TODO()
//    override fun getZone(id: TimeZoneId?): DateTimeZone? =
//        when (id) {
//            null -> null
//            "UTC" -> DateTimeZone.UTC
//            else -> AndroidNewDateTimeZone(id)
//        }
//}
//
//@TargetApi(26)
//private class AndroidNewDateTimeZone(
//    id: TimeZoneId
//) : DateTimeZone(id) {
//    private val tz = Any() //TODO
//
//    override fun previousTransition(instant: UnixMillis): UnixMillis = TODO()
//
//    override fun nextTransition(instant: UnixMillis): UnixMillis = TODO()
//
//    override fun isFixed() = TODO()
//
//    override fun isStandardOffset(instant: UnixMillis) = TODO()
//
//    override fun getStandardOffset(instant: UnixMillis): OffsetMillis = TODO()
//
//    override fun getOffset(instant: UnixMillis): OffsetMillis = TODO()
//
//    override fun getShortName(instant: Long, locale: Locale?) = TODO()
//
//    override fun getName(instant: UnixMillis, locale: Locale?) = TODO()
//
//    override fun getNameKey(instant: UnixMillis): String? = null
//
//    override fun toTimeZone() = TODO()
//
//    override fun toString() = "${javaClass.simpleName}($tz)"
//
//    override fun equals(other: Any?) =
//        when {
//            other === this -> true
//            other !is AndroidNewDateTimeZone -> false
//            other.tz != tz -> false
//            else -> true
//        }
//
//    override fun hashCode() = 31 * super.hashCode() + tz.hashCode()
//}
