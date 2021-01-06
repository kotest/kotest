---
id: mocks
title: Mocking and Kotest
sidebar_label: Mocking
slug: mocking.html
---



Kotest itself has no mock features. However, you can plug-in your favourite mocking library with ease!

Let's take for example [mockk](https://mockk.io):

```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)

    test("Saves to repository") {
        every { repository.save(any()) } just Runs
        target.save(MyDataClass("a"))
        verify(exactly = 1) { repository.save(MyDataClass("a")) }
    }

})
```

This example works as expected, but what if we add more tests that use that _mockk_?

```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)

    test("Saves to repository") {
        every { repository.save(any()) } just Runs
        target.save(MyDataClass("a"))
        verify(exactly = 1) { repository.save(MyDataClass("a")) }
    }

    test("Saves to repository as well") {
        every { repository.save(any()) } just Runs
        target.save(MyDataClass("a"))
        verify(exactly = 1) { repository.save(MyDataClass("a")) }
    }

})
```

The above snippet will cause an exception!
>2 matching calls found, but needs at least 1 and at most 1 calls

This will happen because the mocks are not restarted between invocations. By default, Kotest isolates tests by creating
[a single instance of the spec](../isolation_mode.md) for all the tests to run.

This leads to mocks being reused. But how can we fix this?

### Option 1 - setup mocks before tests

```kotlin
class MyTest : FunSpec({

    lateinit var repository: MyRepository
    lateinit var target: MyService

    beforeTest {
        repository = mockk()
        target = MyService(repository)
    }

    test("Saves to repository") {
        // ...
    }

    test("Saves to repository as well") {
        // ...
    }

})
```

### Option 2 - reset mocks after tests
```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)

    afterTest {
        clearMocks(repository)
    }

    test("Saves to repository") {
        // ...
    }

    test("Saves to repository as well") {
        // ...
    }

})
```

### Positioning the listeners

As for any function that is executed inside the Spec definition, you can place listeners at the end

```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)


    test("Saves to repository") {
        // ...
    }

    test("Saves to repository as well") {
        // ...
    }

    afterTest {
        clearMocks(repository)  // <---- End of file, better readability
    }

})
```

### Option 3 - Tweak the IsolationMode

Depending on the usage, playing with the IsolationMode for a given Spec might be a good option as well.
Head over to [isolation mode documentation](../isolation_mode.md) if you want to understand it better.

```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)


    test("Saves to repository") {
        // ...
    }

    test("Saves to repository as well") {
        // ...
    }

    isolation = IsolationMode.InstancePerTest

})
```
