---
name: kotest
description: >
  Helps write, migrate, and improve Kotlin tests using the Kotest testing framework.
  Covers test spec styles (FunSpec, DescribeSpec, BehaviorSpec, etc.), assertions
  (shouldBe, matchers, soft assertions), property-based testing (Arb, forAll, checkAll),
  data-driven testing, lifecycle hooks, extensions, coroutine testing, project
  configuration, and multiplatform setup. Use when the user mentions Kotest, kotest
  matchers, property testing with Arb, Kotest spec styles, shouldBe assertions,
  Kotlin test framework, or migrating from JUnit/TestNG/Spek to Kotest.
license: Apache-2.0
metadata:
  author: Kotest Team
  version: "1.0.0"
---

# Kotest — Kotlin Testing Framework

Kotest is a flexible and comprehensive testing framework for Kotlin with multiplatform support.
It is split into three standalone sub-projects that can be used independently or together:

1. **Test Framework** — spec-based test definitions with 9 styles
2. **Assertions Library** — rich matchers and assertion utilities
3. **Property Testing** — generator-based property test support

## Step 0: Analyze the Project

Before writing tests or migrating an existing test suite, understand the project:

1. Read `build.gradle.kts` (or `.gradle`) to identify the build system and existing test dependencies
2. Check if the project uses a Gradle version catalog (`gradle/libs.versions.toml`)
3. Determine the **Kotlin version** — Kotest 6.x requires Kotlin 2.2+
4. Determine the **target platforms** — JVM, JS, WasmJS, Native, Android, or Multiplatform
5. Check for existing test frameworks (JUnit 4/5, TestNG, Spek) that may need migration
6. Look for the Kotest IntelliJ plugin configuration (recommended for all JVM Kotest users)
7. Check for an existing `ProjectConfig` class (used for global test settings)

If Bash is available, run `scripts/analyze-project.sh` from this skill's directory to get a
structured summary.

### Classify the Task

| User Intent                                                 | Path                                |
|-------------------------------------------------------------|-------------------------------------|
| Start from scratch — new Kotest project                     | **Path A** — Setup & First Tests    |
| Migrate from JUnit/TestNG/Spek                              | **Path B** — Migration              |
| Add assertions to existing tests (any framework)            | **Path C** — Assertions Only        |
| Add property-based testing                                  | **Path D** — Property Testing       |
| Configure advanced features (concurrency, extensions, tags) | **Path E** — Advanced Configuration |
| Writing tests for Kotlin Multiplatform                      | **Path F** — KMP Testing            |
| Debugging Kotest Issues                                     | **Path G** - Kotest Troubleshooting |

## Path A: Setup & First Tests

### Step 1: Add Dependencies

See [references/SETUP-REFERENCE.md](references/SETUP-REFERENCE.md) for full platform-specific setup.

#### JVM (Gradle Kotlin DSL)

```kotlin
// build.gradle.kts
plugins {
  id("io.kotest") version "<kotest-version>"  // optional but recommended
}

dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:<kotest-version>")
  testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
  testImplementation("io.kotest:kotest-property:<kotest-version>")  // optional
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
```

#### Multiplatform

```kotlin
// build.gradle.kts
plugins {
  id("com.google.devtools.ksp") version "<ksp-version>"
  id("io.kotest") version "<kotest-version>"
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation("io.kotest:kotest-framework-engine:<kotest-version>")
        implementation("io.kotest:kotest-assertions-core:<kotest-version>")
      }
    }
  }
}
```

### Step 2: Choose a Spec Style

Kotest offers 9 spec styles. All are functionally equivalent — pick one that fits the team:

| Style          | Best For                              | Syntax                            |
|----------------|---------------------------------------|-----------------------------------|
| `FunSpec`      | General purpose (recommended default) | `test("name") { }`                |
| `DescribeSpec` | JS/Ruby developers                    | `describe / it`                   |
| `BehaviorSpec` | BDD / Gherkin                         | `given / when / then`             |
| `StringSpec`   | Minimal boilerplate                   | `"name" { }`                      |
| `FreeSpec`     | Arbitrary nesting depth               | `"name" - { } / "name" { }`       |
| `WordSpec`     | ScalaTest users                       | `"subject" should { "verb" { } }` |
| `FeatureSpec`  | Cucumber-style                        | `feature / scenario`              |
| `ExpectSpec`   | Kotest original                       | `expect("name") { }`              |
| `ShouldSpec`   | Kotest original                       | `should("name") { }`              |

