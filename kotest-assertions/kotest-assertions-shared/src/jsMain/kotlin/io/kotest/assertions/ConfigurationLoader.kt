package io.kotest.assertions

internal actual object ConfigurationLoader {
   actual fun getValue(name: String): String? = null // Environment variables aren't (yet) supported on Kotlin/JS.
   actual fun getSourceDescription(name: String): String? = null
}
