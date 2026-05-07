# Multiplatform `Arb.stringPattern` Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose `Arb.Companion.stringPattern(pattern: String): Arb<String>` from `commonMain` of `kotest-property` so it is callable from every Kotest Kotlin target. JVM keeps its existing Java rgxgen impl. Targets that `kotlin-rgxgen` 0.0.1 supports get a real impl backed by that library. All other targets throw `UnsupportedOperationException` immediately on construction.

**Architecture:** Add two intermediate source sets in `kotest-property` only — `rgxgenSupportedMain` (JS + linuxX64 + macosArm64 + mingwX64) and `rgxgenUnsupportedMain` (everything else non-JVM). Each leaf target's `Main` source set declares an extra `dependsOn` to one of them. `commonMain` declares `expect`; each of `jvmMain`, `rgxgenSupportedMain`, `rgxgenUnsupportedMain` declares `actual`. Mirror the wiring on the `Test` side so a single common smoke test exercises all platforms via an `expect val isStringPatternSupported`.

**Tech Stack:** Kotlin Multiplatform 2.3, Kotlin Gradle Plugin source-set hierarchy, `community.flock.kotlinx.rgxgen:kotlin-rgxgen:0.0.1`, existing `com.github.curious-odd-man:rgxgen:2.0` on JVM, Kotest `FunSpec` tests, binary-compatibility-validator.

**Spec:** `docs/superpowers/specs/2026-05-07-multiplatform-stringpattern-design.md`

---

## File Structure

**Created:**
- `kotest-property/src/commonMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt` — `expect` declaration
- `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringPattern.jvm.kt` — JVM `actual` (replaces `stringsjvm.kt`)
- `kotest-property/src/rgxgenSupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt` — kotlin-rgxgen `actual`
- `kotest-property/src/rgxgenUnsupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt` — throwing `actual`
- `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternCommonTest.kt` — common smoke test
- `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt` — `expect val isStringPatternSupported`
- `kotest-property/src/jvmTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.jvm.kt` — `actual val` (true)
- `kotest-property/src/rgxgenSupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt` — `actual val` (true)
- `kotest-property/src/rgxgenUnsupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt` — `actual val` (false)

**Modified:**
- `gradle/libs.versions.toml` — add `kotlin-rgxgen` version + library entry
- `kotest-property/build.gradle.kts` — add `rgxgenSupported{Main,Test}` and `rgxgenUnsupported{Main,Test}` source sets and wiring

**Deleted:**
- `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringsjvm.kt` — content moved into `stringPattern.jvm.kt`

The existing `kotest-property/src/jvmTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternTest.kt` is **not modified** — it remains a JVM-only thread-pool stress test.

---

## Task 1: Add `kotlin-rgxgen` to the version catalog

**Files:**
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Add the version entry**

In `gradle/libs.versions.toml`, under `[versions]`, find the existing line:

```toml
rgxgen = "2.0"
```

Add the new line **immediately below** it:

```toml
rgxgen = "2.0"
kotlin-rgxgen = "0.0.1"
```

- [ ] **Step 2: Add the library entry**

Under `[libraries]`, find the existing line:

```toml
rgxgen = { module = "com.github.curious-odd-man:rgxgen", version.ref = "rgxgen" }
```

Add the new line **immediately below** it:

```toml
rgxgen = { module = "com.github.curious-odd-man:rgxgen", version.ref = "rgxgen" }
kotlin-rgxgen = { module = "community.flock.kotlinx.rgxgen:kotlin-rgxgen", version.ref = "kotlin-rgxgen" }
```

- [ ] **Step 3: Verify Gradle still resolves**

Run:
```bash
./gradlew help -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`. (No code references the new entry yet, so this only validates the catalog parses.)

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml
git commit -m "Add kotlin-rgxgen 0.0.1 to version catalog

