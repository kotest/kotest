---
id: ktor
title: Ktor
sidebar_label: Ktor
slug: ktor.html
---


The ```kotest-assertions-ktor``` module provides response matchers for a [Ktor](https://ktor.io) application. There are matchers
for both `TestApplicationResponse` if you are using the server side test support, and for `HttpResponse` if you are using the ktor
HTTP client.

To add Ktor matchers, add the following dependency to your project

```groovy
io.kotest.extensions:kotest-assertions-ktor:${version}
```

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-assertions-ktor)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-assertions-ktor/)


An example of using the matchers with the server side test support:
```kotlin
withTestApplication({ module(testing = true) }) {
   handleRequest(HttpMethod.Get, "/").apply {
      response shouldHaveStatus HttpStatusCode.OK
      response shouldNotHaveContent "failure"
      response.shouldHaveHeader(name = "Authorization", value = "Bearer")
      response.shouldNotHaveCookie(name = "Set-Cookie", cookieValue = "id=1234")
   }
}
```

And an example of using the client support:
```kotlin
val client = HttpClient(CIO)
val response = client.post("http://mydomain.com/foo")
response.shouldHaveStatus(HttpStatusCode.OK)
response.shouldHaveHeader(name = "Authorization", value = "Bearer")

```
