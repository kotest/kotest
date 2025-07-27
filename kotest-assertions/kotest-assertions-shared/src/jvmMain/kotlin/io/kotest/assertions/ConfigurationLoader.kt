package io.kotest.assertions

internal actual object ConfigurationLoader {
   actual fun getValue(name: String): String? = System.getProperty(name)
   actual fun getSourceDescription(name: String): String? = "the '$name' JVM property"
}