See [references/SPEC-STYLES.md](references/SPEC-STYLES.md) for complete examples of each style.

### Step 3: Write Your First Test

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class MyFirstTest : FunSpec({
  test("string length") {
    "hello".shouldHaveLength(5)
  }

  context("arithmetic") {
    test("addition") {
      1 + 1 shouldBe 2
    }
    test("subtraction") {
      10 - 3 shouldBe 7
    }
  }
})
```

### Step 4: Run Tests

- **Gradle**: `./gradlew test`
- **IntelliJ**: Click the gutter icon next to the test class or individual test
- **Specific test**: `./gradlew test --tests "com.example.MyFirstTest"`

## Path B: Migration from JUnit / TestNG / Spek

See [references/MIGRATION-GUIDE.md](references/MIGRATION-GUIDE.md) for complete migration mappings.

### JUnit 5 → Kotest Quick Mapping

| JUnit 5               | Kotest (FunSpec)                                      |
|-----------------------|-------------------------------------------------------|
| `@Test fun myTest()`  | `test("my test") { }`                                 |
| `@Nested inner class` | `context("group") { }`                                |
| `@BeforeEach`         | `beforeEach { }`                                      |
| `@AfterEach`          | `afterEach { }`                                       |
| `@BeforeAll`          | `beforeSpec { }`                                      |
| `@AfterAll`           | `afterSpec { }`                                       |
| `@Disabled`           | `xtest("disabled") { }` or `.config(enabled = false)` |
| `@Tag("slow")`        | `.config(tags = setOf(Slow))`                         |
| `assertEquals(a, b)`  | `a shouldBe b`                                        |
| `assertTrue(x)`       | `x.shouldBeTrue()`                                    |
| `assertThrows<E> { }` | `shouldThrow<E> { }`                                  |
| `@ParameterizedTest`  | `withTests(...)` (data-driven testing)                |
| `@RepeatedTest(n)`    | `.config(invocations = n)`                            |

### Migration Steps

1. **Keep JUnit on the classpath** — Kotest JVM runner uses JUnit Platform under the hood
2. **Migrate one test class at a time** — JUnit and Kotest tests can coexist
3. **Replace the class** — change `class MyTest` to `class MyTest : FunSpec({})` (or your chosen style)
4. **Move `@Test` methods** into `test("name") { }` blocks inside the `init` lambda
5. **Replace assertions** — `assertEquals` → `shouldBe`, `assertTrue` → `shouldBeTrue()`, etc.
6. **Replace lifecycle** — `@BeforeEach` → `beforeEach { }`, etc.
7. **Replace parameterized tests** — `@ParameterizedTest` → `withTests(...)` data-driven testing
8. **Remove JUnit imports** — all Kotest imports are under `io.kotest.*`

## Path C: Assertions Only

Kotest assertions can be used with **any** test framework (JUnit, TestNG, etc.).

### Setup (standalone)

```kotlin
testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
```

### Core Assertions

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.maps.*

// Equality
result shouldBe expected
result shouldNotBe unexpected

// Strings
name.shouldStartWith("J")
name.shouldContain("oh")
name.shouldHaveLength(4)

// Collections
list.shouldContain("a")
list.shouldContainExactly("a", "b", "c")
list.shouldHaveSize(3)
list.shouldBeSorted()

// Maps
map.shouldContainKey("name")
map.shouldContainValue("John")
map.shouldContain("name", "John")

// Types
obj.shouldBeInstanceOf<String>()
obj.shouldBeNull()
nullable.shouldNotBeNull()

// Exceptions
shouldThrow<IllegalArgumentException> {
  riskyOperation()
}
```

### Soft Assertions

