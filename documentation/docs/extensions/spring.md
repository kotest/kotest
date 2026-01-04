---
id: spring
title: Spring
sidebar_label: Spring
slug: spring.html
---

Kotest offers a Spring extension that allows you to test code that uses the Spring framework for dependency injection.

## Getting Started

:::tip
If you prefer to see an example rather than read docs, then there is a sample project using spring
webflux [here](https://github.com/kotest/kotest-examples/tree/master/kotest-spring-webflux)
:::

In order to use this extension, you need to add `io.kotest:kotest-extensions-spring` module to your test compile path.
The latest version can always be found on maven
central [here](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-spring).

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-spring.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-spring)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-spring%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-spring/maven-metadata.xml)

The Spring extension requires you to activate it for all spec classes, or per spec class. To activate it globally,
you can register the `SpringExtension` in [project config](../framework/project_config.md):

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val extensions = listOf(SpringExtension())
}
```

Alternatively, you can register the extension per spec class via the `@ApplyExtension` annotation.

```kotlin
@ApplyExtension(SpringExtension::class)
class MyTestSpec : FunSpec() {}
```

In order to let Spring know which configuration class to use, you must annotate your Spec classes with
`@ContextConfiguration`. This should point to a class annotated with the Spring `@Configuration` annotation.
Alternatively, you can use [
`@ActiveProfiles`](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-activeprofiles.html)
to point to
a [specific application context file](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html).

:::note
In Kotest 4.3 and earlier, the Spring extension was called `SpringListener`. This extension has now been deprecated in
favour of `SpringExtension`. The usage is the same, but the SpringExtension has more functionality.
:::

## Constructor Injection

When the Spring extension is active, Kotest will automatically take care of instantating your test classes using the
primary constructor. This inclues autowiring constructor parameters.

The following example is a test class which requires a service called `UserService` in its primary constructor. This
`UserService` class is just a regular spring bean which has been annotated with @Component.

```kotlin
@ContextConfiguration(classes = [(Components::class)])
class SpringAutowiredConstructorTest(service: UserService) : WordSpec() {
  init {
    "SpringExtension" should {
      "have autowired the service" {
        service.repository.findUser().name shouldBe "system_user"
      }
    }
  }
}
```

## Test Contexts

The Spring extensions makes available the `TestContextManager` via the coroutine context that tests execute in. You can
gain a handle to this instance through the `testContextManager()` extension method.

From this you can get the `testContext` that Spring is using.

```kotlin
class MySpec(service: UserService) : WordSpec() {
  init {
    "SpringExtension" should {
      "provide the test context manager" {
        println("The context is " + testContextManager().testContext)
      }
    }
  }
}
```

## Test Execution Events

Spring has
various [Test Execution Events](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/test-execution-events.html)
such as `BeforeTestMethod` and `BeforeTestExecution`. These events make an assumption that tests are methods, so they do
not map exactly one to one to tests defined in frameworks like Kotest where tests are functions and can be nested
arbitrarily.

You can customize when these callbacks are fired by using the `SpringTestLifecycleMode` enum when creating the
extension. By default, this is on leaf tests. You can set these to fire on root tests by passing a
`SpringTestLifecycleMode.Root`
argument to the extension:

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val extensions = listOf(SpringExtension(SpringTestLifecycleMode.Root))
}
```

:::tip
If you want to use root mode with @ApplyExtension, you must use the `SpringRootTestExtension` subclass, eg
`@ApplyExtension(SpringRootTestExtension::class)`.
::

### Order of Events

Spring defines a precedence order for when callbacks are fired.
There are four events that can fire related to tests. These are

* `BeforeTestMethodEvent`
* `BeforeTestExecutionEvent`
* `AfterTestExecutionEvent`
* `AfterTestMethodEvent`

Spring requests that the `beforeTestMethod` and `afterTestMethod` events be fired before other user callbacks, and that
the `beforeTestExecution` and `afterTestExecution` events be fired after other user callbacks. It is not possible in
Kotest to define the order of callbacks, but we try to follow the request as closely as possible.

Kotest executes these callbacks in the following groups, where the order between groups is guaranteed, but
the order inside any group is not guaranteed:

Group 1: Spring's `BeforeTestMethodEvent` and any other `TestCaseExtension`s.
Group 2: Spring's `BeforeTestExecutionEvent` and any other `BeforeTest`, `BeforeAny`, and `BeforeEach` callbacks.
Group 3: Spring's `AfterTestExecutionEvent` and any other `AfterTest`, `AfterAny`, and `AfterEach` callbacks.
Group 4: Spring's `AfterTestMethodEvent` and any other `TestCaseExtension`s.

## Final Classes

When using a final class, you may receive a warning from Kotest:

`Using SpringExtension on a final class. If any Spring annotation fails to work, try making this class open`

If you wish, you can disable this warning by setting the system property `kotest.listener.spring.ignore.warning` to
true.

