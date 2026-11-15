# Kotest Framework Reference (6.2)

Configuration, data-driven testing, coroutines, filtering, and extensions. Every snippet uses
Kotest 6.x APIs. Many 5.x names were renamed or removed; see [KNOWN-ISSUES.md](KNOWN-ISSUES.md).

## Configuration Resolution Order

Most specific wins:

1. Test `.config(...)`
2. Spec properties (`timeout = ...`, `isolationMode = ...` inside the spec `init`)
3. Spec `defaultTestConfig`
4. `PackageConfig` (nearest package first, then parent packages) — JVM only
5. `ProjectConfig`
6. System properties / environment variables

## Test Config

```kotlin
test("name").config(
   enabled = true,
   enabledIf = { System.getenv("CI") != null },   // (TestCase) -> Boolean
   enabledOrReasonIf = { Enabled.disabled("not on CI") },
   timeout = 5.seconds,               // whole test, including all invocations
   invocationTimeout = 1.seconds,     // per invocation
   invocations = 3,
   retries = 2, retryDelay = 100.milliseconds,
   tags = setOf(Slow),
   severity = TestCaseSeverityLevel.CRITICAL,
   blockingTest = true,               // JVM: run on a dedicated thread so timeouts can interrupt blocking code
   coroutineTestScope = true,         // run inside kotlinx-coroutines-test with virtual time
   coroutineDebugProbes = true,       // JVM: dump coroutines on failure
   extensions = listOf(MyTestCaseExtension()),
) { /* ... */ }
```

Containers (`context`, `describe`, `given`, …) take a smaller set, including `failfast = true`.

### Spec-level defaults

```kotlin
class MySpec : FunSpec() {
   init {
      isolationMode = IsolationMode.InstancePerRoot
      failfast = true
      coroutineTestScope = true
      defaultTestConfig = DefaultTestConfig(timeout = 2.seconds, retries = 3)  // io.kotest.core.test.config
      test("a") { }
   }
}
```

`DefaultTestConfig` accepts most `.config` parameters (timeout, invocationTimeout, invocations, assertSoftly,
tags, severity, enabledIf, assertionMode, blockingTest, coroutineTestScope, failfast, retries, retryDelay).

## Project Config

```kotlin
package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import kotlin.time.Duration.Companion.seconds

class ProjectConfig : AbstractProjectConfig() {
   override val timeout = 10.seconds
   override val isolationMode = IsolationMode.InstancePerRoot
   override val assertionMode = AssertionMode.Error       // fail tests with no assertions
   override val globalAssertSoftly = true
   override val specExecutionMode = SpecExecutionMode.LimitedConcurrency(4)  // Sequential | Concurrent | LimitedConcurrency(n)
   override val testExecutionMode = TestExecutionMode.Sequential
   override val coroutineTestScope = true
   override val failfast = true                // per spec
   override val projectWideFailFast = true     // stop the whole run on first failure
   override val retries = 1
   override val extensions = listOf(MyProjectExtension())

   override suspend fun beforeProject() { }
   override suspend fun afterProject() { }
}
```

Discovery:

- **JVM** — `io.kotest.provided.ProjectConfig`, or a class named `ProjectConfig` in the common package of all
  specs (6.2), or the FQN in the `kotest.framework.config.fqn` system property **set on the test task**:
  `tasks.withType<Test>().configureEach { systemProperty("kotest.framework.config.fqn", "com.x.MyConfig") }`.
- **JS / Wasm / Native** — KSP finds any `AbstractProjectConfig` subclass anywhere; only one is allowed.
- Prefer `class` over `object`: Native requires a class.
- Spec/test concurrency is JVM only; other platforms always run sequentially.

### Package Config (JVM)

```kotlin
package com.example.payments

class PackageConfig : AbstractPackageConfig() {   // name must be exactly PackageConfig
   override val timeout = 5.seconds
   override val failfast = true
}
```

Applies to specs in that package and in sub-packages.

## Isolation Modes

| Mode                       | Behaviour                                                       |
|----------------------------|-----------------------------------------------------------------|
| `SingleInstance` (default) | One spec instance; all tests share fields                       |
| `InstancePerRoot`          | Fresh instance per root test (recommended when state leaks)     |
| `InstancePerTest`          | Deprecated in docs; prefer `InstancePerRoot`                    |
| `InstancePerLeaf`          | Deprecated in docs; prefer `InstancePerRoot`                    |

## Data-Driven Testing

These functions are built into `kotest-framework-engine` (import `io.kotest.datatest.*`). The 5.x
`kotest-framework-datatest` artifact no longer exists.

Each style has **leaf** variants (rows become tests) and **container** variants (rows become groups you nest
tests inside):

