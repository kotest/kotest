---
id: wiremock
title: WireMock
sidebar_label: WireMock
slug: wiremock.html
---

## WireMock

[WireMock](https://github.com/tomakehurst/wiremock) is a library which provides HTTP response stubbing, matchable on
URL, header and body content patterns etc.

```kotest-extensions-wiremock``` provides integration for using wiremock with kotest.

For using ```kotest-extensions-wiremock``` add the below dependency in your build file.

```groovy
testImplementation("io.kotest:kotest-extensions-wiremock:${version}")
```

Having this dependency in test classpath brings in extension method's in scope which let you convert a WireMockServer
into a kotest TestListener, which you can then register with Kotest and then Kotest will manage lifecycle of
WireMockServer for you.

For example:

```kotlin

class SomeTest : FunSpec({
  val customerServiceServer = WireMockServer(9000)
  listener(customerServiceServer.listenerPerSpec()) //converts WireMockServer to WiremockPerSpecListener

  test("let me get customer information") {
    customerServiceServer.stubFor(
      WireMock.get(WireMock.urlEqualTo("/customers/123"))
        .willReturn(WireMock.ok())
    )

    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection
    connection.responseCode shouldBe 200
  }
})
```

In above example ```listenerPerSpec()``` extension method converts the WireMockServer into a ```WiremockPerSpecListener```
which start's the WireMockServer before starting any test spec and stop's that after all test are completed.

In case you want to stop/start your WireMockServer before/after each test use ```listenerPerTest()``` extension method
with converts the WireMockServer into a ```WiremockPerTestListener```.