Adds community.flock.kotlinx.rgxgen:kotlin-rgxgen:0.0.1 alongside the
existing JVM-only rgxgen, in preparation for using it on non-JVM targets.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
```

---

## Task 2: Add intermediate source sets and wiring

**Files:**
- Modify: `kotest-property/build.gradle.kts`

- [ ] **Step 1: Replace the `kotlin { sourceSets { ... } }` block**

Open `kotest-property/build.gradle.kts`. The current file is:

```kotlin
plugins {
   id("kotest-jvm-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsCore)
            api(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestAssertions.kotestAssertionsTable)
         }
      }
   }
}
```

Replace the whole `kotlin { sourceSets { ... } }` block with:

```kotlin
kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsCore)
            api(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestAssertions.kotestAssertionsTable)
         }
      }

      // ------------------------------------------------------------------
      // Intermediate source sets that route each non-JVM target to one of
      // two implementations of Arb.stringPattern:
      //   - rgxgenSupported*: backed by community.flock.kotlinx.rgxgen
      //   - rgxgenUnsupported*: throws UnsupportedOperationException
      //
      // The two groups are wired in addition to (not in place of) the
      // hierarchy template used by kotest-js/native/android-native
      // conventions; each leaf source set ends up with multiple parents,
      // which Kotlin handles fine.
      // ------------------------------------------------------------------

      val rgxgenSupportedMain by creating {
         dependsOn(commonMain.get())
         dependencies {
            implementation(libs.kotlin.rgxgen)
         }
      }
      val rgxgenUnsupportedMain by creating {
         dependsOn(commonMain.get())
      }
      val rgxgenSupportedTest by creating {
         dependsOn(commonTest.get())
      }
      val rgxgenUnsupportedTest by creating {
         dependsOn(commonTest.get())
      }

      // JS targets (always present via kotest-js-conventions when not jvmOnly)
      findByName("jsMain")?.dependsOn(rgxgenSupportedMain)
      findByName("jsTest")?.dependsOn(rgxgenSupportedTest)

      findByName("wasmJsMain")?.dependsOn(rgxgenUnsupportedMain)
      findByName("wasmJsTest")?.dependsOn(rgxgenUnsupportedTest)

      // Native targets (only present when Kotlin Native is enabled)
      val supportedNativeTargets = listOf(
         "linuxX64",
         "macosArm64",
         "mingwX64",
      )
      val unsupportedNativeTargets = listOf(
         "linuxArm64",
         "iosX64", "iosArm64", "iosSimulatorArm64",
         "tvosArm64", "tvosSimulatorArm64",
         "watchosArm32", "watchosArm64",
         "watchosSimulatorArm64", "watchosDeviceArm64",
         "androidNativeX86", "androidNativeX64", "androidNativeArm64",
      )
      supportedNativeTargets.forEach { target ->
         findByName("${target}Main")?.dependsOn(rgxgenSupportedMain)
         findByName("${target}Test")?.dependsOn(rgxgenSupportedTest)
      }
      unsupportedNativeTargets.forEach { target ->
         findByName("${target}Main")?.dependsOn(rgxgenUnsupportedMain)
         findByName("${target}Test")?.dependsOn(rgxgenUnsupportedTest)
      }
   }
}
```

- [ ] **Step 2: Verify the build configures**

Run:
```bash
./gradlew :kotest-property:tasks -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`. The new source sets are empty so JVM compile is unaffected.

- [ ] **Step 3: Verify the build configures with native enabled**

Run:
```bash
./gradlew :kotest-property:tasks
```
Expected: `BUILD SUCCESSFUL`. This validates the `findByName` calls don't NPE on a real KMP target setup.

- [ ] **Step 4: Commit**

```bash
git add kotest-property/build.gradle.kts
git commit -m "Add rgxgenSupported/rgxgenUnsupported source sets to kotest-property

