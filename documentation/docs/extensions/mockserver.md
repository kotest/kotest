---
id: mockserver
title: MockServer
sidebar_label: MockServer
slug: mockserver.html
---


Kotest provides an [extension](https://github.com/kotest/kotest-extensions-mockserver) for integration with the [MockServer](https://www.mock-server.com) library.

:::note
Requires the `io.kotest.extensions:kotest-extensions-mockserver` module to be added to your build.
:::


[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-mockserver.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-mockserver)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-mockserver.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-mockserver/)



Mockserver allows us to define an in process HTTP server which is hard coded for routes that we want to test against.

To use in Kotest, we attach an instance of `MockServerListener` to the spec under test, and Kotest will control
the lifecycle automatically.

Then it is a matter of using `MockServerClient` to wire in our responses.

For example:

```kotlin
class MyMockServerTest : FunSpec() {
  init {

      // this attaches the server to the lifeycle of the spec
      listener(MockServerListener(1080))

      // we can use the client to create routes. Here we are setting them up
      // before each test by using the beforeTest callback.
      beforeTest {
         MockServerClient("localhost", 1080).`when`(
            HttpRequest.request()
               .withMethod("POST")
               .withPath("/login")
               .withHeader("Content-Type", "application/json")
               .withBody("""{"username": "foo", "password": "bar"}""")
         ).respond(
            HttpResponse.response()
               .withStatusCode(202)
               .withHeader("X-Test", "foo")
         )
      }

      // this test will confirm the endpoint works
      test("login endpoint should accept username and password json") {

         // using the ktor client to send requests
         val client = HttpClient(CIO)
         val resp = client.post<io.ktor.client.statement.HttpResponse>("http://localhost:1080/login") {
            contentType(ContentType.Application.Json)
            body = """{"username": "foo", "password": "bar"}"""
         }

         // these handy matchers come from the kotest-assertions-ktor module
         resp.shouldHaveStatus(HttpStatusCode.Accepted)
         resp.shouldHaveHeader("X-Test", "foo")
      }
  }
}
```

In the above example, we are of course just testing the mock itself, but it shows how a real test could be configured. For example,
you may have an API client that you want to test, so you would configure the API routes using mock server, and then invoke methods
on your API client, ensuring it handles the responses correctly.


