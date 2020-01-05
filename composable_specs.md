 A design for #598 and #399

 **Goal**:

 To allow specs to be composed of smaller reusable specs. These abstractions should be easily parameterized, dynamic and mappable.

**Status**:

Currently this PR is incomplete. The basics are working but there are many more changes needed to fully convert the 3.x.y spec implementation into the 4.0.0 spec implementation. I have put this PR up for early review and feedback.

If we feel it's looking good we could merge and I can continue work on master. This way anyone else will not be doing PRs against a changing codebase. But if we feel it's not good enough I can continue to iterate.

**Implementation**:

This implementation splits specs into two types. _Class specs_ and _value specs_.

_Class specs_ are what you know and love already. Create a public class that extends one of the provided Spec classes and implement tests in the class body.

 ```kotlin
class MyTestClass: FunSpec() {
  init {
    test("foo") {
      1 + 1 shouldBe 2
    }
  }
}
```

_Value specs_ are new. They are defined as top level public vals that use a spec factory function such as `funSpec { }` or `behaviorSpec { }`
These factory functions allows tests to be defined in the same structure that the similarly named classes do. For example the factory equivalent of FunSpec is:

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

   isolationMode = IsolationMode.InstancePerLeaf

   assertionMode = AssertionMode.Error

   testCaseOrder = TestCaseOrder.Random

   test("my test") {
      1 + 1 shouldBe 2
   }
}
```

Specs defined as vals are in every way as functionally complete as specs defined as classes. Both types of specs will be
executed as part of the test suite so you do not need to use class based syntax if not desired.

If you don't want a class or a val to be executed then mark them as private.

```kotlin
private val skipped = funSpec { /*tests here*/ }
```

So what's the point of this new syntax? The point is to allow reusable composable specs. Since the result of a spec factory function is just a `Spec` value,
it can be passed around, filtered, mapped, copied or whatever else you want to do. It's just a value. More importantly however, it can be
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
val funtests = stringSpec {
  "test 1" {
    "abc".shouldHaveLength(3)
  }
}

val stringtests = stringSpec {
  "test 1" {
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

Since the spec factory functions are just that - functions - they can dynamically generate tests based on parameters.

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

**Restrictions**

* Class specs cannot be included into other class specs (I may drop this restriction if it proves easy to implement).
* You cannot merge isolation modes. For example, if a value spec has isolation mode of per test, you cannot then include it in a spec that has the default isolation mode.
* Other parameters lose out to the outer most spec. For example, if you include testsA into a classB then settings for test case order will be honoured from classB.
* Value specs can be automatically detected and included in a test suite, so they offer an alternative syntax to classes, but only on the JVM. With JS we'd need to always include in classes.

**Nomenclature:**

The nomenclature used here is totally open for change. I am currently using terms like `SpecBuilder`, `SpecFactory` and _value spec_ and _class spec_.









