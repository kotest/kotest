---
id: tempfile
title: Temporary Files
slug: temporary-files
---



Sometimes it is required for a test to create a file and delete it after test, deleting it manually may lead to flaky
test.

For example, you may be using a temporary file during a test. If the test passes successfully, your clean up code will execute
and the file will be deleted. But in case the assertion fails or another error occurs, which may have caused the file to not be deleted, you will get a stale file
which might affect the test on the next run (file cannot be overwritten exception and so on).

Kotest provides a function ```tempfile()``` which you can use in your Spec to create a temporary file for your tests, and which
 Kotest will take the responsibility of cleaning up after running all tests in the Spec. This way your
tests does not have to worry about deleting the temporary file.

```kotlin
class MySpec : FunSpec({

   val file = tempfile()

   test("a temporary file dependent test") {
      //...
   }
})

```

## Temporary Directories

Similar to temp files, we can create a temp dir using `tempdir()`.

```kotlin
class MySpec : FunSpec({

   val dir = tempdir()

   test("a temporary dir dependent test") {
      //...
   }
})
```