Introduces two intermediate source sets that each non-JVM leaf target
joins via dependsOn. The supported set carries the kotlin-rgxgen
dependency; the unsupported set is a placeholder for the throwing
implementation added in a follow-up commit. No code yet uses either —
this commit only sets up the wiring.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
```

---

## Task 3: Convert `stringPattern` to multiplatform `expect`/`actual`

This task is one logical change — the build will not compile between substeps because `expect` requires `actual` on every target. Make all the file changes, then verify and commit once.

**Files:**
- Create: `kotest-property/src/commonMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`
- Create: `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringPattern.jvm.kt`
- Create: `kotest-property/src/rgxgenSupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`
- Create: `kotest-property/src/rgxgenUnsupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`
- Delete: `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringsjvm.kt`

- [ ] **Step 1: Create the `expect` declaration in commonMain**

Path: `kotest-property/src/commonMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`

```kotlin
package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Generate strings that match the given regex pattern.
 *
 * Backed by:
 *  - JVM: [com.github.curious-odd-man:rgxgen](https://github.com/curious-odd-man/RgxGen)
 *  - JS, linuxX64, macosArm64, mingwX64:
 *    [community.flock.kotlinx.rgxgen:kotlin-rgxgen](https://github.com/flock-community/kotlin-rgxgen)
 *  - Wasm and other Native targets: not supported — calling this constructor
 *    throws [UnsupportedOperationException]. `kotlin-rgxgen` 0.0.1 does not
 *    publish a binary for those targets.
 *
 * Both backing libraries support a restricted subset of regular expression
 * constructs.
 */
expect fun Arb.Companion.stringPattern(pattern: String): Arb<String>
```

- [ ] **Step 2: Create the JVM `actual` and delete the old file**

Path: `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringPattern.jvm.kt`

```kotlin
package io.kotest.property.arbitrary

import com.github.curiousoddman.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import java.util.Random

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> = sampleStringPattern(rs)

   private fun sampleStringPattern(rs: RandomSource): Sample<String> = synchronized(this) {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      Sample(value)
   }
}
```

This is byte-for-byte the existing `stringsjvm.kt` content with the function declaration prefixed by `actual` and the KDoc removed (it now lives on the `expect`).

Then delete the old file (this both removes the file from the working tree and stages the deletion):

```bash
git rm kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringsjvm.kt
```

- [ ] **Step 3: Create the kotlin-rgxgen `actual`**

Path: `kotest-property/src/rgxgenSupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`

```kotlin
package io.kotest.property.arbitrary

import community.flock.kotlinx.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.random.Random

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      return Sample(value)
   }
}
```

Notes:
- `kotlin.random.Random` (not `java.util.Random`) — kotlin-rgxgen accepts the cross-platform `Random`.
- No `synchronized(this)` wrapper. JS/Wasm are single-threaded; on Native, kotlin-rgxgen 0.0.1's thread-safety is undocumented. If a regression appears under concurrent load on Native, follow-up by adding `kotlinx.coroutines.sync.Mutex` here. Documented as Risk #1 in the spec.

- [ ] **Step 4: Create the throwing `actual`**

Path: `kotest-property/src/rgxgenUnsupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt`

```kotlin
package io.kotest.property.arbitrary

import io.kotest.property.Arb

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> {
   throw UnsupportedOperationException(
      "Arb.stringPattern is not supported on this Kotlin/Native or Wasm target; " +
         "kotlin-rgxgen does not publish a binary for it."
   )
}
```

- [ ] **Step 5: Compile and run the existing JVM test as a regression check**

Run:
```bash
./gradlew :kotest-property:jvmTest --tests "com.sksamuel.kotest.property.arbitrary.StringPatternTest" -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`, both tests in `StringPatternTest` pass. (This proves the JVM behaviour is unchanged.)

- [ ] **Step 6: Compile non-JVM targets**

Run:
```bash
./gradlew :kotest-property:compileKotlinJs :kotest-property:compileKotlinWasmJs
```
Expected: `BUILD SUCCESSFUL`.

If Native is enabled locally, also:
```bash
./gradlew :kotest-property:compileKotlinLinuxX64 :kotest-property:compileKotlinMacosArm64 :kotest-property:compileKotlinMingwX64
```
(only the host's targets will succeed — that's fine; what matters is one supported and one unsupported native target both compile if available).

- [ ] **Step 7: Commit**

```bash
# stringsjvm.kt deletion is already staged from Step 2's `git rm`.
git add kotest-property/src/commonMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt \
        kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringPattern.jvm.kt \
        kotest-property/src/rgxgenSupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt \
        kotest-property/src/rgxgenUnsupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt
git commit -m "Make Arb.stringPattern multiplatform via expect/actual

