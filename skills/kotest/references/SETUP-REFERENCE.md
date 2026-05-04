e# Kotest Setup Reference

Platform-specific dependency and configuration setup for Kotest 6.x.

---

## Version Requirements

| Component | Minimum        | Recommended | Notes                                     |
|-----------|----------------|-------------|-------------------------------------------|
| Kotlin    | 2.2            | 2.2+        | Required for Kotest 6.x                   |
| JDK       | 11             | 17+         | JDK 11 is minimum for Kotest 6.x          |
| Gradle    | 8.0            | 8.5+        | For Kotlin DSL and JUnit Platform support |
| KSP       | Matches Kotlin | —           | Required for JS/Native/WasmJS targets     |

---

## JVM Setup

### Gradle (Kotlin DSL)

```kotlin
// build.gradle.kts
plugins {
  kotlin("jvm") version "<kotlin-version>"
  id("io.kotest") version "<kotest-version>"  // optional, enhances IDE integration
}

dependencies {
  // Test framework (required)
  testImplementation("io.kotest:kotest-runner-junit5:<kotest-version>")

  // Assertions (recommended)
  testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")

  // Property testing (optional)
  testImplementation("io.kotest:kotest-property:<kotest-version>")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
```

### Gradle (Groovy DSL)

```groovy
// build.gradle
plugins {
  id 'org.jetbrains.kotlin.jvm' version '<kotlin-version>'
  id 'io.kotest' version '<kotest-version>'
}

dependencies {
  testImplementation 'io.kotest:kotest-runner-junit5:<kotest-version>'
  testImplementation 'io.kotest:kotest-assertions-core:<kotest-version>'
  testImplementation 'io.kotest:kotest-property:<kotest-version>'
}

tasks.withType(Test).configureEach {
  useJUnitPlatform()
}
```

### Maven

```xml

<dependencies>
  <dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-runner-junit5-jvm</artifactId>
    <version>${kotest.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-assertions-core-jvm</artifactId>
    <version>${kotest.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-property-jvm</artifactId>
    <version>${kotest.version}</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

---

## Kotlin/JS Setup

```kotlin
plugins {
  kotlin("js") version "<kotlin-version>"
  id("com.google.devtools.ksp") version "<ksp-version>"
  id("io.kotest") version "<kotest-version>"
}

kotlin {
  js {
    browser()  // or nodejs()
  }
  sourceSets {
    val jsTest by getting {
      dependencies {
        implementation("io.kotest:kotest-framework-engine:<kotest-version>")
        implementation("io.kotest:kotest-assertions-core:<kotest-version>")
      }
    }
  }
}
```

**Run**: `./gradlew jsTest`

---

## Kotlin/WasmJS Setup

```kotlin
plugins {
  kotlin("wasm-js") version "<kotlin-version>"
  id("com.google.devtools.ksp") version "<ksp-version>"
  id("io.kotest") version "<kotest-version>"
}

kotlin {
  wasmJs {
    browser()
  }
  sourceSets {
    val wasmJsTest by getting {
      dependencies {
        implementation("io.kotest:kotest-framework-engine:<kotest-version>")
        implementation("io.kotest:kotest-assertions-core:<kotest-version>")
      }
    }
  }
}
```

**Run**: `./gradlew wasmJsTest`

---

## Kotlin/Native Setup

```kotlin
plugins {
  kotlin("multiplatform") version "<kotlin-version>"
  id("com.google.devtools.ksp") version "<ksp-version>"
  id("io.kotest") version "<kotest-version>"
}

kotlin {
  linuxX64()        // or any supported native target
  macosX64()
  macosArm64()
  mingwX64()

  sourceSets {
    val commonTest by getting {
      dependencies {
        implementation("io.kotest:kotest-framework-engine:<kotest-version>")
        implementation("io.kotest:kotest-assertions-core:<kotest-version>")
      }
    }
  }
}
```

**Run**: `./gradlew linuxX64Test` (or the appropriate target test task)

---

## Kotlin Multiplatform (Full)

```kotlin
plugins {
  kotlin("multiplatform") version "<kotlin-version>"
  id("com.google.devtools.ksp") version "<ksp-version>"
  id("io.kotest") version "<kotest-version>"
}

