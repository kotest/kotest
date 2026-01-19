---
title: Test Factories
slug: test-factories.html
---



Sometimes we may wish to write a set of generic tests and then reuse them for specific inputs. In Kotest we can do this via _test factories_ which create tests that can be _included_ into one or more specs.

## Overview

Say we wanted to build our own collections library. A slightly trite example, but one that serves the documentation purpose well.

We could create an interface `IndexedSeq` which has two implementations, `List` and `Vector`.

```kotlin
interface IndexedSeq<T> {

    // returns the size of t
    fun size(): Int

    // returns a new seq with t added
    fun add(t: T): IndexedSeq<T>

    // returns true if this seq contains t
    fun contains(t: T): Boolean
}
```

If we wanted to test our `List` implementation, we could do this:

```kotlin
class ListTest : WordSpec({

   val empty = List<Int>()

   "List" should {
      "increase size as elements are added" {
         empty.size() shouldBe 0
         val plus1 = empty.add(1)
         plus1.size() shouldBe 1
         val plus2 = plus1.add(2)
         plus2.size() shouldBe 2
      }
      "contain an element after it is added" {
         empty.contains(1) shouldBe false
         empty.add(1).contains(1) shouldBe true
         empty.add(1).contains(2) shouldBe false
      }
   }
})
```

Now, if we wanted to test `Vector` we have to copy n paste the test. As we add more implementations and more tests, the likelihood is our test suite will become fragmented and out of sync.

We can address this by creating a test factory, which accepts an `IndexedSeq` as a parameter.

To create a test factory, we use a builder function such as `funSpec`, `wordSpec` and so on. A builder function exists for each of the spec [styles](styles.md).

So, to convert our previous tests to a test factory, we simply do the following:

```kotlin
fun <T> indexedSeqTests(name: String, empty: IndexedSeq<T>) = wordSpec {
   name should {
      "increase size as elements are added" {
         empty.size() shouldBe 0
         val plus1 = empty.add(1)
         plus1.size() shouldBe 1
         val plus2 = plus1.add(2)
         plus2.size() shouldBe 2
      }
      "contain an element after it is added" {
         empty.contains(1) shouldBe false
         empty.add(1).contains(1) shouldBe true
         empty.add(1).contains(2) shouldBe false
      }
   }
}
```

And then to use this, we must include it one or more times into a spec (or several specs).


```kotlin
class IndexedSeqTestSuite : WordSpec({
   include(indexedSeqTests("vector"), Vector())
   include(indexedSeqTests("list"), List())
})
```


:::tip
You can include any style factory into any style spec. For example, a fun spec factory can be included into a string spec class.
:::



A test class can include several different types of factory, as well as inline tests as normal. For example:

```kotlin
class HugeTestFile : FunSpec({

   test("first test") {
     // test here
   }

   include(factory1("foo"))
   include(factory2(1, 4))

   test("another test") {
     //  testhere
   }
})
```

Each included test appears in the test output and reports as if it was individually defined.


:::note
Tests from factories are included in the order they are defined in the spec class.
:::


## Listeners

Test factories support the usual before and after test callbacks. Any callback added to a factory, will in turn be added to the spec or specs where the factory is included.

However, only those tests generated _by that factory_ will have the callback applied. This means you can create stand alone factories with their own lifecycle methods and be assured they won't clash with lifecycle methods defined in other factories or specs themselves.

For example:


```kotlin
val factory1 = funSpec {
  beforeTest {
     println("Executing $it")
  }
  test("a") {  }
  test("b") {  }
}
```

```kotlin
class LifecycleExample : FunSpec({
   include(factory1)
   test("c")
   test("d")
})
```

After executing the test suite, the following would be printed:

```bash
Executing a
Executing b
```

And as you can see, the `beforeTest` block added to `factory1` only applies to those tests defined in that factory, and not in the tests defined in the spec it was added to.
