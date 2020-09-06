Grouping Tests with Tags
------------------------

Sometimes you don't want to run all tests and Kotest provides tags to be able to determine which
tests are executed at runtime. Tags are objects inheriting from `io.kotest.core.Tag`.

For example, to group tests by operating system you could define the following tags:

```kotlin
object Linux : Tag()
object Windows: Tag()
```

Alternatively, tags can be defined using the `StringTag` class. When using this class, observe the following rules:

- A tag must not be null or blank.
- A tag must not contain whitespace.
- A tag must not contain ISO control characters.
- A tag must not contain any of the following characters:
    - !: exclamation mark
    - (: left paren
    - ): right paren
    - &: ampersand
    - |: pipe

For example:

```kotlin
val tag = StringTag("Linux")
```

#### Marking Tests

Test cases can then be marked with tags using the `config` function:

```kotlin
import io.kotest.specs.StringSpec

class MyTest : StringSpec() {
  init {
    "should run on Windows".config(tags = setOf(Windows)) {
      // ...
    }

    "should run on Linux".config(tags = setOf(Linux)) {
      // ...
    }

    "should run on Windows and Linux".config(tags = setOf(Windows, Linux)) {
      // ...
    }
  }
}
```


#### Running with Tags

Then by invoking the test runner with a system property of `kotest.tags` you can control which tests are run. The expression to be
passed in is a simple boolean expression using boolean operators: `&`, `|`, `!`, with parenthesis for association.

For example, `Tag1 & (Tag2 | Tag3)`

Provide the simple names of tag object (without package) when you run the tests.
Please pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag.


Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke
Gradle like this:

```
gradle test -Dkotest.tags="Linux & !Database"
```

Tags can also be included/excluded in runtime (for example, if you're running a project configuration instead of properties) through the `RuntimeTagExtension`:

```kotlin
RuntimeTagExpressionExtension.expression = "Linux & !Database"
```

#### Tag Expression Operators

Operators (in descending order of precedence)

| Operator | Description | Example |
|----------|-------------|---------------|
|!|not|!macos|
|&|and|linux & integration|
|&#124;|or|windows &#124; microservice|


#### Marking Specs

You can mark all tests in a spec using the tags function in the spec itself.

```kotlin
class MyTestClass : FunSpec({

  tags(Linux, Mysql)

  test("my test") { } // automatically marked with the above tags
})
```

Note: When tagging tests in this way, the Spec will still need to be instantiated in order to retrieve
the tags. If no root tests are active at runtime, the [beforeSpec](listeners.md) and [afterSpec](listeners.md)
callbacks will _not_ be invoked.

If you wish to avoid creating the spec class at all then you can annotate a spec using `@Tags(tag1, ...)`.
Any tags added using this annotation apply to all tests in the class, however this will not stop a class from
being instantiated unless explicitly excluded.

Consider the following example:

```kotlin
@Tags(Linux)
class MyTestClass : FunSpec({

  tags(UnitTest)

  beforeSpec { println("Before") }

  test("A").config(tags = setOf(Mysql)) {}
  test("B").config(tags = setOf(Postgres)) {}
  test("C") {}
})
```

| Property | Spec Created | Callbacks | Outcome |
|----------|--------------|---------|----------|
| kotest.tags=Linux                  | yes | yes | A, B, C are executed because all tests inherit the `Linux` tag from the annotation |
| kotest.tags=Linux & Mysql          | yes | yes | A is executed only because all tests have the `Linux` tag, but only A has the `Mysql` tag |
| kotest.tags=!Linux                 | no  | no  | No tests are executed, and the MyTestClass is not instantiated because we can exclude it based on the tags annotation |
| kotest.tags=!UnitTest              | yes | no  | No tests are executed because all tests inherit `UnitTest` from the tags function. MyTestClass is instantiated in order to retrieve the tags defined in the class. The beforeSpec callback is not executed because there are no active tests. |
| kotest.tags=Mysql                  | yes | yes | A is executed only, because that is the only test marked with `Mysql` |
| kotest.tags=!Mysql                 | yes | yes | B, C are executed only, because A is excluded by being marked with `Mysql` |
| kotest.tags=Linux & !Mysql         | yes | yes | B, C are executed only, because all tests inherit `Linux` from the annotation, but A is excluded by the `Mysql` tag |


#### Gradle

**Special attention is needed in your gradle configuration**

To use System Properties (-Dx=y), your gradle must be configured to propagate them to the test executors, and an extra configuration must be added to your tests:

Groovy:
```
test {
    //... Other configurations ...
    systemProperties = System.properties
}
```

Kotlin Gradle DSL:
```
val test by tasks.getting(Test::class) {
    // ... Other configurations ...
    systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
}
```

This will guarantee that the system property is correctly read by the JVM



