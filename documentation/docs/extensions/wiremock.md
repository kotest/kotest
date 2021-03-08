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

Having this dependency in test classpath brings in WireMockListener in scope. WireMockListener manage life cycle of
WireMockServer during your test.

For example:

```kotlin

class SomeTest : FunSpec({
  val customerServiceServer = WireMockServer(9000)
  listener(WireMockListener(customerServiceServer, ListenerMode.PER_SPEC))

  test("let me get customer information") {
    customerServiceServer.stubFor(
      WireMock.get(WireMock.urlEqualTo("/customers/123"))
        .willReturn(WireMock.ok())
    )

    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection
    connection.responseCode shouldBe 200
  }

    //  ------------OTHER TEST BELOW ----------------
})
```

In above example we created an instance of `WireMockListener` which starts given `WireMockServer` before running any test in
spec and stops it after completing all test in the spec. You can use `WireMockServer.perSpec(customerServiceServer)` to
achieve same result.

```kotlin

class SomeTest : FunSpec({
  val customerServiceServer = WireMockServer(9000)
  listener(WireMockListener(customerServiceServer, ListenerMode.PER_TEST))

  test("let me get customer information") {
    customerServiceServer.stubFor(
      WireMock.get(WireMock.urlEqualTo("/customers/123"))
        .willReturn(WireMock.ok())
    )

    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection
    connection.responseCode shouldBe 200
  }

  //  ------------OTHER TEST BELOW ----------------
})
```


In above example we created an instance of `WireMockListener` which starts given `WireMockServer` before running every test in
spec and stops it after completing every test in the spec. You can use `WireMockServer.perTest(customerServiceServer)` to
achieve same result.
