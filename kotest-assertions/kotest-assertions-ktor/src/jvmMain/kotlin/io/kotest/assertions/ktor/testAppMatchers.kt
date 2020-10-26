package io.kotest.assertions.ktor

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.ApplicationResponse
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.contentType

infix fun TestApplicationResponse.shouldHaveStatus(httpStatusCode: HttpStatusCode) = this.shouldHaveStatus(httpStatusCode.value)
infix fun TestApplicationResponse.shouldHaveStatus(code: Int) = this should haveStatus(code)
infix fun TestApplicationResponse.shouldNotHaveStatus(httpStatusCode: HttpStatusCode) = this.shouldNotHaveStatus(httpStatusCode.value)
infix fun TestApplicationResponse.shouldNotHaveStatus(code: Int) = this shouldNot haveStatus(code)
fun haveStatus(code: Int) = object : Matcher<TestApplicationResponse> {
  override fun test(value: TestApplicationResponse): MatcherResult {
    return MatcherResult(
        value.status()?.value == code,
        "Response should have status $code but had status ${value.status()?.value}. Response body: ${value.content}",
        "Response should not have status $code. Response body: ${value.content}"
    )
  }
}

infix fun TestApplicationResponse.shouldHaveContent(content: String) = this should haveContent(content)
infix fun TestApplicationResponse.shouldNotHaveContent(content: String) = this shouldNot haveContent(content)
fun haveContent(content: String) = object : Matcher<TestApplicationResponse> {
  override fun test(value: TestApplicationResponse): MatcherResult {
    return MatcherResult(
        value.content == content,
        "Response should have content $content but had content ${value.content}",
        "Response should not have content $content"
    )
  }
}

fun TestApplicationResponse.shouldHaveContentType(contentType: ContentType) =
   this should haveContentType(contentType)

fun TestApplicationResponse.shouldNotHaveContentType(contentType: ContentType) =
   this shouldNot haveContentType(contentType)

fun haveContentType(contentType: ContentType) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.contentType() == contentType,
         "Response should have ContentType $contentType= but was ${value.contentType()}",
         "Response should not have ContentType $contentType"
      )
   }
}

fun TestApplicationResponse.shouldHaveHeader(name: String, value: String) = this should haveHeader(name, value)
fun TestApplicationResponse.shouldNotHaveHeader(name: String, value: String) = this shouldNot haveHeader(name, value)
fun haveHeader(headerName: String, headerValue: String) = object : Matcher<TestApplicationResponse> {
   override fun test(value: TestApplicationResponse): MatcherResult {
      return MatcherResult(
         value.headers[headerName] == headerValue,
         "Response should have header $headerName=$value but $headerName=${value.headers[headerName]}",
         "Response should not have header $headerName=$value"
      )
   }
}

fun TestApplicationResponse.shouldHaveCookie(name: String, cookieValue: String? = null) = this should haveCookie(name, cookieValue)
fun TestApplicationResponse.shouldNotHaveCookie(name: String, cookieValue: String? = null) = this shouldNot haveCookie(name, cookieValue)
fun haveCookie(name: String, cookieValue: String? = null) = object : Matcher<ApplicationResponse> {
  override fun test(value: ApplicationResponse): MatcherResult {

    val passed = when (cookieValue) {
      null -> value.cookies[name] != null
      else -> value.cookies[name]?.value == cookieValue
    }

    return MatcherResult(
        passed,
        "Response should have cookie with name $name",
        "Response should have cookie with name $name"
    )
  }
}
