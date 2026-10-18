# Known Issues: Kotest

Common problems, limitations and workarounds. Config details live in
[FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md); setup in [SETUP-REFERENCE.md](SETUP-REFERENCE.md).

---

## 1. Tests Not Discovered (0 tests executed)

| Cause                                   | Fix                                                                   |
|-----------------------------------------|-----------------------------------------------------------------------|
| No `useJUnitPlatform()` (JVM)           | `tasks.withType<Test>().configureEach { useJUnitPlatform() }`         |
| No `kotest-runner-junit5` (JVM / KMP `jvmTest`) | add it to `testImplementation` / `jvmTest`                     |
| JS / Wasm / Native without KSP or `io.kotest` plugin | apply both, KSP first                                    |
| Class doesn't extend a spec style, or is abstract | extend `FunSpec`, `DescribeSpec`, …                         |
| Tests declared outside the constructor lambda / `init {}` | move them inside                                    |
| Gradle up-to-date cache                 | see [§9](#9-gradle-caching--0-tests-executed)                        |

---

## 2. ProjectConfig Not Picked Up

| Platform | Where Kotest looks                                                                                     |
|----------|--------------------------------------------------------------------------------------------------------|
| JVM      | `io.kotest.provided.ProjectConfig`, then the `kotest.framework.config.fqn` system property, then (6.2+) a class named `ProjectConfig` in a package common to all specs |
| Non-JVM  | anywhere (found by KSP); must be a `class` on Native (`object` is fine on JVM/JS)                       |

`kotest.framework.config.fqn` must be set on the test task — `-D` on the Gradle command line is not forwarded:

```kotlin
tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.config.fqn", "com.example.MyProjectConfig")
}
```

---

## 3. `@AutoScan` Removed (6.0)

Classpath scanning for extensions was removed on every platform. Register explicitly — in
`AbstractProjectConfig.extensions`, per spec with `extension(...)` / `extensions(...)`, or with
`@ApplyExtension(MyExtension::class)` (JVM). See [FRAMEWORK-REFERENCE.md#extensions](FRAMEWORK-REFERENCE.md#extensions).

---

## 4. `assertSoftly` Not Collecting Failures

Only Kotest assertions are soft-aware. Others (MockK `verify`, AssertJ, plain `AssertionError`, Kotest's own `fail()`)
throw immediately. Some Kotest assertions are also never soft: `shouldThrow*`, `shouldNotThrowExactly*`,
`shouldNotThrowMessage`, `shouldCompleteWithin`, `shouldCompleteBetween`.

```kotlin
import io.kotest.assertions.AssertionErrorBuilder.Companion.failSoftly
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.matchers.shouldBe

assertSoftly {
   result shouldBe expected
   shouldNotThrowAnyUnit { verify(exactly = 1) { mock.method(any()) } } // wrap foreign assertions
   failSoftly("collected, not thrown")                                 // instead of fail(...)
}
```

---

## 5. Deprecated Isolation Modes

`InstancePerTest` and `InstancePerLeaf` still exist but are documented as deprecated (undefined behaviour in edge
cases). Use `IsolationMode.InstancePerRoot` (or the default `SingleInstance`). See
[FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md).

---

## 6. Timeout Ignored by Blocking Code (JVM)

A coroutine timeout cannot interrupt `Thread.sleep` / blocking I/O. Enable `blockingTest` so the test runs on its own
thread and can be interrupted:

```kotlin
import kotlin.time.Duration.Companion.seconds

test("blocking operation").config(blockingTest = true, timeout = 5.seconds) {
   Thread.sleep(1000)
}
```

---

## 7. Data Tests on JS / Wasm / Native

Non-JVM platforms do not support a container test with no leaf inside it. The **innermost** data-test scope must
create leaf tests — e.g. in FunSpec end with `withTests`, not `withData` / `withContexts`:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests

class MySpec : FunSpec({
   withContexts("GET", "POST") { method ->
      withTests(200, 404) { status -> /* leaf tests: OK everywhere */ }
   }
})
```

Per-style `withXXX` variants: [FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md).

---

## 8. Matcher Import Confusion

Overloads live in type-specific packages; a wrong import is a compile error.

| Matcher                      | Import                                         |
|------------------------------|------------------------------------------------|
| `shouldBe`                   | `io.kotest.matchers.shouldBe`                  |
| `shouldContain` (String)     | `io.kotest.matchers.string.shouldContain`      |
| `shouldContain` (Collection) | `io.kotest.matchers.collections.shouldContain` |
| `shouldContain` (Map)        | `io.kotest.matchers.maps.shouldContain`        |
| `shouldBeNull`               | `io.kotest.matchers.nulls.shouldBeNull`        |
| `shouldThrow`                | `io.kotest.assertions.throwables.shouldThrow`  |
| `retry`                      | `io.kotest.assertions.retry`                   |
| `eventually` / `continually` | `io.kotest.assertions.nondeterministic.*`      |

---

## 9. Gradle Caching — "0 tests executed"

Gradle skips an up-to-date test task. Options (ask the user before changing the build):

- one-off: `./gradlew test --rerun`
- Kotest Gradle plugin: `kotest { alwaysRerunTests = true }`
- plain Gradle: `tasks.withType<Test>().configureEach { outputs.upToDateWhen { false } }` (not for CI)

---

## 10. IntelliJ Gutter Icons Missing

Install the Kotest IntelliJ plugin (all targets). Apply the `io.kotest` Gradle plugin for jump-to-source / rerun from
the results tree. Invalidate caches if icons still don't appear.

---

## 11. 5.x → 6.x and Common Agent Mistakes

| Wrong / old                                                     | Correct (6.x)                                                                 |
|-----------------------------------------------------------------|-------------------------------------------------------------------------------|
| Kotlin < 2.2                                                    | Kotlin 2.2+, JDK 11+ (otherwise stay on 5.x)                                   |
| Mixed Kotest versions (5.x assertions + 6.x runner)             | one version for every `io.kotest` artifact                                    |
| `specConcurrencyMode` / `testConcurrencyMode` (don't exist)     | `specExecutionMode = SpecExecutionMode.Concurrent`, `testExecutionMode = TestExecutionMode.…` (`io.kotest.engine.concurrency`) |
| `override fun extensions()` / `listeners()` in ProjectConfig    | `override val extensions = listOf(...)`                                       |
| `./gradlew test -Dkotest.tags=…` (not forwarded to the test JVM)| `KOTEST_TAGS="A & !B" ./gradlew test` (6.1+) or `systemProperty("kotest.tags", …)` in the test task |
| `kotest-framework-datatest` dependency                          | remove — data tests are in `kotest-framework-engine` (`io.kotest.datatest`)  |
| `kotest-assertions-api` dependency                              | no longer published; contents moved to `kotest-assertions-shared` (transitive) |
| 4.x table testing (`forAll(row(...))`, `table(headers(...))`)   | add `kotest-assertions-table`, import `io.kotest.data.*`                      |
| Kotest compiler plugin for KMP                                  | KSP + `io.kotest` Gradle plugin                                               |
| Testcontainers `TestContainerExtension`, `ContainerExtension`, `SharedTestContainerExtension`, `DockerComposeContainerExtension`, kafka helpers (all deprecated, removed in 6.3) | `install(TestContainerSpecExtension(container))`; share with `TestContainerProjectExtension`; JDBC: `JdbcDatabaseContainerSpecExtension` / `…ProjectExtension` (returns a `DataSource`); Compose: `ComposeContainerSpecExtension` / `…ProjectExtension`. Details: [FRAMEWORK-REFERENCE.md#extensions](FRAMEWORK-REFERENCE.md#extensions) |
| `JunitXmlReporter` (deprecated 6.2, removed in 7.0)             | Gradle's built-in JUnit XML in `build/test-results/<task>/`                   |
| Expecting `eventually` to use virtual time under `coroutineTestScope` / `runTest` | it always uses wall-clock time                              |
| JUnit 5 property test `@Test fun t() = runBlocking { checkAll… }` | returns a value, so JUnit silently skips it — use `fun t() { runBlocking { … } }` |
| `retry(3, 1.seconds)` meaning "3 tries, 1s apart"               | 2nd parameter is the overall `timeout`; use `retry(3, timeout = 10.seconds, delay = 1.seconds)` |
