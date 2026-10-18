# Kotest Setup Reference

Platform-specific dependency setup for Kotest 6.x. For project config, the `kotest {}` Gradle extension, tags and
extensions see [FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md).

---

## Version Requirements

| Component | Minimum | Notes                                                                       |
|-----------|---------|-----------------------------------------------------------------------------|
| Kotlin    | 2.2     | Kotest 6.x is compiled with language version 2.2                           |
| JDK       | 11      |                                                                             |
| KSP       | —       | Required for JS / WasmJS / WasmWASI / Native. KSP 2.3+ is versioned independently of Kotlin; use a current KSP2 release |

---

## JVM

```kotlin
// build.gradle.kts
plugins {
   kotlin("jvm") version "<kotlin-version>"
   id("io.kotest") version "<kotest-version>" // optional: enhanced IntelliJ support, power-assert, kotest {} options
}

dependencies {
   testImplementation("io.kotest:kotest-runner-junit5:<kotest-version>")
   testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
   testImplementation("io.kotest:kotest-property:<kotest-version>") // optional
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
}
```

Groovy DSL is identical apart from syntax (`id 'io.kotest' version '...'`, `tasks.withType(Test).configureEach { useJUnitPlatform() }`).

**Maven**: use the `-jvm` artifacts (`kotest-runner-junit5-jvm`, `kotest-assertions-core-jvm`, `kotest-property-jvm`)
with `<scope>test</scope>`; Surefire runs them via JUnit Platform.

---

## Kotlin Multiplatform (JVM + JS + Wasm + Native)

The standalone `kotlin("js")` plugin is deprecated and there is no `kotlin("wasm-js")` plugin — always use
`kotlin("multiplatform")` and declare targets inside it.

```kotlin
plugins {
   kotlin("multiplatform") version "<kotlin-version>"
   id("com.google.devtools.ksp") version "<ksp-version>" // apply KSP before Kotest
   id("io.kotest") version "<kotest-version>"
}

kotlin {
   jvm()
   js { browser(); nodejs() }
   wasmJs { browser() }
   linuxX64()
   macosArm64()
   iosSimulatorArm64()

   sourceSets {
      commonTest {
         dependencies {
            // kotest.kotestVersion (Gradle plugin 6.2.6+) avoids repeating the version
            implementation("io.kotest:kotest-framework-engine:${kotest.kotestVersion.get()}")
            implementation("io.kotest:kotest-assertions-core:${kotest.kotestVersion.get()}")
            implementation("io.kotest:kotest-property:${kotest.kotestVersion.get()}")
         }
      }
      // the JVM target still runs through JUnit Platform
      jvmTest {
         dependencies {
            implementation("io.kotest:kotest-runner-junit5:${kotest.kotestVersion.get()}")
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
}
```

| Target   | Test task                                    |
|----------|----------------------------------------------|
| JVM      | `jvmTest`                                    |
| JS       | `jsTest` (or `jsNodeTest` / `jsBrowserTest`) |
| WasmJS   | `wasmJsTest`                                 |
| Native   | `<target>Test`, e.g. `linuxX64Test`, `macosArm64Test` |
| All      | `check` / `allTests`                         |

A JS-only, Wasm-only or Native-only project is the same build with just that target and no `jvmTest` block.

---

## Android

### Unit (host) tests — `src/test`

```kotlin
android.testOptions {
   unitTests.all { it.useJUnitPlatform() }
}

dependencies {
   testImplementation("io.kotest:kotest-runner-junit5:<kotest-version>")
   testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
}
```

### Instrumented (device) tests — `src/androidTest`

```kotlin
dependencies {
   androidTestImplementation("io.kotest:kotest-runner-junit4:<kotest-version>")
   androidTestImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
}
```

Every instrumented spec must be annotated:

```kotlin
import io.kotest.core.spec.style.FreeSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class MyDeviceTest : FreeSpec({
   "runs on device" { }
})
```

---

## Version Catalog (libs.versions.toml)

```toml
[versions]
kotest = "<kotest-version>"
ksp = "<ksp-version>"

[libraries]
kotest-runner-junit5 = { module = "io.kotest:kotest-runner-junit5", version.ref = "kotest" }
kotest-runner-junit4 = { module = "io.kotest:kotest-runner-junit4", version.ref = "kotest" }
kotest-framework-engine = { module = "io.kotest:kotest-framework-engine", version.ref = "kotest" }
kotest-assertions-core = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
kotest-property = { module = "io.kotest:kotest-property", version.ref = "kotest" }

[plugins]
kotest = { id = "io.kotest", version.ref = "kotest" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

Use `alias(libs.plugins.kotest)` / `alias(libs.plugins.ksp)` in `plugins {}`. Keep every Kotest artifact on the same version.

---

## Modules

Assertion modules are listed in [ASSERTIONS-REFERENCE.md](ASSERTIONS-REFERENCE.md#modules); property modules in
[PROPERTY-TESTING-REFERENCE.md](PROPERTY-TESTING-REFERENCE.md). Extension modules (all JVM, group `io.kotest`):

| Extension      | Artifact                                                  |
|----------------|-----------------------------------------------------------|
| Spring         | `kotest-extensions-spring`                                |
| Testcontainers | `kotest-extensions-testcontainers`                        |
| Koin           | `kotest-extensions-koin`                                  |
| Allure         | `kotest-extensions-allure`                                |
| WireMock       | `kotest-extensions-wiremock`                              |
| MockServer     | `kotest-extensions-mockserver`                            |
| Blockhound     | `kotest-extensions-blockhound`                            |
| HTML reporter  | `kotest-extensions-htmlreporter`                          |
| JUnit XML      | `kotest-extensions-junitxml` (deprecated 6.2 — use Gradle's built-in XML in `build/test-results/`) |
| Others         | `kotest-extensions-pitest`, `-decoroutinator`, `-now`     |

Usage: [FRAMEWORK-REFERENCE.md#extensions](FRAMEWORK-REFERENCE.md#extensions).

---

## Platform Feature Matrix

| Feature                                  | JVM | JS / WasmJS | Native | WasmWASI |
|------------------------------------------|-----|-------------|--------|----------|
| All spec styles, assertions, data tests  | ✅   | ✅           | ✅      | ✅        |
| Property testing                         | ✅   | ✅           | ✅      | ✅        |
| ProjectConfig discovery                  | fixed FQN / sysprop / common package | anywhere (KSP) | anywhere (KSP), must be a `class` | anywhere (KSP) |
| Annotation-based config (`@Tags`, `@EnabledIf`, `@ApplyExtension` …) | ✅ | ❌ | ❌ | ❌ |
| Container data tests without a leaf      | ✅   | ❌           | ❌      | ❌        |
| `blockingTest`, coroutine debug probes   | ✅   | ❌           | ❌      | ❌        |
| Third-party extension modules            | ✅   | ❌           | ❌      | ❌        |
