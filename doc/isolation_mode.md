Isolation Modes
===============

Note: Isolation modes replace _One Instance Per Test_ which was a setting in version 3.1 and earlier.

All specs allow you to control how the test engine creates new instances of Specs for test cases. By default, one instance of the Spec
class is created and then each test _lambda_ is exected in turn until all tests have completed.

For example, in the following spec, the output would be "Hello From Sam", as each test is executed in turn in the same instance.

```kotlin
class SingleInstanceExample : WordSpec({
  "a" should {
    println("Hello")
    "b" {
        println("From")
    }
    "c" {
        println("Sam")
    }
  }
})
```

If you wish to create variables inside nested contests, or as top level members of the Spec itself, and you want them to be reset
for each test case, then you can change the isolation mode. To do this simply override the function `isolationMode(): IsolationMode`
to return an instance of the `IsolationMode` enum.

If you do not override this function, then `IsolationMode.SingleInstance` will be used which is what was described above, where a single instance of the Spec class was created for all the tests.

### InstancePerTest

The next mode is `IsolationMode.InstancePerTest` where a new spec will be created for every test case, including inner contexts.
In other words, outer contexts will execute as a "stand alone" test in their own instance of the spec.
An example should make this clear.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  init {
    "a" should {
      println("Hello")
      "b" {
        println("From")
      }
      "c" {
        println("Sam")
      }
    }
  }
}
```

Do you see how we've overriden the `isolationMode` function here.

When this is executed, the following will be printed:

```
Hello
Hello
From
Hello
Sam
```

This is because the outer context (test "a") will be executed first. Then it will be executed again for test "b", and then again for test "c".
Each time in a clean instance of the Spec class. This is very useful when we want to re-use variables.

Another example will show how the variables are reset.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val counter = AtomicInteger(0)

  init {
    "a" should {
      println("a=" + counter.getAndIncrement())
      "b" {
        println("b=" + counter.getAndIncrement())
      }
      "c" {
        println("c=" + counter.getAndIncrement())
      }
    }
  }
}
```

This time, the output will be:

a=0
a=0
b=1
a=0
c=1



### InstancePerLeaf

The next mode is `IsolationMode.InstancePerLeaf` where a new spec will be created for every leaf test case - so excluding inner contexts.
In other words, inner contexts are only executed as part of the "path" to an outer test.
An example should make this clear.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

  init {
    "a" should {
      println("Hello")
      "b" {
        println("From")
      }
      "c" {
        println("Sam")
      }
    }
  }
}
```

When this is executed, the following will be printed:

```
Hello
From
Hello
Sam
```

This is because the outer context - test "a" - will be executed first, followed by test "b" in the same instance.
Then a new spec will be created, and test "a" again executed, followed by test "c".

Another example will show how the variables are reset.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

  val counter = AtomicInteger(0)

  init {
    "a" should {
      println("a=" + counter.getAndIncrement())
      "b" {
        println("b=" + counter.getAndIncrement())
      }
      "c" {
        println("c=" + counter.getAndIncrement())
      }
    }
  }
}
```

This time, the output will be:

a=0
b=1
a=0
c=1
