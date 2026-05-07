# Multiplatform `Arb.stringPattern` — Design

**Date:** 2026-05-07
**Module:** `kotest-property`
**Status:** Approved (pending implementation)

## Goal

Make `Arb.Companion.stringPattern(pattern: String): Arb<String>` available from `commonMain`
so users on every Kotest target can call it. JVM keeps its current Java-based rgxgen
implementation. Targets supported by [`community.flock.kotlinx.rgxgen:kotlin-rgxgen:0.0.1`]
get a real implementation backed by that library. Targets not covered by the library throw
`UnsupportedOperationException` immediately on construction.

[`community.flock.kotlinx.rgxgen:kotlin-rgxgen:0.0.1`]: https://github.com/flock-community/kotlin-rgxgen

## Background

`Arb.stringPattern` exists today in `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringsjvm.kt`.
It parses a regex with `com.github.curious-odd-man:rgxgen:2.0` (a Java library) and produces a
seeded `Arb<String>` whose values match the pattern. The `synchronized(this)` wrapper exists
because rgxgen 2.0's parser tree is not thread-safe.

`kotlin-rgxgen` is a Kotlin Multiplatform port. v0.0.1 is published to Maven Central with
artifacts for: **jvm, js, linuxX64, macosX64, macosArm64, mingwX64**. Its `generate(random:
kotlin.random.Random?)` accepts a seeded `kotlin.random.Random`, matching what we need for
deterministic property tests.

### Target coverage gap

`kotest-property` applies these convention plugins: `kotest-jvm-conventions`,
`kotest-js-conventions`, `kotest-native-conventions`, `kotest-android-native-conventions`,
`kotest-watchos-device-conventions`. (It does **not** apply `kotest-wasi-conventions`, so
`wasmWasi` is not a target of this module — only `wasmJs` from the JS conventions.)

| Target                                                                   | kotlin-rgxgen 0.0.1 | This design |
| ------------------------------------------------------------------------ | ------------------- | ----------- |
| jvm                                                                      | ✅ (Java rgxgen)    | Java rgxgen (unchanged) |
| js                                                                       | ✅                  | kotlin-rgxgen |
| wasmJs                                                                   | ❌                  | throws |
| linuxX64                                                                 | ✅                  | kotlin-rgxgen |
| linuxArm64                                                               | ❌                  | throws |
| macosArm64                                                               | ✅                  | kotlin-rgxgen |
| mingwX64                                                                 | ✅                  | kotlin-rgxgen |
| iosX64 / iosArm64 / iosSimulatorArm64                                    | ❌                  | throws |
| tvosArm64 / tvosSimulatorArm64                                           | ❌                  | throws |
| watchosArm32 / watchosArm64 / watchosSimulatorArm64 / watchosDeviceArm64 | ❌                  | throws |
| androidNativeX86 / androidNativeX64 / androidNativeArm64                 | ❌                  | throws |

(`macosX64` is published by kotlin-rgxgen but is not a Kotest target, so unused.)

## Decisions

1. **JVM keeps `com.github.curious-odd-man:rgxgen:2.0`.** The existing implementation is
   battle-tested at version 2.0; kotlin-rgxgen at 0.0.1 is a young port. JVM users see no
   behaviour change.
2. **Non-JVM uses `community.flock.kotlinx.rgxgen:kotlin-rgxgen:0.0.1`** on the targets it
   provides binaries for.
3. **Unsupported targets throw on construction**, not on first sample. Users find out
   immediately, before any test executes.
4. **Two intermediate source sets** (`rgxgenSupportedMain`, `rgxgenUnsupportedMain`) carry
   the non-JVM `actual`s. The wiring lives in `kotest-property/build.gradle.kts` only — no
   shared convention plugin change, since `kotest-property` is the sole consumer.

## Architecture

### Source set hierarchy (additive)

The existing hierarchy from `kotest-js-conventions` and `kotest-native-conventions` is
**not modified**. We add two new intermediate source sets and wire each leaf target's
`Main` source set to one of them via an extra `dependsOn`.

