package io.kotest.extensions.http

import io.ktor.client.statement.HttpResponse

suspend fun http(resource: String, f: (HttpResponse) -> Unit) {
   val lines = object : Any() {}.javaClass.getResourceAsStream(resource).use { it.bufferedReader().readLines() }
   val req = parseHttpRequest(lines)
   val resp = runRequest(req)
   f(resp)
}
