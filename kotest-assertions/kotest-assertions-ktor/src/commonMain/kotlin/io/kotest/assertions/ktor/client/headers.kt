package io.kotest.assertions.ktor.client

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.contentType

infix fun HttpResponse.shouldHaveETag(etag: String): HttpResponse {
   this should haveHeader(HttpHeaders.ETag, etag)
   return this
}

fun HttpResponse.shouldHaveETag(): HttpResponse {
   this should haveHeader(HttpHeaders.ETag)
   return this
}

infix fun HttpResponse.shouldNotHaveETag(etag: String): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.ETag, etag)
   return this
}

fun HttpResponse.shouldNotHaveETag(): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.ETag)
   return this
}

infix fun HttpResponse.shouldHaveCacheControl(cacheControl: String): HttpResponse {
   this should haveHeader(HttpHeaders.CacheControl, cacheControl)
   return this
}

fun HttpResponse.shouldHaveCacheControl(): HttpResponse {
   this should haveHeader(HttpHeaders.CacheControl)
   return this
}

infix fun HttpResponse.shouldNotHaveCacheControl(cacheControl: String): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.CacheControl, cacheControl)
   return this
}

fun HttpResponse.shouldNotHaveCacheControl(): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.CacheControl)
   return this
}

infix fun HttpResponse.shouldHaveContentEncoding(encoding: String): HttpResponse {
   this should haveHeader(HttpHeaders.ContentEncoding, encoding)
   return this
}

fun HttpResponse.shouldHaveContentEncoding(): HttpResponse {
   this should haveHeader(HttpHeaders.ContentEncoding)
   return this
}

infix fun HttpResponse.shouldNotHaveContentEncoding(encoding: String): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.ContentEncoding, encoding)
   return this
}

fun HttpResponse.shouldNotHaveContentEncoding(): HttpResponse {
   this shouldNot haveHeader(HttpHeaders.ContentEncoding)
   return this
}

infix fun HttpResponse.shouldHaveHeader(name: String): HttpResponse {
   this should haveHeader(name)
   return this
}

fun HttpResponse.shouldHaveHeader(name: String, value: String): HttpResponse {
   this should haveHeader(name, value)
   return this
}

infix fun HttpResponse.shouldNotHaveHeader(name: String): HttpResponse {
   this shouldNot haveHeader(name)
   return this
}

fun HttpResponse.shouldNotHaveHeader(name: String, value: String): HttpResponse {
   this shouldNot haveHeader(name, value)
   return this
}

infix fun HttpResponse.shouldHaveVersion(version: HttpProtocolVersion): HttpResponse {
   this should haveVersion(version)
   return this
}

infix fun HttpResponse.shouldNotHaveVersion(version: HttpProtocolVersion): HttpResponse {
   this shouldNot haveVersion(version)
   return this
}

fun haveVersion(version: HttpProtocolVersion) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.version == version,
         { "Response should have version $version but was ${value.version}" },
         { "Response should not have version $version" },
      )
   }
}

infix fun HttpResponse.shouldHaveContentType(contentType: ContentType): HttpResponse {
   this should haveContentType(contentType)
   return this
}

infix fun HttpResponse.shouldNotHaveContentType(contentType: ContentType): HttpResponse {
   this shouldNot haveContentType(contentType)
   return this
}

fun haveContentType(contentType: ContentType) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.contentType() == contentType,
         { "Response should have Content-Type $contentType but was ${value.contentType()}." },
         { "Response should not have Content-Type $contentType." },
      )
   }
}

fun haveHeader(headerName: String) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult = MatcherResult(
      value.headers.contains(headerName),
      { "Response should have $headerName header set." },
      { "Response should not have $headerName header set." },
   )
}

fun haveHeader(headerName: String, headerValue: String) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      val actual = value.headers[headerName]
      return MatcherResult(
         actual == headerValue,
         { "Response should have $headerName: $headerValue but had: $actual" },
         { "Response should not have $headerName: $headerValue." },
      )
   }
}
