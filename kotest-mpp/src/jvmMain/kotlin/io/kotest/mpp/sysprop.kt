@file:JvmName("syspropjvm")

package io.kotest.mpp

actual fun sysprop(name: String): String? = System.getProperty(name)
