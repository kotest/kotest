---
id: ktor
title: Ktor Matchers
slug: ktor-matchers.html
sidebar_label: Ktor
---

Code is kept on a [separate repository](https://github.com/kotest/kotest-assertions-ktor) and on a different group: `io.kotest.extensions`.

**Full Dependency**

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-assertions-ktor)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-assertions-ktor/)
> 
> ```implementation("io.kotest.extensions:kotest-assertions-ktor:version")```
> 
> ```implementation "io.kotest.extensions:kotest-assertions-ktor:version"```



Matchers for [Ktor](https://ktor.io/) are provided by the `kotest-assertions-ktor` module.


### Test Application Response

The following matchers are used when testing via the ktor server testkit.

| Matcher | Description    |
| ---------- | --- |
| `TestApplicationResponse.shouldHaveStatus(HttpStatusCode)`        | Asserts that the response had the given http status code    |
| `TestApplicationResponse.shouldHaveContent(content)`              | Asserts that the response has the given body     |
| `TestApplicationResponse.shouldHaveContentType(ContentType)`      | Asserts that the response has the given Content Type     |
| `TestApplicationResponse.shouldHaveHeader(name, value)`           | Asserts that the response included the given name=value header     |
| `TestApplicationResponse.shouldHaveCookie(name, value)`           | Asserts that the response included the given cookie     |
| `TestApplicationResponse.shouldHaveCacheControl(value)`           | Asserts that the response included the given cache control header     |
| `TestApplicationResponse.shouldHaveETag(value)`                   | Asserts that the response included the given etag header     |
| `TestApplicationResponse.shouldHaveContentEncoding(value)`        | Asserts that the response included the given content encoding header     |

### HttpResponse

The following matchers can be used against responses from the ktor http client.

| Matcher | Description    |
| ---------- | --- |
| `HttpResponse.shouldHaveStatus(HttpStatusCode)`        | Asserts that the response had the given http status code    |
| `HttpResponse.shouldHaveContentType(ContentType)`      | Asserts that the response has the given Content Type     |
| `HttpResponse.shouldHaveHeader(name, value)`           | Asserts that the response included the given name=value header     |
| `HttpResponse.shouldHaveVersion(HttpProtocolVersion)`  | Asserts that the response used the given protocol version     |
| `HttpResponse.shouldHaveCacheControl(value)`           | Asserts that the response included the given cache control header     |
| `HttpResponse.shouldHaveETag(value)`                   | Asserts that the response included the given etag header     |
| `HttpResponse.shouldHaveContentEncoding(value)`        | Asserts that the response included the given content encoding header     |
