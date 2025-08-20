---
id: wiremock
title: WireMock
sidebar_label: WireMock
slug: wiremock.html
---

## WireMock

[WireMock](https://github.com/tomakehurst/wiremock) is a library which provides HTTP response stubbing, matchable on
URL, header and body content patterns etc.

Kotest provides a module ```kotest-extensions-wiremock``` for integration with wiremock.


[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-wiremock.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest/kotest-extensions-wiremock)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-wiremock%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-wiremock/maven-metadata.xml)


To begin, add the following dependency to your build:

```
io.kotest:kotest-extensions-wiremock:${kotestVersion}
```

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::

Having this dependency in the classpath brings `WireMockListener` into scope.
`WireMockListener` manages  the lifecycle of a `WireMockServer` during your test.

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

In above example we created an instance of `WireMockListener` which starts a `WireMockServer` before running the tests
in the spec and stops it after completing all the tests in the spec.

You can use `WireMockServer.perSpec(customerServiceServer)` to achieve same result.

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


In above example we created an instance of `WireMockListener` which starts a `WireMockServer` before running every test
in the spec and stops it after completing every test in the spec.
You can use `WireMockServer.perTest(customerServiceServer)` to achieve same result.
