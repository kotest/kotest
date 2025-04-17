package io.kotest.assertions.ktor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.nio.charset.Charset

fun Application.testableModule() {
   intercept(ApplicationCallPipeline.Call) {
      if (call.request.uri == "/") {
         call.response.header("wibble", "wobble")
         call.response.cookies.append("mycookie", "myvalue", maxAge = 10L, domain = "foo.com", path = "/bar")
         call.respondText("ok")
      }
   }
}

class KtorAssertionsTest : StringSpec({

   "test status code matcher" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            response shouldHaveStatus 200
         }
      }
   }

   "test status code matcher (enum version)" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            response shouldHaveStatus HttpStatusCode.OK
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

   "test headers matcher fail" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            shouldThrow<AssertionError> {
               response.shouldHaveHeader("fail_name", "fail_value")
            }.message shouldBe "Response should have header fail_name=fail_value but fail_name=null"

            shouldThrow<AssertionError> {
               response.shouldNotHaveHeader("wibble", "wobble")
            }.message shouldBe "Response should not have header wibble=wobble"
         }
      }
   }

   "test content matcher" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            response shouldHaveContent "ok"
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

   "test content type" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            response.shouldHaveContentType(ContentType.Text.Plain.withCharset(Charset.forName("UTF8")))
         }
      }
   }

   "test content type fail" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/").apply {
            shouldThrow<AssertionError> {
               response.shouldHaveContentType(ContentType.Any)
            }.message shouldBe "Response should have ContentType */* but was text/plain; charset=UTF-8"

            shouldThrow<AssertionError> {
               response.shouldNotHaveContentType(ContentType.Text.Plain.withCharset(Charset.forName("UTF8")))
            }.message shouldBe "Response should not have ContentType text/plain; charset=UTF-8"
         }
      }
   }

   "test null response doesn't end with KotlinNullpointerException" {
      withTestApplication({ testableModule() }) {
         handleRequest(HttpMethod.Get, "/not-mapped").apply {
            response.content shouldBe null
            shouldThrow<AssertionError> {
               response shouldHaveContent "fail"
            }
         }
      }
   }
})
