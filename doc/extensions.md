KotlinTest Extensions
====================


### Arrow

The arrow extension module provives assertions for the functional programming library [arrow-kt](https://arrow-kt.io/) for types such as `Option`, `Try`, and so on.
 To use this library you need to add `kotlintest-assertions-arrow` to your build.

Here is an example asserting that an `Option` variable is a `Some` with a value `"Foo"`.

```kotlin
val option: Option<String> = ...
option shouldBe beSome("foo")
```

For the full list of arrow matchers [click here](arrow-matchers.md).

Additionally, the module provides inspectors that work specifically for the `NonEmptyList` type.
For example, we can test that a set of assertions hold only for a single element in a Nel by using the `forOne` inspector.

```kotlin
val list = NonEmptyList(2, 4, 6, 7,8)
list.forOne {
  it.shouldBeOdd()
}
```

Other inspectors include `forNone`, `forAll`, `forExactly(n)`, `forSome` and so on. See the section on [inspectors](https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md#inspectors) for more details.

### Spring

KotlinTest offers a Spring extension that allows you to test code that wires dependencies using Spring.
To use this extension add the `kotlintest-extensions-spring` module to your test compile path.

In order to let Spring know which configuration class to use, you must annotate your Spec classes with `@ContextConfiguration`.
This should point to a class annotated with the Spring `@Configuration` annotation. Alternatively, you can use `@ActiveProfile` to
point to a [specific application context file](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html).

There are two ways to enable spring wiring depending on if you want to use constructor injection, or field injection.

#### Field Injection

If you wish to use field injection, then the `SpringListener` must be registered with any
 Spec that uses spring beans. For example:

```kotlin
@ContextConfiguration(classes = [(TestConfiguration::class)])
class SpringExampleSpec : WordSpec() {

  override fun listeners() = listOf(SpringListener)

  @Autowired
  var bean: MyBean? = null

  init {
    "Spring Extension" should {
      "have wired up the bean" {
        bean shouldNotBe null
      }
    }
  }
}
```

You could add the `SpringListener` project wide by registering the listener in [ProjectConfig](reference.md#project-config).

#### Constructor Injection

For constructor injection, we use a different implementation called `SpringAutowireConstructorExtension` which
 must be registered with [ProjectConfig](reference.md#project-config). This extension will intercept each call to create a Spec instance
 and will autowire the beans declared in the primary constructor.

First an example of the project config.

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> = listOf(SpringAutowireConstructorExtension)
}
```

And now an example of a test class which requires a service called `UserService` in its primary constructor. This service
 class is just a regular spring bean which has been annotated with @Component.

```kotlin
@ContextConfiguration(classes = [(Components::class)])
class SpringAutowiredConstructorTest(service: UserService) : WordSpec() {
  init {
    "SpringListener" should {
      "have autowired the service" {
        service.repository.findUser().name shouldBe "system_user"
      }
    }
  }
}
```

### Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with KotlinTest through the `KoinListener` test listener and its own interface `KoinTest`.

To add the listener to your project, add the depency to your project:
```groovy
testImplementation("io.kotlintest:kotlintest-extensions-koin:${kotlinTestVersion}")
```

With the dependency added, we can use Koin in our tests!

```kotlin
class KotlinTestAndKoin : FunSpec(), KoinTest {

    override fun listeners() = listOf(KoinListener(myKoinModule))
    
    val userService by inject<UserService>()
    
    init {
      test("Use user service") {
        userService.getUser().username shouldBe "Kerooker"
      }
    }

}
```


### System Extensions

Sometimes your code might use some functionalities straight from the JVM, which are very hard to simulate. With KotlinTest System Extensions, these difficulties are made easy to mock and simulate, and your code can be tested correctly. After changing the system and using the extensions, the previous state will be restored.

**Attention**: These code is very sensitive to concurrency. Due to the JVM specification there can only be one instance of these extensions running (For example: Only one Environment map must exist). If you try to run more than one instance at a time, the result is unknown.

#### System Environment

With *System Environment Extension* you can simulate how the System Environment is behaving. That is, what you're obtaining from `System.getenv()`.

KotlinTest provides some extension functions that provides a System Environment in a specific scope:

```kotlin
withEnvironment("FooKey", "BarValue") {
    System.getenv("FooKey") shouldBe "BarValue" // System environment overriden!
}
```

You can also use multiple values in this extension, through a map or list of pairs.

```kotlin
withEnvironment(mapOf("FooKey" to "BarValue", "BarKey" to "FooValue")) {
  // Use FooKey and BarKey
}

```

These functions will add the keys and values if they're not currently present in the environment, and will override them if they are. Any keys untouched by the function will remain in the environment, and won't be messed with.

Instead of extensions functions, you can also use the provided Listeners to apply these functionalities in a bigger scope. There's an alternative for the Spec/Per test level, and an alternative for the Project Level.

```kotlin

class MyTest : FreeSpec() {

      override fun listeners() = listOf(SystemEnvironmentTestListener("foo", "bar"))

    init {
      "MyTest" {
        System.getenv("foo") shouldBe "bar"
      }
    }

}

```

```kotlin

class ProjectConfig : AbstractProjectConfig() {

    override fun listeners(): List<TestListener> = listOf(SystemEnvironmentProjectListener("foo", "bar"))

}

```

#### System Property Extension

In the same fashion as the Environment Extensions, you can override the System Properties (`System.getProperties()`):

```kotlin
withSystemProperty("foo", "bar") {
  System.getProperty("foo") shouldBe "bar"
}
```

And with similar Listeners:

```kotlin
    class MyTest : FreeSpec() {
    
          override fun listeners() = listOf(SystemPropertyListener("foo", "bar"))
    
        init {
          "MyTest" {
            System.getProperty("foo") shouldBe "bar"
          }
        }
    
    }
```

#### System Security Manager

Similarly, with System Security Manager you can override the System Security Manager (`System.getSecurityManager()`)

```kotlin

    withSecurityManager(myManager) {
      // Usage of security manager
    }

```

And the Listeners:

```kotlin
    class MyTest : FreeSpec() {
        
              override fun listeners() = listOf(SecurityManagerListener(myManager))
        
            init {
              // Use my security manager
            }
        
        }
```

#### System Exit Extensions

Sometimes you want to test that your code calls `System.exit`. For that you can use the `System Exit Listeners`. The Listener will throw an exception when the `System.exit` is called, allowing you to catch it and verify:

```kotlin

class MyTest : FreeSpec() {
        
  override fun listeners() = listOf(SpecSystemExitListener)
        
  init {
    "Catch exception" {
      val thrown: SystemExitException = shouldThrow<SystemExitException> {
        System.exit(22)
      }
                
      thrown.exitCode shouldBe 22
    }
  }      
}

```

### No System Out listeners

Maybe you want to guarantee that you didn't leave any debug messages around, or that you're always using a Logger in your logging.

For that, KotlinTest provides you with `NoSystemOutListener` and `NoSystemErrListener`. These listeners won't allow any messages to be printed straight to `System.out` or `System.err`, respectively:

```kotlin
    // In Project or in Spec
    override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)
```

### Locale/Timezone listeners

Some codes use and/or are sensitive to the default Locale and default Timezone. Instead of manipulating the system defaults no your own,
let KotlinTest do it for you!

```kotlin
withDefaultLocale(Locale.FRANCE) {
  println("My locale is now France! Très bien!")
}

withDefaultTimezone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo"))) {
  println("My timezone is now America/Sao_Paulo! Muito bem!")
}

```

And with the listeners

```kotlin
  // In Project or in Spec
  override fun listeners() = listOf(
    LocaleTestListener(Locale.FRANCE),
    TimeZoneTestListener(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")))
  )

```

### Current instant listeners

Sometimes you may want to use the `now` static functions located in `java.time` classes for multiple reasons, such as setting the creation date of an entity

`data class MyEntity(creationDate: LocalDateTime = LocalDateTime.now())`.
 
But what to do when you want to test that value? `now` will be different
each time you call it!

For that, KotlinTest provides `ConstantNowListener` and `withConstantNow` functions.

While executing your code, your `now` will always be the value that you want to test against.

```kotlin
val foreverNow = LocalDateTime.now()

withConstantNow(foreverNow) {
  LocalDateTime.now() shouldBe foreverNow
  delay(10) // Code is taking a small amount of time to execute, but `now` changed!
  LocalDateTime.now() shouldBe foreverNow
}

```

Or, with a listener for all the tests:

```kotlin
  override fun listeners() = listOf(
    ConstantNowTestListener(foreverNow)
  )
```
