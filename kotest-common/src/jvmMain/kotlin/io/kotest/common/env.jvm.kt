@file:JvmName("syspropjvm")

package io.kotest.common

actual fun sysprop(name: String): String? = System.getProperty(name)
actual fun env(name: String): String? = System.getenv(name)