Collect all assertion failures instead of stopping at the first:

```kotlin
assertSoftly {
  name shouldBe "John"
  age shouldBe 30
  email.shouldContain("@")
}
// Reports ALL failures, not just the first
```

### Inspectors

Test elements in collections:

```kotlin
val people = listOf(Person("Alice", 30), Person("Bob", 25), Person("Charlie", 35))

people.forAll { it.age shouldBeGreaterThan (0) }
people.forNone { it.name.shouldBeEmpty() }
people.forAtLeast(2) { it.age shouldBeGreaterThan (25) }
people.forExactly(1) { it.name shouldBe "Bob" }
```

### Custom Matchers

```kotlin
fun beValid() = Matcher<Email> { value ->
  MatcherResult(
    value.isValid(),
    { "expected $value to be a valid email" },
    { "expected $value to not be a valid email" }
  )
}

// Usage:
email should beValid()
email.shouldBeValid() // with extension function
```

See [references/ASSERTIONS-REFERENCE.md](references/ASSERTIONS-REFERENCE.md) for the complete matcher catalog.

## Path D: Property Testing

Property testing generates hundreds/thousands of random inputs to harden test coverage.

### Setup

```kotlin
testImplementation("io.kotest:kotest-property:<kotest-version>")
```

### Basic Usage

```kotlin
import io.kotest.property.forAll
import io.kotest.property.checkAll
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

class PropertyTests : FunSpec({
  // forAll — return a Boolean
  test("string concatenation length") {
    forAll<String, String> { a, b ->
      (a + b).length == a.length + b.length
    }
  }

  // checkAll — use assertions
  test("absolute value is non-negative") {
    checkAll<Int> { n ->
      abs(n) shouldBeGreaterThanOrEqualTo 0
    }
  }

  // With specific generators
  test("adults can vote") {
    forAll(Arb.int(18..120)) { age ->
      canVote(age)
    }
  }
})
```

### Built-in Generators (Arbs)

| Type       | Generator                                                               |
|------------|-------------------------------------------------------------------------|
| `Int`      | `Arb.int()`, `Arb.int(range)`, `Arb.positiveInt()`, `Arb.negativeInt()` |
| `Long`     | `Arb.long()`, `Arb.long(range)`                                         |
| `Double`   | `Arb.double()`, `Arb.numericDouble()`                                   |
| `String`   | `Arb.string()`, `Arb.string(size)`, `Arb.email()`, `Arb.uuid()`         |
| `Boolean`  | `Arb.boolean()`                                                         |
| `List`     | `Arb.list(elementArb)`, `Arb.list(elementArb, range)`                   |
| `Set`      | `Arb.set(elementArb)`                                                   |
| `Map`      | `Arb.map(keyArb, valueArb)`                                             |
| `Enum`     | `Arb.enum<MyEnum>()`                                                    |
| `Nullable` | `arb.orNull()`                                                          |
| `Pair`     | `Arb.pair(arbA, arbB)`                                                  |
| Custom     | `Arb.bind(arb1, arb2, ...) { a, b, ... -> MyClass(a, b) }`              |

### Custom Generators

```kotlin
val personArb = Arb.bind(
  Arb.string(5..20),
  Arb.int(1..120),
  Arb.email()
) { name, age, email ->
  Person(name, age, email)
}

checkAll(personArb) { person ->
  person.age shouldBeGreaterThan 0
}
```

See [references/PROPERTY-TESTING-REFERENCE.md](references/PROPERTY-TESTING-REFERENCE.md) for the full
generator catalog and advanced patterns.

## Path E: Advanced Configuration

### Project Config

Create a global configuration class:

```kotlin
// src/test/kotlin/io/kotest/provided/ProjectConfig.kt
package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {
  // Global assertion mode — fail if no assertions in a test
  override val assertionMode = AssertionMode.Error

  // Global soft assertions — all tests use assertSoftly
  override val globalAssertSoftly = true

  // Default timeout for all tests
  override val timeout = 10.seconds

  // Parallelism
  override val specConcurrencyMode = SpecConcurrencyMode.Concurrent
  override val testConcurrencyMode = TestConcurrencyMode.Sequential

  // Global extensions
  override val extensions = listOf(
    MyDatabaseExtension(),
  )
}
```

