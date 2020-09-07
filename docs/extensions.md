Kotest Extensions
====================


Kotest integrates with many libraries and frameworks. This page outlines the extensions maintained by the Kotest team.

* [Allure](#allure)
* [Arrow](#arrow)
* [Ktor](#ktor)
* [Koin](#koin)
* [Mockserver](#mockserver)
* [Roboelectric](#roboelectric)
* [Spring](#spring)
* [System / Timezone](#system-extensions)
* [Test Containers](#testcontainers)



### Arrow

The arrow assertion module provives assertions for the functional programming library [arrow-kt](https://arrow-kt.io/) for types such as `Option`, `Try`, and so on.
To use this library you need to add `kotest-assertions-arrow` to your build.

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

Other inspectors include `forNone`, `forAll`, `forExactly(n)`, `forSome` and so on. See the section on [inspectors](https://github.com/kotest/kotest/blob/master/doc/reference.md#inspectors) for more details.

### Spring

Kotest offers a Spring extension that allows you to test code that wires dependencies using Spring.
To use this extension add the `kotest-extensions-spring` module to your test compile path.

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
  override fun extensions(): List<Extension> = listOf(SpringAutowireConstructorExtension)
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




### Ktor

The ```kotest-assertions-ktor``` module provides response matchers for a [Ktor]("https://ktor.io/") application. There are matchers
for both `TestApplicationResponse` if you are using the server side test support, and for `HttpResponse` if you are using the ktor
client classes.

To add Ktor matchers, add the following dependency to your project

```groovy
testImplementation("io.kotest:kotest-assertions-ktor:${version}")
```

An example of using the matchers with the server side test support:
```kotlin
withTestApplication({ module(testing = true) }) {
   handleRequest(HttpMethod.Get, "/").apply {
      response shouldHaveStatus HttpStatusCode.OK
      response shouldNotHaveContent "failure"
      response.shouldHaveHeader(name = "Authorization", value = "Bearer")
      response.shouldNotHaveCookie(name = "Set-Cookie", cookieValue = "id=1234")
   }
}
```

And an example of using the client support:
```kotlin
val client = HttpClient(CIO)
val resp = client.post("http://mydomain.com/foo")
response.shouldHaveStatus(HttpStatusCode.OK)
response.shouldHaveHeader(name = "Authorization", value = "Bearer")

```




### Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with Kotest through the `KoinListener` test listener and its own interface `KoinTest`.

To add the listener to your project, add the depency to your project:
```groovy
testImplementation("io.kotest:kotest-extensions-koin:${version}")
```

With the dependency added, we can use Koin in our tests!

```kotlin
class KotestAndKoin : FunSpec(), KoinTest {

    override fun listeners() = listOf(KoinListener(myKoinModule))

    val userService by inject<UserService>()

    init {
      test("Use user service") {
        userService.getUser().username shouldBe "Kerooker"
      }
    }

}
```

### Roboelectric

[Robolectric](http://robolectric.org/) can be used with Kotest through the `RobolectricExtension` which can be found in `kotest-extensions-robolectric` module.

To add this module to project you need spcify following in your `build.gradle`:

```groovy
testImplementation 'io.kotest:kotest-extensions-robolectric:<version>'
```
With this dependency added you should add extensions to your project config. For example if you have no such config yet it would look like

```kotlin
class MyProjectLevelConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = super.extensions() + RobolectricExtension()
}
```

Of course you can just add this extension to another extensions you're already using.

After that done any class which should be ran with Robolectric should be annotated with `@RobolectricTest` annotation.

### Compilation test

The ```kotest-assertions-compiler``` extension provides matchers to assert that given kotlin code snippet compiles or not.
This extension is a wrapper over [kotlin-compile-testing](https://github.com/tschuchortdev/kotlin-compile-testing) and provides following matchers

* String.shouldCompile()
* String.shouldNotCompile()
* File.shouldCompile()
* File.shouldNotCompile()

To add the compilation matcher, add the following dependency to your project

```groovy
testImplementation("io.kotest:kotest-assertions-compiler:${version}")
```

Usage:
```kotlin
    class CompilationTest: StringSpec() {
        init {
            "shouldCompile test" {
                val codeSnippet = """ val aString: String = "A valid assignment" """.trimMargin()

                codeSnippet.shouldCompile()
                File("SourceFile.kt").shouldCompile()
            }

            "shouldNotCompile test" {
                val codeSnippet = """ val aInteger: Int = "A invalid assignment" """.trimMargin()

                codeSnippet.shouldNotCompile()
                File("SourceFile.kt").shouldNotCompile()
            }
        }
    }
```

During checking of code snippet compilation the classpath of calling process is inherited, which means any dependencies which are available in calling process will also be available while compiling the code snippet.


### System Extensions

Sometimes your code might use some functionalities straight from the JVM, which are very hard to simulate. With Kotest System Extensions, these difficulties are made easy to mock and simulate, and your code can be tested correctly. After changing the system and using the extensions, the previous state will be restored.

**Attention**: These code is very sensitive to concurrency. Due to the JVM specification there can only be one instance of these extensions running (For example: Only one Environment map must exist). If you try to run more than one instance at a time, the result is unknown.

#### System Environment

With *System Environment Extension* you can simulate how the System Environment is behaving. That is, what you're obtaining from `System.getenv()`.

Kotest provides some extension functions that provides a System Environment in a specific scope:

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

For that, Kotest provides you with `NoSystemOutListener` and `NoSystemErrListener`. These listeners won't allow any messages to be printed straight to `System.out` or `System.err`, respectively:

```kotlin
    // In Project or in Spec
    override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)
```

### Locale/Timezone listeners

Some codes use and/or are sensitive to the default Locale and default Timezone. Instead of manipulating the system defaults no your own,
let Kotest do it for you!

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

For that, Kotest provides `ConstantNowListener` and `withConstantNow` functions.

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




### MockServer

Kotest provides an extension for integration with the [MockServer](https://www.mock-server.com) library through the `kotest-extensions-mockserver` module.

MockServer is described as an in process server that returns specific responses for different requests via HTTP or HTTPS.
When MockServer receives a request it matches that request against the configured expectations. If a match is found it returns that response, otherwise a 404 is returned.

Read integration instructions [here](mockserver.md).




### TestContainers

[testcontainers-java](https://github.com/testcontainers/testcontainers-java) library that provide lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

```kotest-extensions-testcontainers``` provides integration for using testcontainers-java with kotest.

For using ```kotest-extensions-testcontainers``` add the below dependency in your build file.

```groovy
testImplementation("io.kotest:kotest-extensions-testcontainers:${version}")
```

Having this dependency in test classpath brings in extension method's in scope which let you convert any Startable such as a DockerContainer into a kotest TestListener, which you can register with Kotest and then Kotest will manage lifecycle of container for you.

For example:

```kotlin

class DatabaseRepositoryTest : FunSpec({
   val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
   listener(redisContainer.perTest()) //converts container to listener and registering it with Kotest.

   test("some test which assume to have redis container running") {
      //
   }
})
```

In above example ```perTest()``` extension method converts the container into a ```TestListener``` which start's the
redis container before each test and stop's that after test. Similarly if you want to reuse the container for all tests
in a single spec class you can use ```perSpec()``` extension method which convert's container into a ```TestListener```
which start's the container before running any test in spec and stop's that after all tests, thus a single container is
used by all tests in spec class.





### Allure

[Allure](http://allure.qatools.ru) is an open-source framework designed to generate detailed and interactive test reports.
It works by collecting test data as tests are executed and then compiling that into a final HTML report.

Kotest provides an extenstion for Allure and full integration instructions are [here](allure.md).

