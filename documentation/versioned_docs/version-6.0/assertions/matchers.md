For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.




### Kotest Matcher Modules

These modules provide the core matcher experience. They are hosted in the main Kotest repo, and are released on the same cadence as the
Kotest framework.

| Module                                                | Description                                                  | Type          |
|-------------------------------------------------------|--------------------------------------------------------------|---------------|
| [kotest-assertions-core](core.md)                     | Provides matchers for standard libary types.                 | Multiplatform |
| [kotest-assertions-json](json/overview.md)            | Provides matchers for testing json objects.                  | JVM           |
| [kotest-assertions-kotlinx-time](kotlinx-datetime.md) | Provides matchers for Kotlin's date / time library.          | Multiplatform |
| [kotest-assertions-sql](sql.md)                       | Provides matchers for JDBC.                                  | JVM           |
| [kotest-assertions-ktor](ktor.md)                     | Provides matchers for Ktor server test and client libraries. | Multiplatform |





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
| [Android](https://github.com/LeoColman/kotest-android) | Toolbox for working with Kotest and Android |
| [Http4k](https://github.com/http4k/http4k/tree/master/http4k-testing/kotest) | Functional toolkit for Kotlin HTTP applications |
| [Micronaut](https://github.com/micronaut-projects/micronaut-test) | JVM-based, full-stack framework for building modular, easily testable microservice |
