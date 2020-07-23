package io.kotest.extensions.http

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.utils.io.core.use

fun parseHttpRequest(lines: List<String>): HttpRequestBuilder {
   val builder = HttpRequestBuilder()

   val method = HttpMethod.DefaultMethods.find { lines[0].startsWith(it.value) } ?: HttpMethod.Get
   builder.method = method

   val url = lines[0].removePrefix(method.value).trim()
   builder.url(url)

   lines.drop(1).takeWhile { it.isNotBlank() }.map {
      val (key, value) = it.split(':')
      builder.header(key, value)
   }

   val body = lines.drop(1).dropWhile { it.isNotBlank() }.joinToString("\n").trim()
   builder.body = body

   return builder
}

suspend fun runRequest(req: HttpRequestBuilder): HttpResponse {
   return HttpClient().use {
      it.request<HttpResponse>(req)
   }
}
