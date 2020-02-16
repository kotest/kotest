package com.sksamuel.kotest.matchers.throwable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.throwable.shouldHaveCause
import io.kotest.matchers.throwable.shouldHaveCauseInstanceOf
import io.kotest.matchers.throwable.shouldHaveCauseOfType
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.throwable.shouldNotHaveCause
import io.kotest.matchers.throwable.shouldNotHaveCauseInstanceOf
import io.kotest.matchers.throwable.shouldNotHaveCauseOfType
import io.kotest.matchers.throwable.shouldNotHaveMessage
import java.io.FileNotFoundException
import java.io.IOException

class ThrowableMatchersTest : FreeSpec() {
  init {
    "shouldThrowAny" - {
      "shouldHaveMessage" {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrowAny { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrowAny { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrowAny { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrowAny { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCause()
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" {
        shouldThrowAny { throw TestException() }.shouldNotHaveCause()
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseInstanceOf" {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseInstanceOf<FileNotFoundException>()
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseInstanceOf<IOException>()
      }
      "shouldNotHaveCauseInstanceOf" {
        shouldThrowAny { throw CompleteTestException() }.shouldNotHaveCauseInstanceOf<TestException>()
      }
      "shouldHaveCauseOfType" {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfType" {
        shouldThrowAny { throw CompleteTestException() }.shouldNotHaveCauseOfType<IOException>()
      }
    }
    "shouldThrow" - {
      "shouldHaveMessage" {
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrow<TestException> { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrow<CompleteTestException> { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" {
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrow<TestException> { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrow<CompleteTestException> { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause()
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" {
        shouldThrow<TestException> { throw TestException() }.shouldNotHaveCause()
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseInstanceOf" {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseInstanceOf<FileNotFoundException>()
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseInstanceOf<IOException>()
      }
      "shouldNotHaveCauseInstanceOf" {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseInstanceOf<TestException>()
      }
      "shouldHaveCauseOfType" {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfType" {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfType<IOException>()
      }
    }
    "shouldThrowExactly" - {
      "shouldHaveMessage" {
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrowExactly<TestException> { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" {
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrowExactly<TestException> { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause()
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" {
        shouldThrowExactly<TestException> { throw TestException() }.shouldNotHaveCause()
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseInstanceOf" {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseInstanceOf<FileNotFoundException>()
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseInstanceOf<IOException>()
      }
      "shouldNotHaveCauseInstanceOf" {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseInstanceOf<TestException>()
      }
      "shouldHaveCauseOfType" {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfType" {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfType<IOException>()
      }
    }
    "result" - {
      "shouldHaveMessage" {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!! shouldHaveMessage "this_file.txt not found"
        Result.failure<Any>(TestException()).exceptionOrNull()!! shouldHaveMessage "This is a test exception"
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!! shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!! shouldNotHaveMessage "random message"
        Result.failure<Any>(TestException()).exceptionOrNull()!! shouldNotHaveMessage "This is a complete test exception"
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!! shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCause()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" {
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldNotHaveCause()
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldNotHaveCause()
      }
      "shouldHaveCauseInstanceOf" {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseInstanceOf<FileNotFoundException>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseInstanceOf<IOException>()
      }
      "shouldNotHaveCauseInstanceOf" {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotHaveCauseInstanceOf<TestException>()
      }
      "shouldHaveCauseOfType" {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseOfType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfType" {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotHaveCauseOfType<IOException>()
      }
    }
  }


  class TestException : Throwable("This is a test exception")
  class CompleteTestException : Throwable("This is a complete test exception", FileNotFoundException("file.txt not found"))
}
