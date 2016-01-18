# ktest

KTest is a flexible and comprehensive testing tool for the [Kotlin](https://kotlinlang.org/) ecosystem based on and heavily inspired by the superb [Scalatest](http://www.scalatest.org/). KTest offers KTest offers several styles of test layout so that your team can pick the style they are most happy with.

### Testing Styles

You can choose a testing style by extending in FlatSpec, WordSpec, or FlatSpec in your test class, and writing your tests inside an init {} block

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

### How to use

KTest is published to maven central, so to use, simply add the dependency in test scope to your gradle build:

`testCompile 'com.sksamuel.ktest:ktest:0.90.0'`
