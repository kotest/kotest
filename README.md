KotlinTest
==========

[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) [<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest*.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()

KotlinTest is a flexible and comprehensive testing tool for the [Kotlin](https://kotlinlang.org/) ecosystem based on and heavily inspired by the superb [Scalatest](http://www.scalatest.org/). KotlinTest provides several ways to lay out your test so that your team can pick the style they are most happy with. It also includes many matchers which allow you to write many different types of assertions easily and in a human readable way. Finally, there's helpers for things like collection testing, and future testing.

For latest updates see [Changelog](CHANGELOG.md)

Community
---------
* [Forum](https://groups.google.com/forum/#!forum/kotlintest)
* [Stack Overflow](http://stackoverflow.com/questions/tagged/kotlintest) (Ask the first question there and don't forget to use the tag "kotlintest".)
* [Contribute](https://github.com/kotlintest/kotlintest/wiki/contribute)

How to use
----------

KotlinTest is published to Maven Central, so to use, simply add the dependency in test scope to your build file. You can get the latest version from the little badge at the top of the readme.

Gradle:

    testCompile 'io.kotlintest:kotlintest:xxx'

Maven:

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest</artifactId>
    <version>xxx</version>
    <scope>test</scope>
</dependency>
```

Testing Styles
--------------

You can choose a testing style by extending StringSpec, WordSpec, FunSpec, ShouldSpec, FlatSpec, FeatureSpec, BehaviorSpec or FreeSpec in your test class, and writing your tests inside an init {} block. _In ScalaTest, the body of the class is the constructor, so you write tests directly in the class body. The KotlinTest equivalent is the init block._

```kotlin
class MyTests : StringSpec() {
  init {
    // tests here
  }
}
```

### String Spec

`StringSpec` reduces the syntax to the absolute minimum. Just write a string followed by a lambda expression with your test code. If in doubt, use this style.

```kotlin
class StringSpecExample : StringSpec() {
  init {
    "strings.length should return size of string" {
      "hello".length shouldBe 5
    }
  }
}
```

### Flat Spec

`FlatSpec` offers the keywords `should` and allows that to be used inline, as such:

```kotlin
class MyTests : FlatSpec() {
  init {
    "String.length" should "return the length of the string" {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

### Fun Spec

`FunSpec` allows you to create tests similar to the junit style. You invoke a method called test, with a string parameter to describe the test, and then the test itself:

```kotlin
class MyTests : FunSpec() {
  init {
    test("String.length should return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

### Should Spec

`ShouldSpec` is similar to fun spec, but uses the keyword `should` instead of `test`. Eg:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

This can be nested in context strings too, eg

```kotlin
class MyTests : ShouldSpec() {
  init {
    "String.length" {
      should("return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

### Word Spec

`WordSpec` uses the keyword `should` and uses that to nest test blocks after a context string, eg:

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

### Feature Spec

`FeatureSpec` allows you to use `feature` and `scenario`, as such:

```kotlin
class MyTests : FlatSpec() {
  init {
    feature("the thingy bob") {
      scenario("should explode when I touch it") {
        // test here
      }
      scenario("and should do this when I wibble it") {
        // test heree
      }
    }
  }
}
```

### Behavior Spec

`BehaviorSpec` allows you to use `given`, `when`, `then`, as such:

```kotlin
class MyTests : FlatSpec() {
  init {
    given("a broomstick") {
      `when`("I sit on it") {
        then("I should be able to fly") {
          // test code
        }
      }
      `when`("I throw it away") {
        then("it should come back") {
          // test code
        }
      }
    }
  }
}
```

Because `when` is a keyword in Kotlin, we must enclose with backticks. Alternatively, there are title case versions
available if you don't like the use of backticks, eg, `Given`, `When`, `Then`.

### Free Spec

`FreeSpec` allows you to nest arbitary levels of depth using the keyword `-` (minus), as such:

```kotlin
class MyTests : FreeSpec() {
  init {
    "String.length" - {
      "should return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

Property-based Testing
----------------------

### Property Testing

To automatically test your code with many combinations of values, you can allow KotlinTest to do the boilerplate
by using property testing with `generators`. You invoke `forAll` or `forNone` and pass in a function, where the function
parameters are populated automatically with many different values. The function must specify explcitly the parameter
types as KotlinTest will use those to determine what types of values to pass in. 

For example, here is a property test that checks that for any two Strings, the length of `a + b` 
is the same as the length of `a` plus the length of `b`. In this example KotlinTest would 
execute the test 100 tests for random String combinations.

```kotlin
class PropertyExample: StringSpec() {

  "String size" {
    forAll({ a: String, b: String ->
      (a + b).length == a.length + b.length
    })
  }

}
```

There are generators for all the common types - String, Ints, Sets, etc. If you need to generate custom types
then you can simply specify the generator manually (and write your own). For example here is the same test again but
with the generators specified.

```kotlin
class PropertyExample: StringSpec() {

  "String size" {
    forAll(Gen.string(), Gen.string(), { a: String, b: String ->
      (a + b).length == a.length + b.length
    })
  }

}
```

To write your own generator for a type T, you just implement the interface `Gen<T>`. For example you could write
a `Gen` that supports a custom class called `Person`:

```kotlin
data class Person(val name: String, val age: Int)
class PersonGenerator : Gen<Person> {
  override fun generate(): Person = Person(Gen.string().generate(), Gen.int().generate())
}
```
 

### Table-driven Testing

To test your code with different parameter combinations, you can use tables as input for your test 
cases. 

Your test class should extend from the interface `TableTesting`. Create a table with the `table` function and 
pass a header and one or more row objects. You create the headers with the `headers` function, and
a row with the `row` function. A row can have up to 22 entries. Headers and and rows must all have
the same number of entries.

To use the table, you invoke `forAll(table)` inside a test plan and pass a closure with the actual test code.
The entries of the rows are passed as parameters to the closure.

Table testing can be used with any spec. Here is an example using `StringSpec`.


```kotlin
class StringSpecExample : StringSpec() {
  init {
    "should add" {
       val myTable = table(
         headers("a", "b", "result"),
         row(1, 2, 3),
         row(1, 1, 2)
       )
       forAll(myTable) { a, b, result ->
         a + b shouldBe result
       }
    }
  }
}
```

Matchers
--------

KotlinTest has many built in matchers, along a similar line to the popular [hamcrest](http://hamcrest.org/) project. The simplest assertion is that a value should be equal to something, eg: `x shouldBe y` or `x shouldEqual y`. This will also work for null values, eg `x shouldBe null` or `y shouldEqual null`.

### String Matchers

* To assert that a string starts with a given prefix use `str should startWith(y)`.
* To assert that a string ends with a given suffix use `str should endWith(y)`.
* To assert that a string contains a given substring use `str should have substring y`.
* To assert that a string matches a given regular expression, use `str should match("regex")`.
* To assert that a string has a given length, use `str should haveLength(10)`

### Long / Int Matchers

* To assert that a value is greater than a given value use `x should be gt y`. This is the same as doing `(x > y) shouldBe true`. Choose whatever style you prefer. The same goes for the other operators lt, gte, lte.

### Double Matchers

* To assert that a double is exactly equal to another double use `d shouldBe exactly(e)`
* To assert that a double is equal within some tolerance range, use `d shouldBe (e plusOrMinus y)`

### Collection Matchers

* To assert that a collection has a given size use `col should haveSize(4)`. This is the same as `(col.size == 4) shouldBe true` but more readable.
* To assert that a collection contains a given element use `col should contain(x)`.
* To assert that a collection has a given collection of elements in any order, you can use `col should containInAnyOrder(xs)`

### Map Matchers

* To assert that a map contains a given key use `map should haveKey(k)`.
* To assert that a map contains a given value use `map should haveValue(v)`.
* To assert that a map contains a given mappings use `col should contain(k,v)`.

### References

* To assert that two instances are the same reference, you can use `x should beTheSameInstanceAs(y)`

Exceptions
----------

To assert that a given block of code throws an exception, one can use the `shouldThrow` function. Eg,

```kotlin
shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
```

You can also check the caught exception:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
exception.message should start with "Something went wrong"
```

Before and After Each (since 1.1.0)
-----------------------------------

If you need to run a method before each test and/or after each test (perhaps to reset some values to defaults etc), then simply override the `beforeEach` and `afterEach` methods in your test class, eg:

```kotlin
override fun beforeEach() {
  println("Test starting")
}
```

```kotlin
override fun afterEach() {
  println("Test completed")
}
```


Before and After All
--------------------

If you need to run a setup/tear down function before and after all the tests have run, then simply override the `beforeAll` and `afterAll` methods in your test class, eg:

```kotlin
override fun beforeAll() {
  println("Setting up my tests")
}
```

```kotlin
override fun afterAll() {
  println("Cleaning up after my tests")
}
```

One Instance Per Test (since 1.1.0)
-----------------------------------

By default a single instance of the test class is created for all the test it contains. However, if you wish to have a fresh instance per test (sometimes its easier to have setup code in the init block instead of resetting after each test) then simply override the `oneInstancePerTest` value and set it to true, eg:

```kotlin
class MyTests : ShouldSpec() {
  override val oneInstancePerTest = true
  init {
    // tests here
  }
}
```

Testing Config (since 1.2.0)
----------------------------

Each test can be configured with various parameters. After the test block, invoke the config method passing in the parameters you wish to set. The available parameters are:

* `invocations` - the number of times to run this test. Useful if you have a non-deterministic test and you want to run that particular test a set number of times. Defaults to 1.
* `threads` - Allows the invocation of this test to be parallelized by setting the number of threads to use in a thread pool executor for this test. If invocations is 1 (the default) then this parameter will have no effect. Similarly, if you set invocations to a value less than or equal to the number threads, then each invocation will have its own thread.
* `ignored` - If set to true then this test is ignored. Can be useful if a test needs to be temporarily disabled.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `Duration` which can be instantiated like `2.seconds`, `3.minutes` and so on.
* `tag` / `tags` - a list of String tags that can be set on a test. Then by invoking the test runner with a system property of testTags, you can control which tests are run. For example, tests that require a linux based O/S might be tagged with "linux" then gradle could be invoked with gradle test -DtestTags=linux. Another example might be tagging database tags that you only want to run on a server that has a database installed. Any test that has no tags is always run.

Examples of setting config:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }.config(invocations=10, threads=2)
  }
}
```

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }.config(timeout = 2.seconds)
    }
  }
}
```

```kotlin
class FunSpecTest : FunSpec() {
  init {
    test("FunSpec should support config syntax") {
    }.config(tags = listOf("database", "linux"))
  }
}
```

Closing resource automatically (since 1.3.0)
--------------------------------------------

You can let KotlinTest close resources automatically after all tests have been run:

```kotlin
class StringSpecExample : StringSpec() {

  val reader = autoClose(StringReader("xyz"))

  init {
    "your test case" {
      // use resource reader here
    }
  }
}
```

Resources that should be closed this way must implement [`java.io.Closeable`](http://docs.oracle.com/javase/6/docs/api/java/io/Closeable.html). Closing is performed in  
reversed order of declaration after `afterAll()` was executed.

Inspectors
----------

Inspectors allow us to test elements in a collection. For example, if we had a collection from a method and we wanted to test that every element in the collection passed some assertions, we can do:

```kotlin
val xs = // some collection
forAll(xs) { x =>
  x should have substring "qwerty"
  x should start with "q"
}
```

Similarly, if we wanted to asset that NO elements in a collection passed some assertions, we can do:

```kotlin
val xs = // some collection
forNone(xs) { x =>
  x should have substring "qwerty"
  x should start with "q"
}
```

The full list of inspectors are:

* `forAll` which asserts every element passes the assertions
* `forNone` which asserts no element passes
* `forOne` which asserts only a single element passed
* `forAtMostOne` which asserts that either 0 or 1 elements pass
* `forAtLeastOne` which asserts that 1 or more elements passed
* `forAtLeast(k)` which is a generalization that k or more elements passed
* `forAtMost(k)` which is a generalization that k or fewer elements passed
* `forAny` which is an alias for `forAtLeastOne`
* `forSome` which asserts that between 1 and n-1 elements passed. Ie, if NONE pass or ALL pass then we consider that a failure.
* `forExactly(k)` which is a generalization that exactly k elements passed. This is the basis for the implementation of the other methods

Eventually
----------

When testing future based code, it's handy to be able to say "I expect these assertions to pass in a certain time". Sometimes you can do a Thread.sleep but this is bad as you have to set a timeout that's high enough so that it won't expire prematurely. Plus it means that your test will sit around even if the code completes quickly. Another common method is to use countdown latches. KotlinTest provides the `Eventually` mixin, which gives you the `eventually` method which will repeatedly test the code until it either passes, or the timeout is reached. This is perfect for nondeterministic code. For example:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("do something") {
      eventually(5.seconds) {
        // code here that should complete in 5 seconds but takes an indetermistic amount of time.
      }
    }
  }
}
```
