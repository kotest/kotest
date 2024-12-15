package io.kotest.assertions.ktor

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.ApplicationResponse
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.contentType

infix fun TestApplicationResponse.shouldHaveETag(etag: String): TestApplicationResponse {
   this should haveETag(etag)
   return this
}

infix fun TestApplicationResponse.shouldNotHaveETag(etag: String): TestApplicationResponse {
   this shouldNot haveETag(etag)
   return this
}

fun haveETag(expected: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      val actual = value.headers[HttpHeaders.ETag]
      return MatcherResult(
         actual == expected,
         { "Response should have ETag $expected but had ${actual}." },
         { "Response should not have ETag $expected." },
      )
   }
}

infix fun TestApplicationResponse.shouldHaveCacheControl(cacheControl: String): TestApplicationResponse {
   this should haveCacheControl(cacheControl)
   return this
}

infix fun TestApplicationResponse.shouldNotCacheControl(cacheControl: String): TestApplicationResponse {
   this shouldNot haveCacheControl(cacheControl)
   return this
}

fun haveCacheControl(expected: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      val actual = value.headers[HttpHeaders.CacheControl]
      return MatcherResult(
         actual == expected,
         { "Response should have Cache-Control: $expected but had: ${actual}." },
         { "Response should not have Cache-Control $expected." }
      )
   }
}

infix fun TestApplicationResponse.shouldHaveContentEncoding(encoding: String): TestApplicationResponse {
   this should haveContentEncoding(encoding)
   return this
}

infix fun TestApplicationResponse.shouldNotHaveContentEncoding(encoding: String): TestApplicationResponse {
   this shouldNot haveContentEncoding(encoding)
   return this
}

fun haveContentEncoding(expected: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      val actual = value.headers[HttpHeaders.ContentEncoding]
      return MatcherResult(
         actual == expected,
         { "Response should have Content-Encoding: $expected but had: ${actual}." },
         { "Response should not have Content-Encoding $expected." },
      )
   }
}

infix fun TestApplicationResponse.shouldHaveStatus(httpStatusCode: HttpStatusCode): TestApplicationResponse {
   this.shouldHaveStatus(httpStatusCode.value)
   return this
}

infix fun TestApplicationResponse.shouldHaveStatus(code: Int): TestApplicationResponse {
   this should haveStatus(code)
   return this
}

infix fun TestApplicationResponse.shouldNotHaveStatus(httpStatusCode: HttpStatusCode): TestApplicationResponse {
   this.shouldNotHaveStatus(httpStatusCode.value)
   return this
}

infix fun TestApplicationResponse.shouldNotHaveStatus(code: Int): TestApplicationResponse {
   this shouldNot haveStatus(code)
   return this
}

fun haveStatus(code: Int) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.status()?.value == code,
         { "Response should have status $code but had status ${value.status()?.value}." },
         { "Response should not have status $code." },
      )
   }
}

infix fun TestApplicationResponse.shouldHaveContent(content: String): TestApplicationResponse {
   this should haveContent(content)
   return this
}

infix fun TestApplicationResponse.shouldNotHaveContent(content: String): TestApplicationResponse {
   this shouldNot haveContent(content)
   return this
}

fun haveContent(content: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.content == content,
         { "Response should have content $content but had content ${value.content}" },
         { "Response should not have content $content" },
      )
   }
}

fun TestApplicationResponse.shouldHaveContentType(contentType: ContentType): TestApplicationResponse {
   this should haveContentType(contentType)
   return this
}

fun TestApplicationResponse.shouldNotHaveContentType(contentType: ContentType): TestApplicationResponse {
   this shouldNot haveContentType(contentType)
   return this
}

fun haveContentType(contentType: ContentType) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.contentType() == contentType,
         { "Response should have ContentType $contentType but was ${value.contentType()}" },
         { "Response should not have ContentType $contentType" },
      )
   }
}

fun TestApplicationResponse.shouldHaveHeader(name: String, value: String): TestApplicationResponse {
   this should haveHeader(name, value)
   return this
}

fun TestApplicationResponse.shouldNotHaveHeader(name: String, value: String): TestApplicationResponse {
   this shouldNot haveHeader(name, value)
   return this
}

fun haveHeader(headerName: String, headerValue: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.headers[headerName] == headerValue,
         { "Response should have header $headerName=$headerValue but $headerName=${value.headers[headerName]}" },
         { "Response should not have header $headerName=$headerValue" },
      )
   }
}

fun TestApplicationResponse.shouldHaveCookie(name: String, cookieValue: String? = null): TestApplicationResponse {
   this should haveCookie(name, cookieValue)
   return this
}

fun TestApplicationResponse.shouldNotHaveCookie(name: String, cookieValue: String? = null): TestApplicationResponse {
   this shouldNot haveCookie(name, cookieValue)
   return this
}

fun haveCookie(name: String, cookieValue: String? = null) = object : Matcher<ApplicationResponse> {
   override fun test(value: ApplicationResponse): MatcherResult {

      val passed = when (cookieValue) {
         null -> value.cookies[name] != null
         else -> value.cookies[name]?.value == cookieValue
      }

      return MatcherResult(
         passed,
         { "Response should have cookie with name $name" },
         { "Response should have cookie with name $name" },
      )
   }
}
