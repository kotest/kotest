---
name: kotest
description: >
  Write, review, migrate, and debug Kotlin tests with Kotest 6.x: spec styles (FunSpec, DescribeSpec,
  BehaviorSpec, StringSpec, ...), matchers (shouldBe, shouldThrow, assertSoftly, eventually), data-driven
  tests (withTests/withData), property-based testing (Arb, checkAll, forAll, permutations), lifecycle hooks,
  extensions (Spring, Testcontainers, Koin), ProjectConfig, coroutine/virtual-time testing, tags, and
  Kotlin Multiplatform setup. Use when a project depends on io.kotest, when editing *Spec/*Test.kt files that
  import io.kotest, when the user mentions Kotest, kotest matchers, Arb or property testing in Kotlin, or
  when migrating from JUnit 4/5, TestNG, or Spek to Kotest.
license: Apache-2.0
metadata:
  author: Kotest Team
---

# Kotest

This skill covers Kotest **6.2**. Kotest has three parts that can be used independently: the test
framework, the assertions library (which works under any framework, including JUnit), and property testing.

## Step 0: Read the Project Before Writing Anything

1. **Find the Kotest version** in `build.gradle.kts` or `gradle/libs.versions.toml`.
   - 6.x needs Kotlin 2.2+ and JDK 11+.
   - On **5.x** many APIs below differ, so check the project's existing tests before using them.
2. **Find the targets**: JVM-only, Android, or KMP (`kotlin("multiplatform")`, `commonTest`).
3. **Match the existing tests**: same spec style, package (`io.kotest` or `com.sksamuel.kotest` in old code),
   indentation, and helpers. Don't introduce a second spec style into a module.
4. Look for an existing `ProjectConfig` / `PackageConfig` and any registered extensions.

With Bash available, `scripts/analyze-project.sh [dir]` prints a summary of all of this.

## Pick the Path

| Task                                                    | Read                                                                         |
|---------------------------------------------------------|------------------------------------------------------------------------------|
| New project / add Kotest to a build                     | [SETUP-REFERENCE.md](references/SETUP-REFERENCE.md)                          |
| Write tests in a style                                  | [SPEC-STYLES.md](references/SPEC-STYLES.md)                                  |
| Choose a matcher, custom matchers, soft assertions      | [ASSERTIONS-REFERENCE.md](references/ASSERTIONS-REFERENCE.md)                |
| Data-driven tests, config, coroutines, tags, extensions | [FRAMEWORK-REFERENCE.md](references/FRAMEWORK-REFERENCE.md)                  |
| Property-based testing                                  | [PROPERTY-TESTING-REFERENCE.md](references/PROPERTY-TESTING-REFERENCE.md)    |
| Migrate from JUnit / TestNG / Spek                      | [MIGRATION-GUIDE.md](references/MIGRATION-GUIDE.md)                          |
| Tests not running, config ignored, hangs                | [KOTEST-DEBUGGING.md](references/KOTEST-DEBUGGING.md), [KNOWN-ISSUES.md](references/KNOWN-ISSUES.md) |

Load only the reference you need.

## Minimal Setup

```kotlin
// JVM — build.gradle.kts
dependencies {
   testImplementation("io.kotest:kotest-runner-junit5:<version>")
   testImplementation("io.kotest:kotest-assertions-core:<version>")
   testImplementation("io.kotest:kotest-property:<version>")   // if needed
}
tasks.withType<Test>().configureEach { useJUnitPlatform() }
```

For KMP, apply `id("com.google.devtools.ksp")` and `id("io.kotest")`, then put `kotest-framework-engine` and
`kotest-assertions-core` in `commonTest`. Add `kotest-runner-junit5` and `useJUnitPlatform()` for the JVM target.
See SETUP-REFERENCE for JS, Wasm, Native, and Android.

## Writing Good Kotest Tests

```kotlin
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

class OrderServiceTest : FunSpec({
   val repo = InMemoryOrderRepo()
   val service = OrderService(repo)

   beforeEach { repo.clear() }

   context("placing an order") {
      test("stores the order with its lines") {
         val order = service.place(cart("apple", "pear"))
         assertSoftly(repo.find(order.id)) {
            status shouldBe Status.PLACED
            lines.map { it.sku } shouldContainExactly listOf("apple", "pear")
         }
      }

      test("rejects an empty cart") {
         val e = shouldThrow<IllegalArgumentException> { service.place(cart()) }
         e.message shouldContain "empty"
      }

      withTests(-1, 0) { qty ->
         shouldThrow<IllegalArgumentException> { service.place(cart("apple" to qty)) }
      }
   }

   test("returns a typed result") {
      val result = service.lookup("A-1").shouldBeInstanceOf<Found>()   // smart-casts and returns Found
      withClue("lookup A-1") { result.order.id shouldBe "A-1" }
   }
})
```

Rules:

- **Use the most specific matcher.** Write `list.shouldContain(x)`, `str.shouldStartWith("a")`, `n shouldBeGreaterThan 0`
  rather than `(cond) shouldBe true`, so the failure message carries the actual values.
- **Use the returned value from the matcher.** `shouldThrow`, `shouldNotBeNull`, `shouldBeInstanceOf`,
  `shouldBeSingle`, and `shouldBeSuccess` return their subject, so you don't need `!!` or casts.
- **Test bodies are `suspend`.** Never wrap them in `runBlocking`. For delays, use `coroutineTestScope = true` and
  virtual time.
- **Wait with `eventually`, not `Thread.sleep`.** Write `eventually(5.seconds) { ... }` to poll for an asynchronous
  result, and `continually` to assert that something stays true.
- **Group related assertions with `assertSoftly`** so one run reports every failure. Use `withClue` or `asClue` to add context.
- **Use data-driven tests instead of loops or copy-paste.** In FunSpec and FreeSpec use `withTests` for leaf
  tests and `withContexts` for groups. Each style has its own variant; see FRAMEWORK-REFERENCE.
- **Use property tests for invariants.** `checkAll(Arb.int(), Arb.int()) { a, b -> (a + b) shouldBe (b + a) }`.
- **Make tests independent.** If shared mutable spec fields leak between tests, reset them in `beforeEach` or use
  `isolationMode = IsolationMode.InstancePerRoot`.
- **Name tests by behaviour**, for example `"rejects an empty cart"`, not `"test1"`. Names must be unique within a scope.

## 6.x Facts Agents Commonly Get Wrong

| Wrong (5.x / hallucinated)                              | Correct (6.2)                                                                    |
|---------------------------------------------------------|----------------------------------------------------------------------------------|
| `kotest-framework-datatest` dependency                  | Built into the engine; `import io.kotest.datatest.*`                             |
| `withData` as the only data-test function               | `withTests` / `withContexts` / per-style variants (`withIts`, `withShoulds`, …)  |
| `specConcurrencyMode`, `testConcurrencyMode`, `parallelism` | `specExecutionMode = SpecExecutionMode.Concurrent`, `testExecutionMode` (JVM only) |
| `./gradlew test -Dkotest.tags=...`                      | `KOTEST_TAGS="A & !B" ./gradlew test`, or `systemProperty(...)` on the Test task  |
| `@AutoScan` extensions                                  | Removed; register via `extension(...)` or `ProjectConfig.extensions`             |
| `IsolationMode.InstancePerTest` / `InstancePerLeaf`     | `IsolationMode.InstancePerRoot`                                                  |
| `TestContainerExtension(...)` / `ContainerExtension(...)` | `install(TestContainerSpecExtension(container))` (old ones removed in 6.3)     |
| `forAll(row(...))` table tests from core               | `kotest-assertions-table` (`io.kotest.data`), or prefer data-driven tests        |
| `import io.kotest.assertions.nondeterministic.retry`    | `import io.kotest.assertions.retry`; `retry(maxRetry, timeout, delay)`           |
| `object ProjectConfig` everywhere                       | `class ProjectConfig : AbstractProjectConfig()` (Native requires a class)        |
| `kotest-assertions-api`                                 | `kotest-assertions-shared`                                                       |
| Constructor-injected Spring spec using `extension(SpringExtension())` | `@ApplyExtension(SpringExtension::class)` on the class            |

## Recent Additions Worth Using (6.1 – 6.2)

- `a shouldEqual b` (`io.kotest.matchers.equals`): a type-safe `shouldBe`, so `1 shouldEqual "1"` doesn't compile.
- `actual withEqs { register<BigDecimal>(myEq) } shouldBe expected` (`io.kotest.assertions.eq`), where `myEq` is
  your own `Eq<BigDecimal>`: per-assertion equality overrides that apply recursively into collections and data
  classes, and are safe under concurrency (unlike the global `DefaultEqResolver.register`).
- `shouldBeEqualUsingFields { excludedProperties = setOf(Foo::updatedAt); expected }` (JVM): field-by-field diffs.
- `list.shouldBeSingle()` returns the element; `result.shouldBeFailure<IOException> { }`; `x.shouldBeInstanceOf(Foo::class)` returns `Foo`.
- `DefaultTestConfig`, `PackageConfig`, and `failfast` give layered configuration.
- `coroutineTestScope = true` with `testCoroutineScheduler` gives virtual time. On Kotest ≤ 6.2.6 also add
  `kotlinx-coroutines-test` to test deps to call scheduler methods.
- Permutations DSL (`kotest-property-permutations`, experimental) is a property-testing alternative.
- In the Gradle plugin `kotest { }` block: `kotestVersion` (aligns artifact versions), `enablePowerAssert`, and `alwaysRerunTests`.
- Custom spec styles via `AbstractSpec` + `@TestRunnable`.

## Run and Verify

```bash
./gradlew test                                          # JVM
./gradlew test --tests "com.example.OrderServiceTest"   # one spec
./gradlew allTests                                      # KMP: every target (or jvmTest, jsTest, linuxX64Test, ...)
KOTEST_DEBUG=true ./gradlew test --info                 # engine debug output (see KOTEST-DEBUGGING)
```

After writing or migrating tests, run them and confirm they were **discovered**: the report shows a non-zero
count, and "0 tests" usually means a missing `useJUnitPlatform()` or KSP plugin. If tests pass on the first
run, deliberately break one assertion to check that it can fail. For full setup or migration work, go
through [assets/checklist.md](assets/checklist.md).
