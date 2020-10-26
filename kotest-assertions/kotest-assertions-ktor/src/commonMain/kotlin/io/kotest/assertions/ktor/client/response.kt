package io.kotest.assertions.ktor.client

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

infix fun HttpResponse.shouldHaveStatus(httpStatusCode: HttpStatusCode) = shouldHaveStatus(httpStatusCode.value)
infix fun HttpResponse.shouldHaveStatus(code: Int) = this should haveStatus(code)
infix fun HttpResponse.shouldNotHaveStatus(httpStatusCode: HttpStatusCode) = shouldNotHaveStatus(httpStatusCode.value)
infix fun HttpResponse.shouldNotHaveStatus(code: Int) = this shouldNot haveStatus(code)
fun haveStatus(code: Int) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.status.value == code,
         "Response should have status $code but had status ${value.status.value}. Response body: ${value.content}",
         "Response should not have status $code. Response body: ${value.content}"
      )
   }
}

infix fun HttpResponse.shouldHaveVersion(version: HttpProtocolVersion) = this should haveVersion(version)
infix fun HttpResponse.shouldNotHaveVersion(version: HttpProtocolVersion) = this shouldNot haveVersion(version)
fun haveVersion(version: HttpProtocolVersion) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.version == version,
         "Response should have version $version but had version ${value.version}",
         "Response should not have version $version"
      )
   }
}

fun HttpResponse.shouldHaveHeader(name: String, value: String) = this should haveHeader(name, value)
fun HttpResponse.shouldNotHaveHeader(name: String, value: String) = this shouldNot haveHeader(name, value)
fun haveHeader(headerName: String, headerValue: String) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.headers[headerName] == headerValue,
         "Response should have header $headerName=$value but $headerName=${value.headers[headerName]}",
         "Response should not have header $headerName=$value"
      )
   }
}

fun HttpResponse.shouldHaveContentType(contentType: ContentType) = this should haveContentType(contentType)
fun HttpResponse.shouldNotHaveContentType(contentType: ContentType) = this shouldNot haveContentType(contentType)
fun haveContentType(contentType: ContentType) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.contentType() == contentType,
         "Response should have ContentType $contentType= but was ${value.contentType()}",
         "Response should not have ContentType $contentType"
      )
   }
}
