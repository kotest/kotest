package io.kotest.assertions.ktor.client

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

infix fun HttpResponse.shouldHaveStatus(httpStatusCode: HttpStatusCode) = shouldHaveStatus(httpStatusCode.value)
infix fun HttpResponse.shouldHaveStatus(code: Int) = this should haveStatus(code)
infix fun HttpResponse.shouldNotHaveStatus(httpStatusCode: HttpStatusCode) = shouldNotHaveStatus(httpStatusCode.value)
infix fun HttpResponse.shouldNotHaveStatus(code: Int) = this shouldNot haveStatus(code)
fun haveStatus(expected: Int) = object : Matcher<HttpResponse> {
   override fun test(value: HttpResponse): MatcherResult {
      return MatcherResult(
         value.status.value == expected,
         { "Response should have status $expected but had status ${value.status.value}." },
         { "Response should not have status $expected." },
      )
   }
}

fun HttpResponse.shouldBeOK() = this.shouldHaveStatus(HttpStatusCode.OK)
fun HttpResponse.shouldBeInternalServerError() = this.shouldHaveStatus(HttpStatusCode.InternalServerError)
fun HttpResponse.shouldBeBadRequest() = this.shouldHaveStatus(HttpStatusCode.BadRequest)
fun HttpResponse.shouldBeNotFound() = this.shouldHaveStatus(HttpStatusCode.NotFound)
