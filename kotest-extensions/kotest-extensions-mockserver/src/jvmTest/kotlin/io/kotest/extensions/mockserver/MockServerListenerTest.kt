//package io.kotest.extensions.mockserver
//
//import io.kotest.assertions.ktor.client.shouldHaveHeader
//import io.kotest.assertions.ktor.client.shouldHaveStatus
//import io.kotest.core.spec.style.FunSpec
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.request.post
//import io.ktor.http.ContentType
//import io.ktor.http.HttpStatusCode
//import io.ktor.http.contentType
//import org.mockserver.client.MockServerClient
//import org.mockserver.model.HttpRequest
//import org.mockserver.model.HttpResponse
//
//class MockServerListenerTest : FunSpec() {
//   init {
//
//      listener(MockServerListener(1080))
//
//      beforeTest {
//         MockServerClient("localhost", 1080).`when`(
//            HttpRequest.request()
//               .withMethod("POST")
//               .withPath("/login")
//               .withHeader("Content-Type", "application/json")
//               .withBody("""{"username": "foo", "password": "bar"}""")
//         ).respond(
//            HttpResponse.response()
//               .withStatusCode(202)
//               .withHeader("X-Test", "foo")
//         )
//      }
//
//      test("login endpoint should accept username and password json") {
//         val client = HttpClient(CIO)
//         val resp = client.post<io.ktor.client.statement.HttpResponse>("http://localhost:1080/login") {
//            contentType(ContentType.Application.Json)
//            body = """{"username": "foo", "password": "bar"}"""
//         }
//         resp.shouldHaveStatus(HttpStatusCode.Accepted)
//         resp.shouldHaveHeader("X-Test", "foo")
//      }
//   }
//}
