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

infix fun HttpResponse.shouldHaveETag(etag: String) = this should haveHeader(HttpHeaders.ETag, etag)
fun HttpResponse.shouldHaveETag() = this should haveHeader(HttpHeaders.ETag)
infix fun HttpResponse.shouldNotHaveETag(etag: String) = this shouldNot haveHeader(HttpHeaders.ETag, etag)
fun HttpResponse.shouldNotHaveETag() = this shouldNot haveHeader(HttpHeaders.ETag)

infix fun HttpResponse.shouldHaveCacheControl(cacheControl: String) =
   this should haveHeader(HttpHeaders.CacheControl, cacheControl)

fun HttpResponse.shouldHaveCacheControl() = this should haveHeader(HttpHeaders.CacheControl)
infix fun HttpResponse.shouldNotHaveCacheControl(cacheControl: String) =
   this shouldNot haveHeader(HttpHeaders.CacheControl, cacheControl)


fun HttpResponse.shouldNotHaveCacheControl() = this shouldNot haveHeader(HttpHeaders.CacheControl)
infix fun HttpResponse.shouldHaveContentEncoding(encoding: String) =
   this should haveHeader(HttpHeaders.ContentEncoding, encoding)

fun HttpResponse.shouldHaveContentEncoding() = this should haveHeader(HttpHeaders.ContentEncoding)

infix fun HttpResponse.shouldNotHaveContentEncoding(encoding: String) =
   this shouldNot haveHeader(HttpHeaders.ContentEncoding, encoding)

fun HttpResponse.shouldNotHaveContentEncoding() = this shouldNot haveHeader(HttpHeaders.ContentEncoding)

infix fun HttpResponse.shouldHaveHeader(name: String) = this should haveHeader(name)
fun HttpResponse.shouldHaveHeader(name: String, value: String) = this should haveHeader(name, value)
infix fun HttpResponse.shouldNotHaveHeader(name: String) = this shouldNot haveHeader(name)
fun HttpResponse.shouldNotHaveHeader(name: String, value: String) = this shouldNot haveHeader(name, value)

infix fun HttpResponse.shouldHaveVersion(version: HttpProtocolVersion) = this should haveVersion(version)
infix fun HttpResponse.shouldNotHaveVersion(version: HttpProtocolVersion) = this shouldNot haveVersion(version)
fun haveVersion(version: HttpProtocolVersion) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.version == version,
         { "Response should have version $version but was ${value.version}" },
         { "Response should not have version $version" },
      )
   }
}

fun HttpResponse.shouldHaveContentType(contentType: ContentType) = this should haveContentType(contentType)
fun HttpResponse.shouldNotHaveContentType(contentType: ContentType) = this shouldNot haveContentType(contentType)
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
