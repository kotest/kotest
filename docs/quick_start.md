Quick Start
===========



Kotest is split into 3 basic sub-projects.
These are provided separately so you can pick and choose which parts to use if you don't want to go _all in_ on Kotest.


* **Test Framework**<br/>Layout tests in a fluid way and execute them on the JVM or Javascript.<br/><img src="https://img.shields.io/maven-central/v/io.kotest/kotest-framework-engine.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/)
* **Assertions Library**<br/>A Kotlin-first multi-platform assertions library.<br/><img src="https://img.shields.io/maven-central/v/io.kotest/kotest-assertions-core.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/)
* **Property Testing**<br/>An advanced multi-platform property test library with shrinking support.<br/><img src="https://img.shields.io/maven-central/v/io.kotest/kotest-property.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/)

#### Which subproject(s) to use?

* If you want to lay out tests in a [fluid way](styles.md); with built in coroutine support at every level; the ability to use [functions as test lifecycle callbacks](listeners.md#dsl-methods-with-functions); with extensive extension points; with advanced [conditional evaluation](conditional_evaluation.md); and execute these tests on the JVM and/or Javascript, then build your test classes using the Kotest Test Framework.

* If you want a Kotlin focused multi-platform enabled assertions library; with over [300 rich assertions](matchers.md); with support for [inspectors](inspectors.md); helpers for [non-determistic tests](nondeterministic_testing.md); powerful [data driven testing](data_driven_testing.md); modules for [arrow](matchers/arrow.md), json and more, then opt to use the Kotest assertions library.

* If you want a powerful multi-platform enabled [property test](property_testing.md) library, with over 50 [built in generators](generators.md); the ability to easily compose new generators; with failure shrinking; with exhaustive checks; with coverage metrics; then choose the Kotest property test module.


The following instructions give you the batteries included setup in gradle or maven. Omit any modules you don't wish to use.

_Note: Kotest is a [multi-platform project](https://kotlinlang.org/docs/reference/multiplatform.html).
If you are unfamilar with this, then Kotlin compiles to different targets - JVM, JS, Native, iOS and so on. If you are doing server side or android development then you want the modules that end with -JVM, such as `kotest-property-jvm`_


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

