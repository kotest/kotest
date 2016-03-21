# kotlintest

[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) [<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest*.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22kotlintest)

KotlinTest is a flexible and comprehensive testing tool for the [Kotlin](https://kotlinlang.org/) ecosystem based on and heavily inspired by the superb [Scalatest](http://www.scalatest.org/). KotlinTest provides several ways to lay out your test so that your team can pick the style they are most happy with. It also includes many matchers which allow you to write many different types of assertions easily and in a human readable way. Finally, there's helpers for things like collection testing, and future testing.

### Testing Styles

You can choose a testing style by extending FlatSpec, WordSpec, FunSpec or FreeSpec in your test class, and writing your tests inside an init {} block. _In ScalaTest, the body of the class is the constructor, so you write tests directly in the class body. The KotlinTest equivalent is the init block._

```kotlin
class MyTests : WordSpec {
  init {
    // tests here
  }
}
```

#### Flat Spec

Word spec offers the keywords `should`, and `with`, and allows those to be used inline, as such:

```kotlin
"ListStack.pop" should "return the last element from stack" with {
  val stack = ListStack<String>()
  stack.push("hello")
  stack.push("world")
  stack.pop() shouldBe "world"
}
"ListStack.pop" should "remove the element from the stack" with {
  val stack = ListStack<String>()
  stack.push("hello")
  stack.push("world")
  stack.pop()
  stack.size() shouldBe 1
}
```

#### Fun Spec

Fun spec allows you to create tests similar to the junit style. You invoke a method called test, with a parameter to describe the test, and then the test itself:

```kotlin
test("ListStack.pop should remove the last element from stack") {
  val stack = ListStack<String>()
  stack.push("hello")
  stack.push("world")
  stack.size() shouldBe 2
  stack.pop() shouldBe "world"
  stack.size() shouldBe 1
}

test("ListStack.peek should leave the stack unmodified") {
  val stack = ListStack<String>()
  stack.push("hello")
  stack.push("world")
  stack.size() shouldBe 2
  stack.peek() shouldBe "world"
  stack.size() shouldBe 2
}
```

#### Word Spec

Word spec offers the keywords `should`, and `with`, and allows those to be nested, as such:

```kotlin
"ListStack.pop" should {
  "return the last element from stack" with {
    val stack = ListStack<String>()
    stack.push("hello")
    stack.push("world")
    stack.pop() shouldBe "world"
  }
  "remove the element from the stack" with {
    val stack = ListStack<String>()
    stack.push("hello")
    stack.push("world")
    stack.pop()
    stack.size() shouldBe 1
  }
}
```

#### Flat Spec

Flat spec allows you to nest arbitary levels of depth using the keywords `-` (minus) and `with`, as such:

```kotlin
"given a ListStack" - {
   "then pop" - {
     "should return the last element from stack" with {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.pop() shouldBe "world"
      }
      "should remove the element from the stack" with {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.pop()
        stack.size() shouldBe 1
      }
    }
  }
}
```

### Matchers

KotlinTest has many built in matchers, along a similar line to the popular [hamcrest](http://hamcrest.org/) project. The simplest assertion is that a value should be equal to something, eg: `x shouldBe y` or `x shouldEqual y`.

#### String Matchers

* To assert that a string starts with a given prefix use `x should start with y`.
* To assert that a string ends with a given suffix use `x should end with y`.
* To assert that a string contains a given substring use `x should have substring y`.

#### Long / Int Matchers

* To assert that a value is greater than a given value use `x should be gt y`. This is the same as doing `(x > y) shouldBe true`. Choose whatever style you prefer. The same goes for the other operators lt, gte, lte.

#### Collection Matchers

* To assert that a collection has a given size use `col should have size 4`. This is the same as `(col,size == 4) shouldBe true` but more readable.

### Exceptions

To assert that a given block of code throws an exception, one can use the expecting(exception) block. Eg,

```kotlin
expecting(IllegalAccessException::class) {
  // code in here that you expect to throw a IllegalAccessException
}
```

### Inspectors

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

### How to use

KotlinTest is published to maven central, so to use, simply add the dependency in test scope to your gradle build:

`testCompile 'io.kotlintest:kotlintest:xxx'`
