---
id: quickstart
title: Quick Start
sidebar_label: Quick Start
---

The following instructions give you the batteries included setup in gradle or maven. Omit any modules you don't wish to use.

:::note
Kotest is a [multi-platform project](https://kotlinlang.org/docs/reference/multiplatform.html).
If you are unfamilar with this, then Kotlin compiles to different targets - JVM, JS, Native, iOS and so on. If you are doing server side or android development then you want the modules that end with JVM, such as `kotest-property-jvm`.
:::

#### Gradle

To use in gradle, configure your build to use the [JUnit Platform](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle). For Gradle 4.6 and higher this is
 as simple as adding `useJUnitPlatform()` inside the tasks with type `Test` and then adding the Kotest dependency.

<details open>
<summary>Groovy (build.gradle)</summary>

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotest:kotest-runner-junit5:<version>' // for kotest framework
  testImplementation 'io.kotest:kotest-assertions-core:<version>' // for kotest core jvm assertions
  testImplementation 'io.kotest:kotest-property:<version>' // for kotest property test
}
```

</details>


<details open>
<summary>Android Project (Groovy)</summary>

```groovy
android.testOptions {
    unitTests.all {
        useJUnitPlatform()
    }
}

dependencies {
    testImplementation 'io.kotest:kotest-runner-junit5:<version>' // for kotest framework
    testImplementation 'io.kotest:kotest-assertions-core:<version>' // for kotest core jvm assertions
    testImplementation 'io.kotest:kotest-property:<version>' // for kotest property test
}
```

</details>

If you are using Gradle+Kotlin, this works for both Android and non-Android projects:

<details open>
<summary>Kotlin (build.gradle.kts)</summary>

```kotlin
tasks.withType<Test> {
  useJUnitPlatform()
}

dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:<version>") // for kotest framework
  testImplementation("io.kotest:kotest-assertions-core:<version>") // for kotest core jvm assertions
  testImplementation("io.kotest:kotest-property:<version>") // for kotest property test
}
```

</details>


#### Maven

For maven you must configure the surefire plugin for junit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
</plugin>
```

And then add the Kotest JUnit5 runner to your build to use the framework product.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-runner-junit5-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

For using kotest core jvm assertions add the following configuration.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-assertions-core-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

And for using kotest property testing add the following configuration.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-property-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

#### Snapshots

Snapshot are automatically published on each commit to master.
If you want to test the latest snapshot build, setup the same way described above, change the version to the current snapshot version and add the following repository to your `repositories` block:

```kotlin
repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}
```

