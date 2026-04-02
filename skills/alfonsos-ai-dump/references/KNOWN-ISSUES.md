# Known Issues: Kotest

Common problems, limitations, and workarounds when using Kotest.

---

## 1. Tests Not Discovered

**Problem:** Running `./gradlew test` reports 0 tests executed.

**Causes & Fixes:**

- **Missing `useJUnitPlatform()`** — Add to your build:
  ```kotlin
  tasks.withType<Test>().configureEach { useJUnitPlatform() }
  ```
- **Missing runner dependency** — Add `kotest-runner-junit5` to `testImplementation`
- **Test class not extending a Spec** — All tests must be in a class extending a Kotest spec style (e.g., `FunSpec`)
- **Tests outside `init {}` block** — Tests must be defined inside the body lambda or `init {}` block
- **KMP missing KSP/Kotest plugin** — JS/Native/WasmJS targets require both KSP and Kotest Gradle plugins
- **Wrong source set** — Ensure tests are in `src/test/kotlin` (JVM) or appropriate KMP source set

---

## 2. ProjectConfig Not Picked Up

**Problem:** Global configuration in `AbstractProjectConfig` subclass is ignored.

**Fix (Kotest 6.x):**

Place the config at one of these locations:
1. `io.kotest.provided.ProjectConfig` (default, recommended)
2. Any package common to all test classes
3. Specify via system property: `kotest.framework.config.fqn`

```kotlin
// src/test/kotlin/io/kotest/provided/ProjectConfig.kt
package io.kotest.provided

object ProjectConfig : AbstractProjectConfig() {
    // config here
}
```

**Note:** In Kotest 6.x, the config class is no longer discovered via classpath scanning. It must be at a known location.

---

## 3. @AutoScan Removed

**Problem:** Extensions annotated with `@AutoScan` are not discovered in Kotest 6.0+.

**Fix:** Register extensions explicitly:

```kotlin
// In ProjectConfig
object ProjectConfig : AbstractProjectConfig() {
    override val extensions = listOf(MyExtension())
}

// Or per-spec
class MySpec : FunSpec({
    extension(MyExtension())
})

// Or via annotation
@ApplyExtension(MyExtension::class)
class MySpec : FunSpec({ /* ... */ })
```

---

## 4. assertSoftly Not Catching Failures

**Problem:** Assertions from non-Kotest libraries (MockK `verify`, AssertJ, etc.) throw immediately and bypass `assertSoftly`.

**Fix:** Wrap non-Kotest assertions in `shouldNotThrowAnyUnit`:

```kotlin
assertSoftly {
    result shouldBe expected
    shouldNotThrowAnyUnit {
        verify(exactly = 1) { mock.method(any()) }
    }
}
```

Also use `failSoftly("message")` instead of `fail("message")` inside `assertSoftly` blocks.

---

## 5. InstancePerTest / InstancePerLeaf Deprecated

**Problem:** `IsolationMode.InstancePerTest` and `IsolationMode.InstancePerLeaf` have undefined behavior in edge cases.

**Fix:** Migrate to `IsolationMode.InstancePerRoot`:

```kotlin
class MySpec : FunSpec({
    isolationMode = IsolationMode.InstancePerRoot
    // ...
})
```

---

## 6. Coroutine Timeout Issues

**Problem:** Tests using blocking code (Thread.sleep, blocking I/O) ignore Kotest's timeout because they block the coroutine dispatcher.

**Fix:** Use `blockingTest = true`:

```kotlin
test("blocking operation").config(blockingTest = true, timeout = 5.seconds) {
    Thread.sleep(1000) // this can now be interrupted by timeout
}
```

---

## 7. Data-Driven Tests on Native

**Problem:** On Kotlin/Native, if a spec contains ONLY data-driven tests (no manual tests), test discovery may fail.

**Fix:** Add at least one regular test alongside data-driven tests:

```kotlin
class MySpec : FunSpec({
    test("placeholder") { } // ensures discovery works

    withTests(row1, row2) { /* ... */ }
})
```

---

## 8. Matcher Import Confusion

**Problem:** `shouldContain`, `shouldBe`, etc. have overloads in multiple packages. Wrong import causes compilation errors.

**Common packages:**

| Matcher | Package |
|---------|---------|
| `shouldBe` | `io.kotest.matchers.shouldBe` |
| `shouldContain` (String) | `io.kotest.matchers.string.shouldContain` |
| `shouldContain` (Collection) | `io.kotest.matchers.collections.shouldContain` |
| `shouldContain` (Map) | `io.kotest.matchers.maps.shouldContain` |
| `shouldThrow` | `io.kotest.assertions.throwables.shouldThrow` |

**Fix:** Use specific imports, not star imports across multiple matcher packages.

---

## 9. Version Mixing

**Problem:** Using different Kotest versions for different modules (e.g., `kotest-assertions-core:5.9.0` with `kotest-runner-junit5:6.0.0`) causes runtime errors.

**Fix:** Always use the same Kotest version for ALL Kotest dependencies. Use a version catalog or property:

```kotlin
val kotestVersion = "6.x.x"

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}
```

---

## 10. Gradle Caching — "0 tests executed"

**Problem:** Running `./gradlew test` shows "0 tests" because Gradle cached the previous result (no source changes).

**Fix:**

```kotlin
// Force re-run
./gradlew test --rerun

// Or disable caching for test task
tasks.withType<Test>().configureEach {
    outputs.upToDateWhen { false }  // NOT recommended for CI
}
```

---

## 11. IntelliJ Plugin Issues

**Problem:** Gutter icons don't appear, or tests can't be run individually from the IDE.

**Fix:**
1. Install the Kotest IntelliJ plugin from the JetBrains Marketplace
2. Ensure the Kotest Gradle plugin is applied for enhanced IDE integration
3. Invalidate caches and restart IntelliJ if icons are missing

---

## 12. kotest-framework-datatest Removed

**Problem:** `kotest-framework-datatest` dependency causes errors in Kotest 6.x.

**Fix:** Remove the dependency. Data-driven testing (`withTests(...)`) is now built into the core framework.

---

## 13. Extension API Changes in 6.x

**Problem:** `override fun listeners()` or `override fun extensions()` no longer compiles.

**Fix:** Change to val:

```kotlin
// Before (5.x)
override fun extensions() = listOf(MyExtension())

// After (6.x)
override val extensions = listOf(MyExtension())
```

---

## 14. Kotlin Version Incompatibility

**Problem:** Kotest 6.x requires Kotlin 2.2+. Using an older Kotlin version causes compilation errors.

**Fix:** Upgrade Kotlin to 2.2 or later before upgrading to Kotest 6.x. If you cannot upgrade Kotlin, stay on Kotest 5.x.

