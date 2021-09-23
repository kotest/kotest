---
title: Test Engine Lifecycle and Extensions
slug: lifecycle.html
---

Kotest has an extension selection of hooks through which you can integrate into the test engine lifecycle.

| Lifecycle |
| ----- |
| A spec is selected for execution by the _TestSuiteScheduler_ and a coroutine is created for the spec. |
| Any `SpecLaunchedExtension`s are invoked, passing in the reference to the spec that has been scheduled. The extensions may opt to skip execution or continue. Any changes to the coroutine context are propagated downstream. |
| If either the spec is annotated with `@Ignored` or the spec is annotated with `@EnabledIf` and fails the if condition, then then spec will be skipped. Any `SpecIgnoredExtension`s are invoked with the reference to the spec. |
| If the spec is not skipped, an instance of the spec is created. On the JVM this process will delegate to any `ConstructorExtension`s that are registered. If none exist, then the default instantiation method is used. |
| On the JVM, the instantiated spec will be passed to any `PostInstantiationExtension`s which have the ability to adjust the instance (for example, applying dependency injection).
|

  * If ref is enabled:
    * spec = create instance of ref
    * If spec creation fails:
      * TestEngineListener.specAborted(ref)
    * SpecInterceptExtension.intercept(spec)
    * If spec is inactive (no enabled root tests):
      * TestEngineListener.specInactive(kclass)
      * InactiveSpecListener.inactive(spec, results)
    * If spec is active (has enabled root tests):
      * TestEngineListener.specStarted(kclass)
      * StartSpecListener.specStarted(kclass)
      * For each isolated spec: create new instance
        * BeforeSpecListener.beforeSpec(spec)
        * Execute tests
        * AfterSpecListener.afterSpec(spec)
      * FinishSpecListener.finishSpec(kclass)
      * TestEngineListener.specFinished(kclass, results)
* TestEngineListener.specExit(kclass, throwable)

### Spec Instantiation Lifecycle

* spec = create instance of ref
* If spec was created via reflection successfully:
  * spec = PostInstantiationExtension.process(spec)
  * SpecInstantiationListener.specInstantiated(spec)
* If spec reflective instantiation failed:
  * SpecInstantiationListener.specInstantiationError(KClass, Throwable)

### Test Lifecycle
