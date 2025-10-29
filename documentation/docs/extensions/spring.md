---
id: spring
title: Spring
sidebar_label: Spring
slug: spring.html
---

Kotest offers a Spring extension that allows you to test code that uses the Spring framework for dependency injection.

:::tip
If you prefer to see an example rather than read docs, then there is a sample project using spring webflux [here](https://github.com/kotest/kotest-examples/tree/master/kotest-spring-webflux)
:::

In order to use this extension, you need to add `io.kotest:kotest-extensions-spring` module to your test compile path. The latest version can always be found on maven central [here](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-spring).

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::


[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-spring.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-spring)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-spring%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-spring/maven-metadata.xml)


The Spring extension requires you to activate it for all test classes, or per test class. To activate it globally,
register the `SpringExtension` in [project config](../framework/project_config.md):

```kotlin
package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class ProjectConfig : AbstractProjectConfig() {
   override val extensions = listOf(SpringExtension())
}
```

To activate it per test class:

```kotlin
import io.kotest.core.extensions.ApplyExtension
import io.kotest.extensions.spring.SpringExtension

@ApplyExtension(SpringExtension::class)
class MyTestSpec : FunSpec() {}
```

In order to let Spring know which configuration class to use, you must annotate your Spec classes with `@ContextConfiguration`.
This should point to a class annotated with the Spring `@Configuration` annotation. Alternatively, you can use
[`@ActiveProfiles`](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-activeprofiles.html) to
point to a [specific application context file](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html).

:::note
In Kotest 4.3 and earlier, the Spring extension was called `SpringListener`. This extension has now been deprecated in favour of `SpringExtension`. The usage is the same, but the SpringExtension has more functionality.
:::

### Constructor Injection

For constructor injection, Kotest automatically registers a `SpringAutowireConstructorExtension`
when the spring module is added to the build, assuming auto scan is enabled (see [Project Config](../framework/project-config.html)). If Auto scan is
disabled, you will need to manually load the extension in your Project config.

This extension will intercept each call to create a Spec instance
and will autowire the beans declared in the primary constructor.

The following example is a test class which requires a service called `UserService` in its primary constructor. This service
class is just a regular spring bean which has been annotated with @Component.

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


### TestContexts

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


### Test Method Callbacks

Spring has various test callbacks such as `beforeTestMethod` that are based around the idea that tests are methods.
This assumption is fine for legacy test frameworks like JUnit but not applicable to modern test frameworks like Kotest where tests are functions.

Therefore, when using a [spec style](../framework/styles.md) that is nested, you can customize when the test method callbacks are fired.
By default, this is on the leaf node. You can set these to fire on the root nodes by passing a SpringTestLifecycleMode argument to the extension:

```kotlin
class ProjectConfig : AbstractProjectConfig() {
   override val extensions = listOf(SpringExtension(SpringTestLifecycleMode.Root))
}
```



### Final Classes

When using a final class, you may receive a warning from Kotest:

`Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open`

If you wish, you can disable this warning by setting the system property `kotest.listener.spring.ignore.warning` to true.

