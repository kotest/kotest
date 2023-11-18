---
id: coroutine_debugging
title: Coroutine Debugging
slug: coroutine-debugging.html
sidebar_label: Coroutine Debugging
---

[kotlinx-coroutines-debug](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-debug) is a module that provides debugging capabilities for coroutines on the JVM. When enabled, a debug agent
is installed by ByteBuddy and captures information on coroutines as they are created, started, suspended and resumed.

Kotest provides the ability to enable debugging per test. We can do this by enabling `coroutineDebugProbes` in test config.

Once enabled, any coroutines launched inside the test will be included in a "coroutine dump" after the test completes, or as soon
as an exception is thrown.

```kotlin
class CoroutineDebugging : FunSpec() {
   init {
      test("foo").config(coroutineDebugProbes = true) {
         someMethodThatLaunchesACoroutine() // launches a new coroutine
      }
   }
}
```


The coroutine dump will look something like:

```
Coroutines dump 2021/11/27 22:17:43

Coroutine DeferredCoroutine{Active}@71f1906, state: CREATED
	(Coroutine creation stacktrace)
	at kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(IntrinsicsJvm.kt:122)
	at kotlinx.coroutines.intrinsics.CancellableKt.startCoroutineCancellable(Cancellable.kt:30)
	at kotlinx.coroutines.BuildersKt__Builders_commonKt.async$default(Builders.common.kt:82)
	at kotlinx.coroutines.BuildersKt.async$default(Unknown Source)
	at com.sksamuel.kotest.engine.coroutines.Wibble$1.invokeSuspend(CoroutineDebugTest.kt:37)
	at com.sksamuel.kotest.engine.coroutines.Wibble$1.invoke(CoroutineDebugTest.kt)
```


### Spec level config


Coroutine debugging can be enabled for all tests in a spec by overriding the `coroutineDebugProbes` setting
inside a spec:


```kotlin
class CoroutineDebugging : FunSpec() {
  init {

    coroutineDebugProbes = true

    test("foo") {
      // debugging enabled here
    }

    test("bar") {
      // debugging enabled here
    }

  }
}
```



### Project wide config


Coroutine debugging can be enabled for all tests in a project by using [ProjectConfig](../project_config.md):


```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val coroutineDebugProbes = true
}
```
