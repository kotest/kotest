 A design for #598 and #399

 Note: This PR replaces the previous PR. Most notable change is that test factories (previously value specs) are not in themselves considered executable units.

 **Goal**:

 To allow specs to be composed of smaller reusable specs. These abstractions should be easily parameterized, dynamic and mappable.

**Status**:

Currently this PR is incomplete. The basics are working but more changes are required before this can be considered completed.
I would rather merge sooner than later so that other contributors are not working against a codebase that could change.

**Implementation**:

This implementation adds a new type of "test container" called `TestFactory`. A TestFactory is a way of defining tests (and test related configuraton)
and then import those tests into a spec.

To create a test factory, we use a test factory builder function, for example:

```kotlin
val funTests = funSpec {
   test("foo") {
      1 + 1 shouldBe 2
   }
   test("bar") {
      "a" + "b" shouldBe "ab"
   }
}
```

Callbacks such as before/after can be defined inline, along with configuration. For example:

```kotlin
val funTests = funSpec {

   beforeTest {
      println("Starting test case ${it.name}")
   }

   afterTest { testCase, result ->
      println(testCase.name + " is completed with result " + result)
   }

   assertionMode = AssertionMode.Error

   test("my test") {
      1 + 1 shouldBe 2
   }
}
```

The aim of these test test factories is to allow reusable composable specs. Since the result of a test factory builder function is just a `TestFactory` instance,
they can be passed around, filtered, mapped, copied or whatever else you want to do. It's just a value. More importantly however, it can be
included as part other specs.

```kotlin
val tests = stringSpec {
  "test 1" {
    "abc".shouldHaveLength(3)
  }
}

class MySpec : StringSpec() {
  init {
    include(tests)
    "test 2" {
      "".shouldHaveLength(0)
    }
  }
}
```

Note that you can even mix and match the spec styles when composing. The output reports will reflect the correct structure.

```kotlin
val funtests = funSpec {
  test("test 1") {
    "abc".shouldHaveLength(3)
  }
}

val stringtests = stringSpec {
  "test 2" {
    "zyx".shouldHaveLength(3)
  }
}


class MySpec : FunSpec() {
  init {
    include(funtests)
    include(stringtests)
  }
}
```

Since the test factory functions are just that - functions - they can dynamically generate tests based on parameters.

```kotlin
// defines an interface for the Semigroup typeclass
interface Semigroup<T> {
  fun combine(a: T, b: T): T
}

object StringSemigroup : Semigroup<String> {
  fun combine(a: String, b: String): T = "$a$b"
}

object LongSemigroup : Semigroup<Long> {
  fun combine(a: Long, b: Long): T = a + b
}

// every semigroup should uphold the law that ((ab)c) == (a(bc))
fun <T> associativity(sg: SemiGroup<T>, a: T, b: T, c: T) = funSpec {
  test("semigroup should be associative") {
    sg.combine(a, sg.combine(b, c)) shouldBe sg.combine(sg.combine(a, b), c)
  }
}

class StringSemigroupTest : FunSpec() {
  init {

    include(associativity(StringSemigroup, "a", "b", "c"))

    test("combination") {
        StringSemigroup.combine("a" ,"b") shouldBe "ab"
    }
  }
}

class LongSemigroupTest : FunSpec() {
  init {

    include(associativity(LongSemigroup, 1, 2, 3))

    test("combination") {
        LongSemigroup.combine(1, 2) shouldBe 3
    }
  }
}
```

Specs cannot be included into other specs, only test factories can be added to specs.

If you have some test factories and want to execute those without defining new tests, then you can use the `CompositeSpec` class.

```kotlin
val tests1 = funSpec { }
val tests2 = stringSpec { }
class SomeTests : CompositeSpec(tests1, tests2)
```