### Lifecycle Hooks

```kotlin
class DatabaseTest : FunSpec({
  // Per-test hooks
  beforeEach { println("Before each test") }
  afterEach { println("After each test") }

  // Per-spec hooks
  beforeSpec { println("Before all tests in this spec") }
  afterSpec { println("After all tests in this spec") }

  // Per-container / per-leaf hooks
  beforeContainer { println("Before each container test") }
  afterContainer { println("After each container test") }

  test("my test") { /* ... */ }
})
```

### Tags & Filtering

```kotlin
object Slow : Tag()
object Database : Tag()

class MyTest : FunSpec({
  test("slow integration test").config(tags = setOf(Slow, Database)) {
    // ...
  }
})

// Run only specific tags:
// ./gradlew test -Dkotest.tags="Slow & Database"
// ./gradlew test -Dkotest.tags="!Slow"  (exclude)
```

### Isolation Modes

```kotlin
class StateTest : FunSpec({
  isolationMode = IsolationMode.InstancePerRoot  // fresh instance per root test

  val counter = AtomicInteger(0)

  test("first") { counter.incrementAndGet() shouldBe 1 }
  test("second") { counter.incrementAndGet() shouldBe 1 }  // passes! fresh instance
})
```

| Mode                       | Behavior                              |
|----------------------------|---------------------------------------|
| `SingleInstance` (default) | One spec instance for all tests       |
| `InstancePerRoot`          | New spec instance per root-level test |

### Coroutine Testing

All Kotest tests run inside coroutines by default:

```kotlin
class CoroutineTest : FunSpec({
  test("async operations") {
    val deferred = async { fetchData() }
    deferred.await() shouldBe expectedData
  }

  test("with timeout").config(timeout = 5.seconds) {
    // will fail if takes longer than 5 seconds
    longRunningOperation()
  }
})
```

### Non-Deterministic Testing

```kotlin
// Eventually — retry until passes or timeout
eventually(5.seconds) {
  eventStore.getById(id).status shouldBe "COMPLETED"
}

// Continually — assert remains true for duration
continually(2.seconds) {
  server.isHealthy().shouldBeTrue()
}

// Retry — with custom config
retry(3, 1.seconds) {
  api.call() shouldBe success
}
```

### Data-Driven Testing

```kotlin
class ConversionTest : FunSpec({
  context("celsius to fahrenheit") {
    withTests(
      Pair(0, 32),
      Pair(100, 212),
      Pair(-40, -40),
    ) { (celsius, fahrenheit) ->
      cToF(celsius) shouldBe fahrenheit
    }
  }
})
```

For data classes with more fields, use `data class` rows:

```kotlin
data class PythagTriple(val a: Int, val b: Int, val c: Int)

class PythagorasTest : FunSpec({
  withTests(
    PythagTriple(3, 4, 5),
    PythagTriple(5, 12, 13),
    PythagTriple(8, 15, 17),
  ) { (a, b, c) ->
    a * a + b * b shouldBe c * c
  }
})
```

### Extensions

Create reusable test plugins:

```kotlin
class DatabaseExtension : BeforeSpecListener, AfterSpecListener {
  override suspend fun beforeSpec(spec: Spec) {
    Database.start()
    Database.migrate()
  }
  override suspend fun afterSpec(spec: Spec) {
    Database.stop()
  }
}

// Register in a spec
class MySpec : FunSpec({
  extension(DatabaseExtension())
  // tests ...
})

// Or register globally in ProjectConfig
object ProjectConfig : AbstractProjectConfig() {
  override val extensions = listOf(DatabaseExtension())
}
```

### AutoClose

Automatically close resources after a spec:

```kotlin
class ResourceTest : FunSpec({
  val connection = autoClose(Database.connect())

  test("query") {
    connection.query("SELECT 1").shouldNotBeNull()
  }
  // connection is automatically closed after all tests
})
```

## Path F: KMP Testing

