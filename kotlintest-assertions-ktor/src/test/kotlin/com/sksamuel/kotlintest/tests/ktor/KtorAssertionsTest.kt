package com.sksamuel.kotlintest.tests.ktor

import io.kotlintest.assertions.ktor.shouldHaveContent
import io.kotlintest.assertions.ktor.shouldHaveHeader
import io.kotlintest.assertions.ktor.shouldHaveStatus
import io.kotlintest.specs.StringSpec
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.uri
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

fun Application.testableModule() {
  intercept(ApplicationCallPipeline.Call) {
    if (call.request.uri == "/") {
      call.response.header("wibble", "wobble")
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
})