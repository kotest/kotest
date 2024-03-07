---
title: Spec Lifecycle and Extensions
slug: spec-lifecycle.html
---

Kotest has an extensive selection of hooks through which you can integrate into the spec lifecycle.

| Lifecycle |
| ----- |
| A spec is selected for execution by the _TestSuiteScheduler_ and a coroutine is created for the spec. |
| Any [SpecLaunchExtensions](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/SpecLaunchExtension.kt) are invoked, passing in the reference to the spec that has been scheduled. The extensions may opt to skip execution or continue. Any changes to the coroutine context are propagated downstream. |
| If either the spec is annotated with `@Ignored` or the spec is annotated with `@EnabledIf` and fails the if condition, then the spec will be skipped. If skipped, any [SpecIgnoredListener](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/SpecIgnoredListener.kt) are invoked with the reference to the spec. |
| If the spec is not skipped, an instance of the spec is created. On the JVM this process will delegate to any [ConstructorExtensions](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/jvmMain/kotlin/io/kotest/core/extensions/ConstructorExtension.kt) that are registered, or if none exist, then the default instantiation method is used (reflection). On other platforms the spec will be created directly. |
| On the JVM, the instantiated spec will be passed to any [PostInstantiationExtensions](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/PostInstantiationExtension.kt) which have the ability to adjust the instance (for example, applying dependency injection). |
| If spec creation is successful, then any [SpecInitializeExtensions](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/SpecInitializeExtension.kt) are invoked and if spec creation fails, then any [SpecCreationErrorListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/SpecCreationErrorListener.kt) are invoked with the exception. |
| Any [SpecInterceptExtensions](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/SpecInterceptExtension.kt) are invoked passing in the created spec. These extensions may opt to skip execution or continue. Any changes to the coroutine context are propagated downstream. |
| If the spec is active (contains at least one, enabled, root test case), then any [PrepareSpecListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/listeners/PrepareSpecListener.kt) are invoked. Otherwise, the spec is inactive, and any [InactiveSpecListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/extensions/InactiveSpecListener.kt) are invoked and execution is skipped. |
| Any [BeforeSpecListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/listeners/BeforeSpecListener.kt) are invoked passing in the spec instance. Any errors in these extensions will cause test execution and after spec listeners to be skipped. |
| All tests in the spec are executed. |
| Any [AfterSpecListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/listeners/AfterSpecListener.kt) are invoked passing in the spec instance. |
| Any [FinalizeSpecListeners](https://github.com/kotest/kotest/blob/master/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/listeners/FinalizeSpecListener.kt) are invoked passing in the `KClass` reference to the spec that was completed. |

Note: For each isolated spec, a fresh spec instance will be created and the `PostInstantiationExtension`, `SpecCreatedListener`, `SpecCreationErrorListener`, `SpecInterceptExtension`, `BeforeSpecListener` and `AfterSpecListener` callbacks will be repeated.
