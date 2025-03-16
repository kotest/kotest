---
id: blockhound
title: BlockHound
sidebar_label: BlockHound
slug: blockhound.html
---

The Kotest BlockHound extension activates [BlockHound](https://github.com/reactor/BlockHound) support for coroutines. It helps to detect blocking code on non-blocking coroutine threads, e.g. when accidentally calling a blocking I/O library function on a UI thread.

:::note
To use this extension add the `io.kotest.extensions:kotest-extensions-blockhound` module to your test compile path.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-blockhound.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-blockhound)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-blockhound.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-blockhound/)

### Getting Started

Register the `BlockHound` extension in your test class:

```kotlin
@DoNotParallelize
class BlockHoundSpecTest : FunSpec({
   extension(BlockHound())

   test("detects for spec") {
      blockInNonBlockingContext()
   }
})
```

The `BlockHound` extension can also be registered per [test case](../framework/testcaseconfig.html) or at the [project level](../framework/project-config.html).

:::caution
This code is sensitive to concurrency. There can only be one instance of this extension running at a time as it will take effect globally.

You cannot register the `BlockHound` extension multiple times at different levels.

Use `@DoNotParallelize` for `BlockHound`-enabled tests.
:::

### Detection

Blocking calls will be detected in coroutine threads which are expected not to block. Such threads are created by the default dispatcher as this example demonstrates:

```kotlin
private suspend fun blockInNonBlockingContext() {
   withContext(Dispatchers.Default) {
      @Suppress("BlockingMethodInNonBlockingContext")
      Thread.sleep(2)
   }
}
```

The BlockHound extension will by default produce an exception like this whenever it detects a blocking call:
```
reactor.blockhound.BlockingOperationError: Blocking call! java.lang.Thread.sleep
	at io.kotest.extensions.blockhound.KotestBlockHoundIntegration.applyTo$lambda-2$lambda-1(KotestBlockHoundIntegration.kt:27)
	at reactor.blockhound.BlockHound$Builder.lambda$install$8(BlockHound.java:427)
	at reactor.blockhound.BlockHoundRuntime.checkBlocking(BlockHoundRuntime.java:89)
	at java.base/java.lang.Thread.sleep(Thread.java)
	at io.kotest.extensions.blockhound.BlockHoundTestKt$blockInNonBlockingContext$2.invokeSuspend(BlockHoundTest.kt:17)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:677)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:664)
```

:::note
By invoking it as `BlockHound(BlockHoundMode.PRINT)`, it will print detected calls and continue the test without interruption.
:::

Whenever a blocking call is detected, you can
* replace the call with a non-blocking one (using a coroutine-aware library), or
* schedule the calling coroutine to run on a separate I/O thread (e.g. via `Dispatchers.IO`), or
* add an exception if the blocking is harmless (see below).

### Customization

To customize BlockHound, familiarize yourself with the [BlockHound documentation](https://github.com/reactor/BlockHound/blob/master/docs/README.md).

Exceptions for blocking calls considered harmless can be added via a separate `BlockHoundIntegration` class like this:

```kotlin
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.BlockHoundIntegration

class MyBlockHoundIntegration : BlockHoundIntegration {
   override fun applyTo(builder: BlockHound.Builder): Unit = with(builder) {
      allowBlockingCallsInside("org.slf4j.LoggerFactory", "performInitialization")
   }
}
```

In order to allow `BlockHound` to auto-detect and load the integration, add its fully qualified class name to a service provider configuration file
 `resources/META-INF/services/reactor.blockhound.integration.BlockHoundIntegration`.
