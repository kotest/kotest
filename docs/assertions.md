Assertions
==========

Kotest is split into several subprojects which can be used independently. One of these subprojects is
the comprehensive assertion / matchers support. These can be used with the Kotest test framework, or with
another test framework like JUnit or Spock.

## Matchers

The core functionality of the assertion modules is without doubt the statements that
confirm that your test is in the state you expect. For example, to assert that a variable has an expected value:

`name shouldBe "sam"`

Kotest calls these functions _matchers_.

There are general purpose matchers, such as `shouldBe` as well as matchers for many other specific scenarios,
such as `str.shouldHaveLength(10)` and `file.shouldBeDirectory()`. They come in both infix and regular variants.

There are over 325 matchers spread across multiple modules. Read about all the [matchers here](matchers.md).




### Custom Matchers

It is easy to add your own matchers. Simply extend the `Matcher<T>` interface, where T is the type you wish to match against.
The Matcher interface specifies one method, `test`, which you must implement returning an instance of Result.
The Result contains a boolean to indicate if the test passed or failed, and two messages.

```kotlin
interface Matcher<in T> {
   fun test(value: T): MatcherResult
}
```

Matcher is _contravariant_ so a matcher for Number can be used to test an Int, for example.

The first message should always be in the positive, ie, indicate what "should" happen, and the second message
is used when the matcher is used with _not_.

For example to create a matcher that checks that a string contains the substring "foo", we can do the following:

```kotlin
fun containFoo() = object : Matcher<String> {
  override fun test(value: String) = MatcherResult(value.contains("foo"), "String $value should include foo", "String $value should not include foo")
}
```
This matcher could then be used as follows:

```kotlin
"hello foo" should containFoo()
"hello bar" shouldNot containFoo()
```

And we should then create an extension function version, like this:

```kotlin
fun String.shouldContainFoo() = this should containFoo()
fun String.shouldNotContainFoo() = this shouldNot containFoo()
```






## Exceptions

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
exception.message should startWith("Something went wrong")
```

If you want to test that _exactly_ a type of exception is thrown, then use `shouldThrowExactly<E>`.
If you want to test that _any_ exception is thrown, then use `shouldThrowAny`.





## Soft Assertions

Normally, assertions like `shouldBe` throw an exception when they fail.
But sometimes you want to perform multiple assertions in a test, and
would like to see all of the assertions that failed. Kotest provides
the `assertSoftly` function for this purpose.

```kotlin
assertSoftly {
  foo shouldBe bar
  foo should contain(baz)
}
```

If any assertions inside the block failed, the test will continue to
run. All failures will be reported in a single exception at the end of
the block.

Another version of `assertSoftly` takes a test target and lambda with test target as its receiver.

```kotlin
assertSoftly(foo) {
    shouldNotEndWith("b")
    length shouldBe 3
}
```


We can configure assert softly to be implicitly added to every test via [project config](project_config.md).



## Inspectors

Inspectors allow us to test elements in a collection, and assert the quantity of elements that should be
expected to pass (all, none, exactly k and so on)

Read about [inspectors here](inspectors.md)




## Assertion Mode


You can ask Kotest to fail the build, or output a warning to stderr, if a test is executed that does not use a Kotest assertion (other assertion libraries are not detected).

To do this, set `assertionMode` to `AssertionMode.Error` or `AssertionMode.Warn` inside a spec. For example.

```kotlin
class MySpec : FunSpec() {
   init {
      assertions = AssertionMode.Error
      test("this test has no assertions") {
         val name = "sam"
         name.length == 3 // this isn't actually testing anything
      }
   }
}
```

Running this test will output something like:

```
Test 'this test has no assertions' did not invoke any assertions
```

If we want to set this globally, we can do so in [project config](project_config.md).
