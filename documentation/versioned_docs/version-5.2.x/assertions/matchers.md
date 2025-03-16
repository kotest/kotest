---
title: Matchers
slug: matchers.html
---


A `Matcher` is the Kotest term for an assertion that performs a specific test. For example, a matcher may test that a double is greater than zero.
Or it it may test that a file is not empty.

Kotest currently has approximately 325 matchers split across several modules. Most of these matchers are for standard library types.
Others are project specific. Additionally, there are matchers provided by third party libraries.

Kotest matchers are _framework agnostic_. You can use them with the Kotest framework, or with any other framework. If you are happy with JUnit,
you can still use the powerful matchers provided by the kotest assertion modules.

Matchers can be used in two styles:

 * Extension functions like `a.shouldBe(b)` or `a.shouldStartWith("foo")`
 * Infix functions like `a shouldBe b` or `a should startWith("foo")`

Both styles are supported. The advantage of the extension function style is that the IDE can autocomplete for you,
 but some people may prefer the infix style as it is slightly cleaner.

Matchers can be negated by using `shouldNot` instead of `should` for the infix style. For example, `a shouldNot startWith("boo")`.
For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.




### Kotest Matcher Modules

These modules provide the core matcher experience. They are hosted in the main Kotest repo, and are released on the same cadence as the
Kotest framework.

| Module | Description | Type |
| -------- | ---- | ---- |
| [kotest-assertions-core](core.md) | Provides matchers for standard libary types. | Multiplatform |
| [kotest-assertions-json](json.md) | Provides matchers for testing json objects. | JVM |
| [kotest-assertions-kotlinx-time](kotlinx-datetime.md) | Provides matchers for Kotlin's date / time library. | Multiplatform |
| [kotest-assertions-sql](sql.md) | Provides matchers for JDBC. | JVM |
| [kotest-assertions-ktor](ktor.md) | Provides matchers for Ktor server test and client libraries. | Multiplatform |





### Kotest External Matcher Modules

These modules are hosted in the kotest organization but in separate repositories from the main kotest project. They are released on an independent
cadence from the Kotest framework. They provide matchers for third party libraries.


| Module | Description | Type |
| -------- | ---- | ---- |
| [kotest-assertions-arrow](arrow.md) | Provides matchers for the Arrow functional programming library. | JVM |
| [kotest-assertions-compiler](compiler.md) | Provides matchers that test for compilable code. | JVM |
| [kotest-assertions-klock](klock.md) | Providers matchers for Klock. | Multiplatform |
| [kotest-assertions-konform](konform.md) | Provides matchers for Konform. | Multiplatform |
| [kotest-assertions-jsoup](jsoup.md) | Provides matchers JSoup. | JVM |




### Community Provided Matchers

This is a list of projects that provide Kotest matchers. They are maintained outside of the Kotest organization.

| Library | Description |
| -------- | ---- |
| [Http4k](https://github.com/http4k/http4k/tree/master/http4k-testing/kotest) | Functional toolkit for Kotlin HTTP applications |
| [Micronaut](https://github.com/micronaut-projects/micronaut-test) | JVM-based, full-stack framework for building modular, easily testable microservice |
