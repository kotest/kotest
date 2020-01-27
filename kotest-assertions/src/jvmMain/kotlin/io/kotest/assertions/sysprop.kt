package io.kotest.assertions

actual fun sysprop(name: String): String? = System.getProperty(name)
