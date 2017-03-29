package io.kotlintest.matchers

import java.io.File
import java.io.FileNotFoundException

fun matcherDsl() {

  // example of a tolerance match for doubles
  1.0 shouldBe (1.3 plusOrMinus 0.2)

  // example of an exact match for doubles
  1.0 shouldBe exactly(1.0)

  // example of expecting an exception
  shouldThrow<FileNotFoundException> {
    File("bibble").length()
  }

  // example of combining matchers
  "hello world" should (startWith("hello") and endWith("world"))

  // a file should have an extension
  File("foo.test") should haveExtension(".test")
}