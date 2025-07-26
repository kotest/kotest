package io.kotest.common

fun sysprop(key: String, default: String): String = sysprop(key) ?: default
fun <T> sysprop(key: String, default: T, converter: (String) -> T): T = sysprop(key)?.let { converter(it) } ?: default
fun sysprop(key: String, default: Int): Int = sysprop(key, default) { it.toInt() }
fun sysprop(key: String, default: Double): Double = sysprop(key, default) { it.toDouble() }
fun sysprop(key: String, default: Boolean): Boolean = sysprop(key, default) { it == "true" }

expect fun sysprop(name: String): String?
expect fun env(name: String): String?

fun syspropOrEnv(name: String): String? = sysprop(name) ?: env(name) ?: env(syspropNameToSafeEnvironmentVariableName(name))
private fun syspropNameToSafeEnvironmentVariableName(name: String): String = name.replace('.', '_')