Promotes Arb.stringPattern to commonMain. JVM keeps the existing
com.github.curious-odd-man:rgxgen implementation. JS, linuxX64,
macosArm64 and mingwX64 use community.flock.kotlinx.rgxgen:kotlin-rgxgen.
Wasm and the remaining Native targets throw UnsupportedOperationException
on construction.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
```

---

## Task 4: Add a common smoke test

This task adds one test that runs on every target, asserting the right behaviour for that target via an `expect val isStringPatternSupported` flag.

**Files:**
- Create: `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`
- Create: `kotest-property/src/jvmTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.jvm.kt`
- Create: `kotest-property/src/rgxgenSupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`
- Create: `kotest-property/src/rgxgenUnsupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`
- Create: `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternCommonTest.kt`

- [ ] **Step 1: Write the failing common test**

Path: `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternCommonTest.kt`

```kotlin
package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take

class StringPatternCommonTest : FunSpec({
   test("Arb.stringPattern is wired correctly per platform") {
      if (isStringPatternSupported) {
         Arb.stringPattern("[a-z]{3}").take(50).forEach {
            it.shouldMatch(Regex("[a-z]{3}"))
         }
      } else {
         shouldThrow<UnsupportedOperationException> {
            Arb.stringPattern("[a-z]{3}")
         }
      }
   }
})
```

- [ ] **Step 2: Run the test — expect compile failure**

Run:
```bash
./gradlew :kotest-property:compileTestKotlinJvm -PjvmOnly=true
```
Expected: **FAILS** with `unresolved reference: isStringPatternSupported`. This proves the test is reaching for a value that does not yet exist.

- [ ] **Step 3: Add the `expect val`**

Path: `kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`

```kotlin
package com.sksamuel.kotest.property.arbitrary

internal expect val isStringPatternSupported: Boolean
```

- [ ] **Step 4: Add the JVM `actual`**

Path: `kotest-property/src/jvmTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.jvm.kt`

```kotlin
package com.sksamuel.kotest.property.arbitrary

internal actual val isStringPatternSupported: Boolean = true
```

- [ ] **Step 5: Add the supported `actual`**

Path: `kotest-property/src/rgxgenSupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`

```kotlin
package com.sksamuel.kotest.property.arbitrary

internal actual val isStringPatternSupported: Boolean = true
```

- [ ] **Step 6: Add the unsupported `actual`**

Path: `kotest-property/src/rgxgenUnsupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt`

```kotlin
package com.sksamuel.kotest.property.arbitrary

internal actual val isStringPatternSupported: Boolean = false
```

- [ ] **Step 7: Run the JVM test — expect pass**

Run:
```bash
./gradlew :kotest-property:jvmTest --tests "com.sksamuel.kotest.property.arbitrary.StringPatternCommonTest" -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`. The single test passes (JVM is supported, so the `if` branch runs and the regex matches).

- [ ] **Step 8: Run the JS test**

Run:
```bash
./gradlew :kotest-property:jsNodeTest --tests "*StringPatternCommonTest*"
```
Expected: `BUILD SUCCESSFUL`. JS uses the supported `actual`.

- [ ] **Step 9: Run the Wasm JS test**

Run:
```bash
./gradlew :kotest-property:wasmJsNodeTest --tests "*StringPatternCommonTest*"
```
Expected: `BUILD SUCCESSFUL`. Wasm uses the unsupported `actual`; the test asserts the constructor throws.

- [ ] **Step 10: Commit**

```bash
git add kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternCommonTest.kt \
        kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt \
        kotest-property/src/jvmTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.jvm.kt \
        kotest-property/src/rgxgenSupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt \
        kotest-property/src/rgxgenUnsupportedTest/kotlin/com/sksamuel/kotest/property/arbitrary/stringPatternSupport.kt
git commit -m "Add common smoke test for Arb.stringPattern

