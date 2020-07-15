package io.kotest.extensions.http

import io.ktor.client.statement.HttpResponse

suspend fun http(resource: String, f: suspend (HttpResponse) -> Unit) {
   http(resource, mapOf(), f)
}

suspend fun http(resource: String, parameters: Map<String, String>, f: suspend (HttpResponse) -> Unit) {
   val lines = object : Any() {}.javaClass.getResourceAsStream(resource).use { it.bufferedReader().readLines() }
      .map { line ->
         parameters.entries.fold(line) { acc, op -> acc.replace("{{${op.key}}}", op.value) }
      }
   val req = parseHttpRequest(lines)
   val resp = runRequest(req)
   f(resp)
}
