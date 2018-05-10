package com.sksamuel.kotlintest.matchers

import io.kotlintest.specs.FreeSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.shouldThrowAny
import io.kotlintest.shouldThrowExactly
import java.io.FileNotFoundException
import java.io.IOException

class ExceptionMatchersTest : FreeSpec() {

  init {
    "shouldThrow" - {
      "error if no exception throw" {
        try {
          shouldThrow<IllegalAccessException> {
            listOf(1, 2, 3)
          }
          throw RuntimeException("If we get here its a bug")
        } catch (ex: AssertionError) {
        }
      }
      "should test for correct exception" {
        try {
          shouldThrow<IllegalStateException> {
            throw IOException("bibble")
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: AssertionError) {
        }
      }
      "should support throwables" {
        shouldThrow<Throwable> {
          throw Throwable("bibble")
        }
        try {
          shouldThrow<Throwable> {
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: Throwable) {
        }
      }
      "return matched exception" {
        val e = shouldThrow<IllegalAccessException> {
          throw IllegalAccessException("bibble")
        }
        e.message shouldBe "bibble"
      }
      "should not force non-null when the call is nullable" {
        shouldThrow<Exception> {
          FakeObjectWithMethodWithNullableSignature.method()
        }
      }
    }
    "shouldThrowExactly" - {
      "should test for precise exception class" {
        shouldThrowExactly<IOException> {
          throw IOException("bibble")
        }
        try {
          shouldThrowExactly<IOException> {
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: AssertionError) {
        }
      }
      "error on subclass" {
        try {
          shouldThrowExactly<IOException> {
            throw FileNotFoundException("bibble")
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: AssertionError) {
        }
      }
    }
    "shouldThrowAny" - {
      "should test for any exception" {
        shouldThrowAny {
          throw IOException("bibble")
        }
      }
    }
  }
}

private object FakeObjectWithMethodWithNullableSignature {
  fun method(): Any? {
    throw Exception()
  }
}