Asserts that Arb.stringPattern matches its declared regex on supported
targets and throws UnsupportedOperationException on unsupported ones.
The split is driven by an internal expect val isStringPatternSupported.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
```

---

## Task 5: API binary-compatibility check

The JVM API surface is unchanged: `stringPattern` is still public, still in `io.kotest.property.arbitrary`, still has the same signature. `apiCheck` should pass without any `apiDump`.

**Files:** none changed normally. If drift is reported, the dump file will be `kotest-property/api/kotest-property.api`.

- [ ] **Step 1: Run `apiCheck`**

Run:
```bash
./gradlew :kotest-property:apiCheck -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2: If `apiCheck` fails — dump and inspect**

(Skip this step on success.)

```bash
./gradlew :kotest-property:apiDump -PjvmOnly=true
git diff kotest-property/api/kotest-property.api
```

The expected diff is **empty** — adding `expect`/`actual` should not change the JVM-emitted public API. If real drift appears, stop and inspect: it likely indicates a missed `actual` or accidental visibility change. Resolve before continuing. Once intentional, commit:

```bash
git add kotest-property/api/kotest-property.api
git commit -m "Refresh kotest-property API dump after stringPattern multiplatformization

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>"
```

---

## Task 6: Final verification

- [ ] **Step 1: JVM-only check (fast)**

Run:
```bash
./gradlew :kotest-property:check -PjvmOnly=true
```
Expected: `BUILD SUCCESSFUL`. Both `StringPatternTest` and `StringPatternCommonTest` are in the JVM test suite.

- [ ] **Step 2: Multiplatform check**

Run:
```bash
./gradlew :kotest-property:check
```
Expected: `BUILD SUCCESSFUL` for the targets the host can build (JVM, JS, Wasm JS, plus host-specific Native). On macOS the host builds `macosArm64` (supported); on Linux it builds `linuxX64` (supported) and `linuxArm64` (unsupported). Both branches of the smoke test are exercised somewhere in this run.

- [ ] **Step 3: Spot-check a single supported native target if available**

If on Linux:
```bash
./gradlew :kotest-property:linuxX64Test --tests "*StringPatternCommonTest*"
```
If on macOS:
```bash
./gradlew :kotest-property:macosArm64Test --tests "*StringPatternCommonTest*"
```
Expected: `BUILD SUCCESSFUL`, the supported branch passes (regex matches).

- [ ] **Step 4: Spot-check an unsupported native target if available**

If on Linux:
```bash
./gradlew :kotest-property:linuxArm64Test --tests "*StringPatternCommonTest*"
```
(Skip if cross-compile to `linuxArm64` isn't available on the build host.) Expected: `BUILD SUCCESSFUL`, the unsupported branch passes (constructor throws).

- [ ] **Step 5: Verify there are no stray references to the old file**

Run:
```bash
grep -r "stringsjvm" kotest-property/ || echo "no references remaining"
```
Expected: `no references remaining`.

---

## Notes for the implementer

1. **Three-space indentation** throughout — matches the rest of the Kotest codebase. (Specified in `CLAUDE.md`.)
2. **Test package** is `com.sksamuel.kotest.property.arbitrary` (legacy, intentional — see `CLAUDE.md` "The base package is `io.kotest`. Test code historically also uses `com.sksamuel.kotest`").
3. **Don't refactor** `Arb.string`, `StringShrinkerWithMin`, etc. They are out of scope.
4. **kotlin-rgxgen 0.0.1 thread-safety** on Native is undocumented. The plan deliberately omits a `synchronized` wrapper on non-JVM targets — single-threaded JS/Wasm don't need it, and Native concurrency is left for a follow-up if a problem surfaces. JVM keeps the existing `synchronized(this)`.
5. **`expect val` placement.** The JVM `actual` lives in `jvmTest` (parent of nothing — JVM does not route through `rgxgenSupportedTest`). The non-JVM `actual`s live in the two intermediate `Test` source sets. Each compilation gets exactly one `actual` for `isStringPatternSupported`.
6. **`findByName` is intentional** in the wiring. Source sets like `linuxArm64Main` only exist when Kotlin Native is enabled; in JVM-only mode they are `null` and silently skipped.
7. **If a target name changes upstream** (e.g., a future Kotlin renames `androidNativeArm64`), the `findByName` lookup will silently no-op and a missing-actual compile error will surface — at which point the lists in the build script need updating.
