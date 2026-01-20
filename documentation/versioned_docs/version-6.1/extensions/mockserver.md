---
id: mockserver
title: MockServer
sidebar_label: MockServer
slug: mockserver.html
---


Kotest provides an [extension](https://github.com/kotest/kotest/tree/master/kotest-extensions/kotest-extensions-mockserver) for integration with the [MockServer](https://www.mock-server.com) library.

:::note
Requires the `io.kotest:kotest-extensions-mockserver` module to be added to your build.
:::

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group, with version cadence tied to main Kotest releases.
:::


[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-mockserver.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-mockserver)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-mockserver%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-mockserver/maven-metadata.xml)



Mockserver allows us to define an in process HTTP server which is hard coded for routes that we want to test against.

To use in Kotest, we install an instance of `MockServerExtension` in the spec under test, and Kotest will control
the lifecycle automatically.

Then it is a matter of using `MockServerClient` to wire in our responses.

For example:

```kotlin
class MyMockServerTest : FunSpec() {
  init {

      // this attaches the server to the lifeycle of the spec
      install(MockServerExtension(1080))

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

## Dynamic Ports

When using the `MockServerExtension`, you can specify one or more ports if you wish to hardcore them. Otherwise, you can
not specify them at all, and Kotest will automatically allocate a free port for the server to run on. Then, you can use
the returned server instance from the install function to retrieve the allocated port.

Here is an example of using dynamic ports:

```kotlin
class MyMockServerTest : FunSpec() {
  init {

    val server = install(MockServerExtension())

    beforeTest {
      MockServerClient("localhost", server.port).`when`(
        HttpRequest.request()
          .withMethod("GET")
          .withPath("/v")
      ).respond(
        HttpResponse.response()
          .withStatusCode(200)
      )
    }

    test("test /health returns 200") {

      val client = HttpClient(CIO)
      val resp = client.post<io.ktor.client.statement.HttpResponse>("http://localhost:${healthcheck.port}/health")
      resp.shouldHaveStatus(HttpStatusCode.OK)
    }
  }
}
```