| Style        | Leaf                    | Container                                     |
|--------------|-------------------------|-----------------------------------------------|
| FunSpec      | `withTests`             | `withContexts`                                |
| FreeSpec     | `withTests`             | `withContexts`                                |
| DescribeSpec | `withIts`               | `withDescribes`, `withContexts`               |
| ShouldSpec   | `withShoulds`           | `withContexts`                                |
| ExpectSpec   | `withExpects`           | `withContexts`                                |
| FeatureSpec  | `withScenarios`         | `withFeatures`                                |
| BehaviorSpec | `withThens`             | `withGivens`, `withWhens`, `withAnds`, `withContexts` |
| WordSpec     | —                       | `withWhens`, `withShoulds`                    |
| StringSpec   | `withData` (leaf)       | —                                             |

`withData` still works everywhere. It maps to the container variant (except in StringSpec).

```kotlin
import io.kotest.datatest.withTests
import io.kotest.datatest.withContexts

data class Triple(val a: Int, val b: Int, val c: Int)

class PythagTest : FunSpec({
   withTests(Triple(3, 4, 5), Triple(5, 12, 13)) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   // custom names
   withTests(nameFn = { "${it.a}² + ${it.b}²" }, Triple(3, 4, 5), Triple(6, 8, 10)) { (a, b, c) ->
      a * a + b * b shouldBe c * c
   }

   // map keys become test names
   withTests(mapOf("zero" to 0, "one" to 1)) { n -> (n >= 0) shouldBe true }

   // nesting: containers outside, leaf innermost
   withContexts("dev", "prod") { env ->
      withTests(1, 2) { n -> check(env, n) }
   }
})
```

Rules:

- The vararg form needs at least 2 rows. For a single row, pass a `List`.
- Default names come from `toString()` of "stable" types (data classes, primitives, enums). Otherwise implement
  `WithDataTestName`, annotate with `@IsStableType`, or pass `nameFn`.
- **JS / Native**: the innermost data scope must be a leaf (`withTests`/`withIts`/…), not a container.
- Run only data tests with `KOTEST_TAGS=kotest.data`.

## Coroutines

Every test body is a `suspend` function. You never need `runBlocking`.

### Virtual time

```kotlin
import io.kotest.core.test.testCoroutineScheduler
import kotlin.time.Duration.Companion.days

test("long delay without waiting").config(coroutineTestScope = true) {
   val job = launch { delay(1.days) }
   testCoroutineScheduler.advanceTimeBy(1.days)
   testCoroutineScheduler.runCurrent()
   job.isCompleted shouldBe true
}
```

- Kotest ≤ 6.2.6 does not expose `kotlinx-coroutines-test` transitively; there, calling scheduler methods
  (`advanceTimeBy`, `runCurrent`, …) requires `testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:<version>")`.
