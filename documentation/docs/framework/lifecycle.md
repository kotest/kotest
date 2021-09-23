---
title: Test Engine Lifecycle and Extensions
slug: lifecycle.html
---

Kotest has an extension selection of hooks through which you can integrate into the test engine lifecycle.

| Lifecycle |
| ----- |
| A spec is selected for execution by the _TestSuiteScheduler_ and a coroutine is created for the spec. |
| Any `SpecLaunchedExtension`s are invoked, passing in the reference to the spec that has been scheduled. The extensions may opt to skip execution or continue. Any changes to the coroutine context are propagated downstream. |
| If either the spec is annotated with `@Ignored` or the spec is annotated with `@EnabledIf` and fails the if condition, then the spec will be skipped. If skipped, any `SpecIgnoredListener`s are invoked with the reference to the spec. |
| If the spec is not skipped, an instance of the spec is created. On the JVM this process will delegate to any `ConstructorExtension`s that are registered, or if none exist, then the default instantiation method is used (reflection). On other platforms the spec will be created directly. |
| On the JVM, the instantiated spec will be passed to any `PostInstantiationExtension`s which have the ability to adjust the instance (for example, applying dependency injection).
| If spec creation is successful, then any `SpecCreatedListener`s are invoked and if spec creation fails, then any `SpecCreationErrorListener`s are invoked with the exception.
| Any `SpecInterceptExtension`s are invoked passing in the created spec. These extensions may opt to skip execution or continue. Any changes to the coroutine context are propagated downstream.
| If the spec is active (contains at least one, enabled, root test case), then any `PrepareSpecListener`s are invoked. Otherwise, the spec is inactive, and any `InactiveSpecListener`s are invoked and execution is skipped.
| Any `BeforeSpecListener`s are invoked. Any errors in these extensions will cause test execution and after spec listeners to be skipped.
| All tests in the spec are executed.
| Any `AfterSpecListener`s are invoked.
| Any `FinalizeSpecListeners`s are invoked.

Note: For each isolated spec, a fresh spec instance will be created and the `PostInstantiationExtension`, `SpecCreatedListener`, `SpecCreationErrorListener`, `SpecInterceptExtension`, `BeforeSpecListener` and `AfterSpecListener` callbacks will be repeated.
