package me.eugeniomarletti.android.joda.notzdb

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal fun <T> threadLocal(initialValue: () -> T) = ThreadLocalPropertyDelegate(initialValue)

internal class ThreadLocalPropertyDelegate<T>(
    private val defaultValue: () -> T
) :
    ThreadLocal<T>(),
    ReadWriteProperty<Any?, T> {

    override fun initialValue() = defaultValue()
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get() as T
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}
