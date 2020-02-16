Grouping Tests with Tags
------------------------

Sometimes you don't want to run all tests and Kotest provides tags to be able to run only
certain tests. Tags are objects inheriting from `io.kotest.Tag`.

To group tests by operating system you could define the following tags:

```kotlin
object Linux : Tag()
object Windows: Tag()
```

Test cases are marked with tags with the `config` function:

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

Then by invoking the test runner with a system property of `kotest.tags.include` and/or `kotest.tags.exclude`, you
can control which tests are run:

* If no `kotest.tags.include` and/or `kotest.tags.exclude` are specified, all tests (both tagged and untagged ones) are run.
* If only `kotest.tags.include` are specified, only tests with that tag are run (untagged test are *not* run).
* If only `kotest.tags.exclude` are specified, only tests without that tag are run (untagged tests *are* run).
* If you provide more than one tag for `kotest.tags.include` or `kotest.tags.exclude`, a test case with at least one of the given tags is included/excluded.

Provide the simple names of tag object (without package) when you run the tests.
Please pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag.

Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke
Gradle like this:

```
gradle test -Dkotest.tags.include=Linux -Dkotest.tags.exclude=Database
```

If you use `kotest.tags.include` and `kotest.tags.exclude` in combination, only the tests tagged with a tag from
`kotest.tags.include` but not tagged with a tag from `kotest.tags.exclude` are run. If you use only `kotest.tags.exclude`
all tests but the tests tagged with the given tags are are run.

Tags can also be included/excluded in runtime (for example, if you're running a project configuration instead of properties) through the `RuntimeTagExtension`:

```kotlin
RuntimeTagExtension.included += MyTag
RuntimeTagExtension.excluded += MyOtherTag
```

Everything else will work like the described above.



**A special attention is needed in your gradle configuration**

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



