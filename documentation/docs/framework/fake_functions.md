---
title: Fake Functions
slug: fakery.html
---

In functional programming, our dependencies are less likely to be instances of concrete classes and more likely to be functions.
Whenever we are unit testing something with functional dependencies, it's usually easier to just pass another function
rather than mock that dependency. Consider, for example, the following implementation:

```kotlin
fun interface HasAnswer {
   fun answer(question: String): Int
}

class AnsweringService: HasAnswer {
   override fun answer(question: String): Int { TODO() }
}

class MyService(private val hasAnswer: HasAnswer) {
   fun answer(question: String): Int = hasAnswer.answer(question)
}
```

Traditionally, we would mock `HasAnswer` and pass that mock to `MyService`:

```kotlin
val mockHasAnswer = run {
  val ret = mockk<HasAnswer>()
  every { ret.answer(any()) } returns 42
  ret
}

val myService = MyService(mockHasAnswer)
// tests here
```

However, we can also just pass a lambda, which is so very much simpler:

```kotlin
val myService = MyService(hasAnswer = { 42 })
// tests to follow
```
<br/>
<br/>

If we want this test-double function to return different values and/or throw exceptions, kotest has simple helper functions which make these tasks easier, such as:

```kotlin
 val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
 fakeFunction.next() shouldBe "yes"
 fakeFunction.next() shouldBe "no"
 fakeFunction.next() shouldBe "maybe"
```

This fake function can be used in unit tests as follows:

```kotlin
val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
val myService = MyService { fakeFunction.next() }
myService.answer("what") shouldBe "yes"
myService.answer("when") shouldBe "no"
myService.answer("where") shouldBe "maybe"
```
Should we need a fake function that sometimes returns a value and sometimes throws an exception,it can easily be done as follows:

```kotlin
val fakeFunction = sequenceOf(
  Result.success("yes"),
  Result.failure(RuntimeException("bad request")),
  Result.success("no")
).toFunction()
fakeFunction.next() shouldBe "yes"
shouldThrow<RuntimeException> { fakeFunction.next() }
fakeFunction.next() shouldBe "no"
```

As this function implements `HasAnswer` interface, we can use it as a dependency in our unit tests as well.