- Inside `coroutineTestScope = true` the test body runs under `runTest`, so the receiver is a `TestScope`.
  Since 6.2.6 the ambient coroutine context (for example Spring's) is propagated into it.
- `eventually`/`continually`/`until` always use **wall-clock** time, even with `coroutineTestScope`.
- Use `blockingTest = true` when the code blocks a thread (`Thread.sleep`, JDBC) and you need `timeout` to fire.

## Fail Fast

```kotlin
context("checkout flow").config(failfast = true) {
   test("add to cart") { }
   test("pay") { }       // skipped if "add to cart" fails
}
```

You can also set `failfast = true` at spec level, `override val failfast = true` in ProjectConfig, or
`projectWideFailFast = true` to abort the whole run.

## Tags and Filtering

```kotlin
object Slow : Tag()
object Database : Tag()

test("big import").config(tags = setOf(Slow, Database)) { }

@Tags("Integration")              // spec-level tag
class RepoTest : FunSpec({ })
```

Run with:

```bash
KOTEST_TAGS="Database & !Slow" ./gradlew test              # env var, every platform (6.1+)
KOTEST_INCLUDE_PATTERN='com.example.*' ./gradlew test        # filter specs/tests by pattern; ';' separates patterns
./gradlew test --tests "com.example.RepoTest"                # Gradle filter (JVM and KMP)
./gradlew test --tests "com.example.RepoTest.context -- leaf name"   # nested test
```

`./gradlew test -Dkotest.tags=...` does **not** reach the test JVM, because Gradle doesn't forward `-D` to
test workers. Either use the env var or set the system property on the task:

```kotlin
tasks.withType<Test>().configureEach {
   systemProperty("kotest.tags", System.getProperty("kotest.tags") ?: "")
}
```

Focus and disable while iterating: prefix a test with `f:` (`test("f:only this")`) to run only that root test,
or with `x` (`xtest`, `xdescribe`, `xcontext`) to disable it. `KOTEST_TEST_ENABLED_OVERRIDE=true` forces
disabled tests to run.

## Lifecycle Hooks

```kotlin
class DbTest : FunSpec({
   beforeSpec { }          // once, before the first test (only if a test runs)
   afterSpec { }
   beforeEach { }          // before each leaf test
   afterEach { (testCase, result) -> }
   beforeContainer { }     // before each container
   beforeTest { }          // before every test, leaf or container
   beforeInvocation { _, i -> }
   finalizeSpec { kclass, results -> }   // after all instances of the spec finish

   val conn = autoClose(Database.connect())     // closed after the spec
   val dir = tempdir()                          // JVM; deleted after the spec (keepOnFailure = true to keep)
   val file = tempfile(suffix = ".json")        // JVM
})
```

## Extensions

Listeners observe lifecycle events; extensions can change execution. Both live in
`io.kotest.core.listeners` / `io.kotest.core.extensions`.

```kotlin
class DbListener : BeforeSpecListener, AfterSpecListener {
   override suspend fun beforeSpec(spec: Spec) = Db.start()
   override suspend fun afterSpec(spec: Spec) = Db.stop()
}

class TimingExtension : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      val start = TimeSource.Monotonic.markNow()
      return execute(testCase).also { println("${testCase.name.name}: ${start.elapsedNow()}") }
   }
}

class MySpec : FunSpec({
   extension(DbListener())          // per spec; returns the extension
   extension(TimingExtension())
})
// or globally: ProjectConfig.extensions = listOf(...)
```

Other interfaces: `BeforeTestListener`, `AfterTestListener`, `BeforeProjectListener`, `AfterProjectListener`,
`SpecExtension`, `ProjectExtension`, `TagExtension`, `EnabledExtension`, and `MountableExtension` (used with `install`).
`@AutoScan` was removed in 6.0, so register every extension explicitly.

### Testcontainers (`io.kotest.extensions:kotest-extensions-testcontainers`, JVM)

```kotlin
val redis = install(TestContainerSpecExtension(GenericContainer("redis:7-alpine"))) {
   withExposedPorts(6379)
}
val ds: DataSource = install(JdbcDatabaseContainerSpecExtension(PostgreSQLContainer("postgres:16"))) {
   maximumPoolSize = 4
}
val compose = install(ComposeContainerSpecExtension.fromResource("docker-compose.yml"))
```

- Use the `*ProjectExtension` variants (`TestContainerProjectExtension`, `JdbcDatabaseContainerProjectExtension`,
  `ComposeContainerProjectExtension`) to share one container across specs.
- `TestContainerExtension`, `ContainerExtension`, `JdbcTestContainerExtension`, `SharedTestContainerExtension`, and
  `DockerComposeContainersExtension` are deprecated and will be removed in 6.3.

### Koin (`kotest-extensions-koin`, package `io.kotest.koin`)

```kotlin
class ServiceTest : FunSpec(), KoinTest {
   init {
      extension(KoinExtension { listOf(appModule()) })   // module factory: fresh modules per test (6.2.4)
      // KoinExtension(module = appModule, mode = KoinLifecycleMode.Root) to share across a root's leaves
      test("x") { get<Service>().ping() shouldBe "pong" }
   }
}
```

### Spring (`kotest-extensions-spring`)

```kotlin
@SpringBootTest
@ApplyExtension(SpringExtension::class)   // required for constructor injection; extension() in init is too late
class ControllerTest(private val service: MyService) : FunSpec({
   test("wired") { service.shouldNotBeNull() }
})
```

### System, clock, and locale (`io.kotest:kotest-extensions`, JVM)

```kotlin
withEnvironment("API_URL", "http://localhost") { }    // io.kotest.extensions.system
withSystemProperty("feature.x", "on") { }
withDefaultLocale(Locale.FRANCE) { }                    // io.kotest.extensions.locale
val clock = TestClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)   // io.kotest.extensions.clock
clock.plus(5.minutes)
```

`withEnvironment` mutates the process environment and is not safe with concurrent tests.

## Gradle Plugin (`id("io.kotest")`)

Required for JS, Wasm, and Native (together with KSP). Optional on the JVM, where it adds IntelliJ jump-to-source and
rerun-from-tree support.

```kotlin
kotest {
   kotestVersion          // read-only (6.2.6): the plugin's Kotest version, to keep artifacts aligned
   customGradleTask = false       // true = run JVM tests via `jvmKotest`/`kotest` task instead of Test task
   alwaysRerunTests = true        // @ExperimentalKotest: disable Gradle up-to-date caching for tests
   enablePowerAssert = true       // @ExperimentalKotest (6.2): wire Power Assert for shouldBe
   truncateTestNamesInGradle = true  // Gradle 9.3+ long test path fix
   showIgnoreReasons = true
}

dependencies {
   testImplementation("io.kotest:kotest-assertions-json:${kotest.kotestVersion.get()}")
}
```

## Custom Spec Styles (6.2)

To build your own DSL, extend `io.kotest.core.spec.AbstractSpec`, annotate the DSL functions with
`@TestRunnable`, and register tests with
`add(TestDefinitionBuilder.builder(TestNameBuilder.builder(name).build(), TestType.Test).build(body))`.
Nested scopes extend `AbstractTestScope`. The IntelliJ plugin picks up custom styles automatically. For the full
walkthrough, see https://kotest.io/docs/framework/custom-styles.html.
