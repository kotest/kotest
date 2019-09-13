package com.sksamuel.kotest.tests.ktor

import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.ktor.shouldHaveCookie
import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.specs.StringSpec
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

fun Application.testableModule() {
  intercept(ApplicationCallPipeline.Call) {
    if (call.request.uri == "/") {
      call.response.header("wibble", "wobble")
      call.response.cookies.append("mycookie", "myvalue", maxAge = 10, domain = "foo.com", path = "/bar")
      call.respondText("ok")
    }
  }
}

class KtorAssertionsTest : StringSpec({

  "test status code matcher" {
    withTestApplication({ testableModule() }) {
      handleRequest(HttpMethod.Get, "/").apply {
        response.shouldHaveStatus(200)
      }
    }
  }

  "test status code matcher (enum version)" {
    withTestApplication({ testableModule() }) {
      handleRequest(HttpMethod.Get, "/").apply {
        response.shouldHaveStatus(HttpStatusCode.OK)
      }
    }
  }

  "test headers matcher" {
    withTestApplication({ testableModule() }) {
      handleRequest(HttpMethod.Get, "/").apply {
        response.shouldHaveHeader("wibble", "wobble")
      }
    }
  }

  "test content matcher" {
    withTestApplication({ testableModule() }) {
      handleRequest(HttpMethod.Get, "/").apply {
        response.shouldHaveContent("ok")
      }
    }
  }

  "test cookie values" {
    withTestApplication({ testableModule() }) {
      handleRequest(HttpMethod.Get, "/").apply {
        response.shouldHaveCookie("mycookie", "myvalue")
      }
    }
  }
})