```
commonMain
├── jvmMain                                     (existing — actual via Java rgxgen)
├── nonjvmMain                                  (existing — unchanged content)
│   ├── jsHostedMain
│   │   ├── jsMain                              + dependsOn(rgxgenSupportedMain)
│   │   └── wasmJsMain                          + dependsOn(rgxgenUnsupportedMain)
│   └── nativeMain
│       ├── linuxX64Main                        + dependsOn(rgxgenSupportedMain)
│       ├── linuxArm64Main                      + dependsOn(rgxgenUnsupportedMain)
│       ├── macosArm64Main                      + dependsOn(rgxgenSupportedMain)
│       ├── mingwX64Main                        + dependsOn(rgxgenSupportedMain)
│       ├── iosX64Main / iosArm64Main / iosSimulatorArm64Main
│       │       all + dependsOn(rgxgenUnsupportedMain)
│       ├── tvosArm64Main / tvosSimulatorArm64Main
│       │       all + dependsOn(rgxgenUnsupportedMain)
│       ├── watchosArm32Main / watchosArm64Main / watchosSimulatorArm64Main / watchosDeviceArm64Main
│       │       all + dependsOn(rgxgenUnsupportedMain)
│       └── androidNativeX86Main / androidNativeX64Main / androidNativeArm64Main
│               all + dependsOn(rgxgenUnsupportedMain)
├── rgxgenSupportedMain    [new]                (actual via kotlin-rgxgen)
└── rgxgenUnsupportedMain  [new]                (actual throws)
```

Mirror `*Test` source sets for `commonTest`-side tests if needed (see Tests).

### Files

```
kotest-property/src/commonMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt
   └─ expect fun Arb.Companion.stringPattern(pattern: String): Arb<String>

kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringPattern.jvm.kt
   └─ actual fun Arb.Companion.stringPattern(...): Arb<String>
      // existing logic from stringsjvm.kt: com.github.curious-odd-man rgxgen,
      // synchronized(this), java.util.Random(rs.random.nextLong()).

kotest-property/src/rgxgenSupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt
   └─ actual fun Arb.Companion.stringPattern(...): Arb<String>
      // community.flock.kotlinx.rgxgen.RgxGen.parse(pattern)
      //   .generate(kotlin.random.Random(rs.random.nextLong()))

kotest-property/src/rgxgenUnsupportedMain/kotlin/io/kotest/property/arbitrary/stringPattern.kt
   └─ actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> {
        throw UnsupportedOperationException(
           "Arb.stringPattern is not supported on this Kotlin/Native or Wasm target; " +
           "kotlin-rgxgen does not publish a binary for it."
        )
      }
```

The existing file `kotest-property/src/jvmMain/kotlin/io/kotest/property/arbitrary/stringsjvm.kt`
is **deleted**; its content moves into `stringPattern.jvm.kt` with the function declaration
prefixed by `actual`.

### Build script changes

`kotest-property/build.gradle.kts`:

```kotlin
kotlin {
   sourceSets {

      val rgxgenSupportedMain by creating {
         dependsOn(commonMain.get())
         dependencies {
            implementation(libs.kotlin.rgxgen)
         }
      }
      val rgxgenUnsupportedMain by creating {
         dependsOn(commonMain.get())
      }

      // wire JS
      jsMain { dependsOn(rgxgenSupportedMain) }

      // wire Wasm (unsupported). kotest-property does not apply
      // kotest-wasi-conventions, so no wasmWasi target exists here.
      wasmJsMain { dependsOn(rgxgenUnsupportedMain) }

      // wire native (only if Kotlin Native is enabled — findByName returns null otherwise)
      findByName("linuxX64Main")?.dependsOn(rgxgenSupportedMain)
      findByName("macosArm64Main")?.dependsOn(rgxgenSupportedMain)
      findByName("mingwX64Main")?.dependsOn(rgxgenSupportedMain)

      listOf(
         "linuxArm64Main",
         "iosX64Main", "iosArm64Main", "iosSimulatorArm64Main",
         "tvosArm64Main", "tvosSimulatorArm64Main",
         "watchosArm32Main", "watchosArm64Main",
         "watchosSimulatorArm64Main", "watchosDeviceArm64Main",
         "androidNativeX86Main", "androidNativeX64Main", "androidNativeArm64Main",
      ).forEach { findByName(it)?.dependsOn(rgxgenUnsupportedMain) }

      // existing dependency wiring
      commonMain { ... unchanged }
      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)              // unchanged — Java rgxgen stays on JVM
         }
      }
      commonTest { ... unchanged }
   }
}
```

`findByName` is used because the relevant target source sets only exist when the
corresponding convention is active (Native disabled in JVM-only mode).

### `gradle/libs.versions.toml`

Add:

```toml
[versions]
kotlin-rgxgen = "0.0.1"

[libraries]
kotlin-rgxgen = { module = "community.flock.kotlinx.rgxgen:kotlin-rgxgen", version.ref = "kotlin-rgxgen" }
```

`rgxgen = "2.0"` and the `rgxgen` library entry stay.

## Public API

```kotlin
// commonMain
package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Generate strings that match the given regex pattern.
 *
 * Backed by:
 *  - JVM:    com.github.curious-odd-man:rgxgen
 *  - JS / linuxX64 / macosArm64 / mingwX64:
 *            community.flock.kotlinx.rgxgen:kotlin-rgxgen
 *  - Wasm and other Native targets: not supported — calling this constructor
 *    throws [UnsupportedOperationException].
 *
 * Both backing libraries support a restricted subset of regular expression
 * constructs.
 */
expect fun Arb.Companion.stringPattern(pattern: String): Arb<String>
```

## Tests

`commonTest` already exists in `kotest-property` and runs across every active target.
Add the smoke tests there so coverage tracks platform automatically.

```
kotest-property/src/commonTest/kotlin/com/sksamuel/kotest/property/arbitrary/StringPatternCommonTest.kt
```

Strategy: split the assertion via two tests, gated by an `expect val`:

```kotlin
// commonMain (test source)
internal expect val isStringPatternSupported: Boolean

// rgxgenSupportedMain test source
internal actual val isStringPatternSupported: Boolean = true

// rgxgenUnsupportedMain test source
internal actual val isStringPatternSupported: Boolean = false

// jvmMain test source (exists for symmetry only — JVM is supported)
internal actual val isStringPatternSupported: Boolean = true
```

Then a single common test:

```kotlin
class StringPatternCommonTest : FunSpec({
   test("stringPattern is wired correctly per platform") {
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

The existing JVM-only `jvmTest/StringPatternTest.kt` stays where it is — it relies on
`newFixedThreadPoolContext`, which is JVM-only.

To keep the `expect val` test machinery isolated, the test source sets get the same
`dependsOn` wiring as their main counterparts (`rgxgenSupportedTest` / `rgxgenUnsupportedTest`
intermediate source sets). This matches Kotlin's expected pattern: each `Test` source set
dependsOn its corresponding `Main` for `internal` access plus the relevant `Test` parent.

## Binary compatibility

`kotest-property/api/kotest-property.api` (JVM only) is the single tracked `.api` file
today — the binary-compatibility-validator setup checks JVM only. The JVM API surface
is unchanged: `Arb.Companion.stringPattern(String): Arb` is still emitted from the same
package; only its underlying source mechanism changes (declaration in commonMain via
`expect`, definition in jvmMain via `actual`). Run

```bash
./gradlew :kotest-property:apiDump
```

and commit any drift, but no new entries are expected.

## Risks and open questions

1. **kotlin-rgxgen thread-safety.** The JVM impl wraps `rgxgen.generate(...)` in
   `synchronized(this)` because Java rgxgen's parser tree mutates during generation.
   Whether kotlin-rgxgen needs the same is unclear from its README. **Plan:** verify by
   reading kotlin-rgxgen's source during implementation; if uncertain, mirror the
   `synchronized` wrapper to stay safe. (`synchronized` works on JVM/Native; on JS it is a
   no-op, which is fine because JS is single-threaded.)
2. **kotlin-rgxgen 0.0.1 maturity.** Limited release history. Acceptable risk per the
   choice to keep Java rgxgen on JVM, isolating risk to the new platforms.
3. **Wasm test execution.** Wasm tests can be sensitive to `take(N)` and coroutine usage.
   The common smoke test deliberately uses a small N (50) and simple `forEach`.
4. **Source set wiring discoverability.** The `findByName(...)` calls in the build script
   are silent if a target name changes. **Plan:** add a brief comment block listing which
   targets go where, and rely on CI to flag drift if a target gets renamed (the missing
   `actual` would fail the build).
5. **`apiCheck` baseline.** First PR run will fail `apiCheck` until `apiDump` is run.
   Standard workflow per `CLAUDE.md`.

## Out of scope

- Adding a pure-Kotlin fallback regex generator for unsupported targets.
- Bumping kotlin-rgxgen beyond 0.0.1 (no later release exists yet).
- Refactoring `kotest-property`'s convention-plugin layout.
- Touching the existing `Arb.string` family — `stringPattern` is the only function
  affected.
