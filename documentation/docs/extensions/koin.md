---
id: koin
title: Koin
sidebar_label: Koin
slug: koin.html
---

## Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with Kotest through the `KoinExtension` extension.

To use the extension in your project, add the dependency to your project:

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-koin)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-koin/)

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::

```kotlin
io.kotest:kotest-extensions-koin:${kotestVersion}
```

With the dependency added, we can easily use Koin in our tests!

```kotlin
class KotestAndKoin : KoinTest, FunSpec() {
  init {
    extension(KoinExtension(koinModule) { mockk<UserService>() })

    test("use userService") {
      val userService by inject<UserService>()

      userService.getUser().username shouldBe "LeoColman"
    }
  }
}
```

By default, the extension will start/stop the Koin context between leaf tests.
If you are using a nested spec style (like DescribeSpec) and instead want the Koin context
to persist over all leafs of a root tests (for example to share mocked declarations between tests),
you can specify the lifecycle mode as `KoinLifecycleMode.Root` in the KoinExtension constructor.

```kotlin
class KotestAndKoin : KoinTest, DescribeSpec() {

  init {
    extension(KoinExtension(module = myKoinModule, mode = KoinLifecycleMode.Root))

    describe("use userService") {
      val userService by inject<UserService>()

      it("inside a leaf test") {
        userService.getUser().username shouldBe "LeoColman"
      }

      it("this shares the same context") {
        userService.getUser().username shouldBe "LeoColman"
      }
    }
  }
}
```
