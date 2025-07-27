package io.kotest.assertions

import io.kotest.common.env

internal actual object ConfigurationLoader {
   actual fun getValue(name: String): String? = env(environmentVariableName(name))
   actual fun getSourceDescription(name: String): String? = "the '${environmentVariableName(name)}' environment variable"

   private fun environmentVariableName(name: String): String = name.replace(".", "_")
}