See [references/SETUP-REFERENCE.md](references/SETUP-REFERENCE.md) for platform-specific setup.

### Key Points

- **JVM**: Uses `kotest-runner-junit5` + JUnit Platform
- **JS / WasmJS / Native**: Uses `kotest-framework-engine` + KSP + Kotest Gradle plugin
- **Android**: Uses `kotest-runner-junit4` for instrumented tests, `kotest-runner-junit5` for unit tests
- **commonTest**: Tests in `commonTest` run on all targets
- Non-JVM engines are feature-limited (no annotation-based config, no classpath scanning)

### Common Gotchas

- Tests must be in a class extending a Spec style — top-level functions are not discovered
- On JS/Native: `@AutoScan` does not work; register extensions explicitly
- On Native: Data-driven tests require at least one non-data test in the spec
- Android instrumented tests use JUnit4 runner: `kotest-runner-junit4`

## Path G: Kotest Debugging

See [references/KOTEST-DEBUGGING.md](references/KOTEST-DEBUGGING.md) for the full guide.

### Quick Checklist

1. **Enable debug logging** — set `KOTEST_DEBUG=true` to get verbose engine output
2. **Collect versions** — Kotest, Kotlin, Gradle, JVM, and target platform
3. **Capture full output** — `KOTEST_DEBUG=true ./gradlew test 2>&1 | tee kotest-debug.log`
4. **Check test reports** — `build/reports/tests/test/index.html`
5. **Isolate the failure** — `./gradlew test --tests "com.example.MyFailingTest"`
6. **Verify ProjectConfig** — grep debug output for `projectconfig` if config changes have no effect
7. **Reproduce minimally** — stripped-down spec with no external dependencies

## Verification

After setup or migration, verify with the [checklist](assets/checklist.md). Key checks:

1. `./gradlew test` succeeds with no errors
2. Tests are discovered and executed (check test report)
3. IntelliJ shows gutter icons for running individual tests
4. Assertions produce clear failure messages with diffs
5. No stale JUnit imports remain (if migrated)

## Common Issues

See [references/KNOWN-ISSUES.md](references/KNOWN-ISSUES.md) for details. Key gotchas:

### Framework Issues

- **Tests not discovered** — ensure `useJUnitPlatform()` is in build config (JVM), or KSP + Kotest plugin applied (KMP)
- **ProjectConfig not picked up** — must be at `io.kotest.provided.ProjectConfig` or set via system property
  `kotest.framework.config.fqn`
- **`@AutoScan` not working** — removed in Kotest 6.0; register extensions explicitly
- **Coroutine timeout** — use `.config(timeout = ...)` on tests, or `blockingTest = true` for blocking code
- **InstancePerTest deprecated** — use `InstancePerRoot` instead

### Assertion Issues

- **`assertSoftly` not catching errors** — only works with Kotest's own assertions; wrap others in
  `shouldNotThrowAny { }`
- **Matcher not found** — check the correct import; matchers are in type-specific packages like
  `io.kotest.matchers.string.*`

### Property Testing Issues

- **Test too slow** — reduce iterations: `checkAll(100) { ... }`
- **Flaky seed** — fix the seed: `checkAll(PropTestConfig(seed = 12345)) { ... }`
- **Custom type** — use `Arb.bind()` to compose generators for data classes

## Reference Files

- [Setup Reference](references/SETUP-REFERENCE.md) — platform-specific dependency and configuration setup
- [Spec Styles](references/SPEC-STYLES.md) — complete examples of all 9 spec styles
- [Assertions Reference](references/ASSERTIONS-REFERENCE.md) — full matcher catalog with examples
- [Property Testing Reference](references/PROPERTY-TESTING-REFERENCE.md) — generators, config, shrinking
- [Migration Guide](references/MIGRATION-GUIDE.md) — JUnit 4/5, TestNG, Spek migration mappings
- [Known Issues](references/KNOWN-ISSUES.md) — common problems and workarounds
- [Kotest Debugging](references/KOTEST-DEBUGGING.md) — how to gather diagnostic info and reproduce issues

