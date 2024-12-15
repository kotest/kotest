package io.kotest.assertions.ktor.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

data class HeaderTest(
   val headerName: String,
   val headerValue: String,
   val shouldExist: KFunction1<HttpResponse, Unit>,
   val shouldNotExist: KFunction1<HttpResponse, Unit>,
   val shouldHaveValue: KFunction2<HttpResponse, String, Unit>,
   val shouldNotHaveValue: KFunction2<HttpResponse, String, Unit>
)

class ResponseKtTest : StringSpec({
   withData(
      nameFn = { "Header test: ${it.headerName}" },
      HeaderTest(
         HttpHeaders.ETag,
         "some-etag-hash",
         HttpResponse::shouldHaveETag,
         HttpResponse::shouldNotHaveETag,
         HttpResponse::shouldHaveETag,
         HttpResponse::shouldNotHaveETag
      ),
      HeaderTest(
         HttpHeaders.CacheControl, "some-cache-control",
         HttpResponse::shouldHaveCacheControl,
         HttpResponse::shouldNotHaveCacheControl,
         HttpResponse::shouldHaveCacheControl,
         HttpResponse::shouldNotHaveCacheControl,
      ),
      HeaderTest(
         HttpHeaders.ContentEncoding, "UTF-8",
         HttpResponse::shouldHaveContentEncoding,
         HttpResponse::shouldNotHaveContentEncoding,
         HttpResponse::shouldHaveContentEncoding,
         HttpResponse::shouldNotHaveContentEncoding,
      )
   ) { (headerName, headerValue, shouldExist, shouldNotExist, shouldHaveValue, shouldNotHaveValue) ->
      testApplication {
         routing {
            get("/withHeader") {
               call.response.header(headerName, headerValue)
               call.respondText("with $headerName")
            }
            get("/withoutHeader") {
               call.respondText("without $headerName")
            }
         }

         client.get("/withoutHeader").apply {
            // Failure match for header without value
            shouldThrow<AssertionError> {
               shouldExist(this)
            }.message shouldBe "Response should have $headerName header set."

            // Success not match for header without value
            shouldNotExist(this)
         }

         client.get("/withHeader").apply {
            // Success match for header without value
            shouldExist(this)

            // Success match for header with value
            shouldHaveValue(this, headerValue)

            // Failure match for header with value
            shouldThrow<AssertionError> {
               shouldHaveValue(this, "otherValue")
            }.message shouldBe "Response should have $headerName: otherValue but had: $headerValue"

            // Failure not match for header without value
            shouldThrow<AssertionError> {
               shouldNotExist(this)
            }.message shouldBe "Response should not have $headerName header set."

            // Success not match for header with value
            shouldNotHaveValue(this, "otherValue")

            // Failure not match for header with value
            shouldThrow<AssertionError> {
               shouldNotHaveValue(this, headerValue)
            }.message shouldBe "Response should not have $headerName: $headerValue."
         }
      }
   }

   "For any custom header" {
      val customName = "X-Custom"
      val customValue = "custom"
      testApplication {
         routing {
            get("/with") {
               call.response.header(customName, customValue)
               call.respondText("ok")
            }
            get("/without") {
               call.respondText("ok")
            }
         }
         client.get("/with").shouldHaveHeader(customName)
         client.get("/with").shouldHaveHeader(customName, customValue)
         client.get("/with").shouldNotHaveHeader(customName, "anotherValue")
         client.get("/without").shouldNotHaveHeader(customName)
      }
   }

   "HttpVersion assertions" {
      testApplication {
         routing { get("/") { call.respondText("ok") } }
         client.get("/").apply {
            shouldHaveVersion(HttpProtocolVersion.HTTP_1_1)
            shouldNotHaveVersion(HttpProtocolVersion.HTTP_2_0)
            shouldThrow<AssertionError> {
               shouldNotHaveVersion(HttpProtocolVersion.HTTP_1_1)
            }.message shouldBe "Response should not have version HTTP/1.1"
            shouldThrow<AssertionError> {
               shouldHaveVersion(HttpProtocolVersion.HTTP_2_0)
            }.message shouldBe "Response should have version HTTP/2.0 but was HTTP/1.1"
         }
      }
   }

   "ContentType assertions" {
      testApplication {
         routing { get("/") { call.respondText("ok") } }
         client.get("/").apply {
            shouldHaveContentType(ContentType.Text.Plain.withParameter("charset", "UTF-8"))
            shouldNotHaveContentType(ContentType.Application.GZip)
            shouldThrow<AssertionError> {
               shouldNotHaveContentType(ContentType.Text.Plain.withParameter("charset", "UTF-8"))
            }.message shouldBe "Response should not have Content-Type text/plain; charset=UTF-8."
            shouldThrow<AssertionError> {
               shouldHaveContentType(ContentType.Application.GZip)
            }.message shouldBe "Response should have Content-Type application/gzip but was text/plain; charset=UTF-8."
         }
      }
   }
})
