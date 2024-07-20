@file:Suppress("DEPRECATION") // Remove when removing http extension

package io.kotest.extensions.http

import io.ktor.client.statement.HttpResponse

@Deprecated("Obsolete, has never been advertised or documented. Deprecated since 5.9.0")
suspend fun http(resource: String, f: suspend (HttpResponse) -> Unit) {
   http(resource, mapOf(), f)
}

@Deprecated("Obsolete, has never been advertised or documented. Deprecated since 5.9.0")
suspend fun http(resource: String, parameters: Map<String, String>, f: suspend (HttpResponse) -> Unit) {
   http(resource, parameters, null, f)
}

@Deprecated("Obsolete, has never been advertised or documented. Deprecated since 5.9.0")
suspend fun http(resource: String, parameters: Map<String, String>, timeout: Long?, f: suspend (HttpResponse) -> Unit) {
   val lines = object : Any() {}.javaClass.getResourceAsStream(resource).use { it.bufferedReader().readLines() }
      .map { line ->
         parameters.entries.fold(line) { acc, op -> acc.replace("{{${op.key}}}", op.value) }
      }
   val req = parseHttpRequest(lines)
   val resp = when{
      timeout != null -> runRequest(req, timeout)
      else -> runRequest(req)
   }
   f(resp)
}