kotlin {
  jvm()
  js { browser(); nodejs() }
  wasmJs { browser() }
  linuxX64()
  macosArm64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    commonTest {
      dependencies {
        implementation("io.kotest:kotest-framework-engine:<kotest-version>")
        implementation("io.kotest:kotest-assertions-core:<kotest-version>")
        implementation("io.kotest:kotest-property:<kotest-version>")
      }
    }
    // JVM gets the JUnit5 runner additionally
    jvmTest {
      dependencies {
        implementation("io.kotest:kotest-runner-junit5:<kotest-version>")
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
```

---

## Android Setup

### Unit Tests (JVM-based, `test/`)

```kotlin
dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:<kotest-version>")
  testImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
```

### Instrumented Tests (`androidTest/`)

```kotlin
dependencies {
  androidTestImplementation("io.kotest:kotest-runner-junit4:<kotest-version>")
  androidTestImplementation("io.kotest:kotest-assertions-core:<kotest-version>")
}
```

Use `JUnit4` runner for instrumented tests since Android's test infrastructure is built on JUnit 4.

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
kotest-assertions-json = { module = "io.kotest:kotest-assertions-json", version.ref = "kotest" }
kotest-property = { module = "io.kotest:kotest-property", version.ref = "kotest" }

[plugins]
kotest = { id = "io.kotest", version.ref = "kotest" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## Assertion Extension Modules

| Module             | Artifact                             | Platform |
|--------------------|--------------------------------------|----------|
| Core matchers      | `kotest-assertions-core`             | All      |
| JSON matchers      | `kotest-assertions-json`             | All      |
| Ktor matchers      | `kotest-assertions-ktor`             | JVM      |
| Arrow matchers     | `kotest-assertions-arrow`            | All      |
| kotlinx-datetime   | `kotest-assertions-kotlinx-datetime` | All      |
| Konform validation | `kotest-assertions-konform`          | All      |
| Compiler testing   | `kotest-assertions-compiler`         | JVM      |

---

## Extension Modules

| Extension      | Artifact                           | Description                     |
|----------------|------------------------------------|---------------------------------|
| Spring         | `kotest-extensions-spring`         | Spring test context integration |
| Testcontainers | `kotest-extensions-testcontainers` | Testcontainers lifecycle        |
| Koin           | `kotest-extensions-koin`           | Koin DI integration             |
| Allure         | `kotest-extensions-allure`         | Allure reporting                |
| WireMock       | `kotest-extensions-wiremock`       | WireMock server integration     |
| MockServer     | `kotest-extensions-mockserver`     | MockServer integration          |
| Blockhound     | `kotest-extensions-blockhound`     | Blocking call detection         |
| JUnit XML      | `kotest-extensions-junitxml`       | JUnit XML report output         |
| HTML Reporter  | `kotest-extensions-htmlreporter`   | HTML test reports               |

---

## Platform Feature Matrix

| Feature                      | JVM             | JS | WasmJS | Native |
|------------------------------|-----------------|----|--------|--------|
| All spec styles              | ✅               | ✅  | ✅      | ✅      |
| All assertions               | ✅               | ✅  | ✅      | ✅      |
| Property testing             | ✅               | ✅  | ✅      | ✅      |
| Data-driven testing          | ✅               | ✅  | ✅      | ✅      |
| ProjectConfig auto-discovery | ✅               | ❌  | ❌      | ❌      |
| Annotation-based config      | ✅               | ❌  | ❌      | ❌      |
| Classpath scanning           | ❌ (removed 6.0) | ❌  | ❌      | ❌      |
| Coroutine debug probes       | ✅               | ❌  | ❌      | ❌      |
| Blocking test mode           | ✅               | ❌  | ❌      | ❌      |
| Extensions (Spring, etc.)    | ✅               | ❌  | ❌      | ❌      |

