package com.sksamuel.kotlintest.matchers.throwable

import io.kotlintest.matchers.throwable.*
import io.kotlintest.shouldThrow
import io.kotlintest.shouldThrowAny
import io.kotlintest.shouldThrowExactly
import io.kotlintest.specs.FreeSpec
import java.io.FileNotFoundException
import java.io.IOException
import java.io.WriteAbortedException

class ThrowableMatchersTest : FreeSpec() {
  init {
    "shouldThrowAny" - {
      "shouldBeOfType" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldBeOfType<FileNotFoundException>()
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldBeOfType<IOException>()
        shouldThrowAny { throw TestException() }.shouldBeOfType<TestException>()
        shouldThrowAny { throw CompleteTestException() }.shouldBeOfType<CompleteTestException>()
      }
      "shouldNotBeOfType" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldNotBeOfType<WriteAbortedException>()
        shouldThrowAny { throw TestException() }.shouldNotBeOfType<FileNotFoundException>()
        shouldThrowAny { throw CompleteTestException() }.shouldNotBeOfType<TestException>()
      }
      "shouldBeExactlyOfType" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldBeExactlyOfType<FileNotFoundException>()
        shouldThrowAny { throw TestException() }.shouldBeExactlyOfType<TestException>()
        shouldThrowAny { throw CompleteTestException() }.shouldBeExactlyOfType<CompleteTestException>()
      }
      "shouldNotBeExactlyOfType" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldNotBeExactlyOfType<IOException>()
        shouldThrowAny { throw TestException() }.shouldNotBeExactlyOfType<Throwable>()
        shouldThrowAny { throw CompleteTestException() }.shouldNotBeExactlyOfType<Throwable>()
      }
      "shouldHaveMessage" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrowAny { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrowAny { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" - {
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrowAny { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrowAny { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" - {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCause()
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" - {
        shouldThrowAny { throw TestException() }.shouldNotHaveCause()
        shouldThrowAny { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseOfType" - {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseOfType<IOException>()
      }
      "shouldNotHaveCauseOfType" - {
        shouldThrowAny { throw CompleteTestException() }.shouldNotHaveCauseOfType<TestException>()
      }
      "shouldHaveCauseOfExacltyType" - {
        shouldThrowAny { throw CompleteTestException() }.shouldHaveCauseOfExacltyType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfExacltyType" - {
        shouldThrowAny { throw CompleteTestException() }.shouldNotHaveCauseOfExacltyType<IOException>()
      }
    }
    "shouldThrow" - {
      "shouldHaveMessage" - {
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrow<TestException> { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrow<CompleteTestException> { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" - {
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrow<TestException> { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrow<CompleteTestException> { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" - {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause()
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" - {
        shouldThrow<TestException> { throw TestException() }.shouldNotHaveCause()
        shouldThrow<IOException> { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseOfType" - {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<IOException>()
      }
      "shouldNotHaveCauseOfType" - {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfType<TestException>()
      }
      "shouldHaveCauseOfExacltyType" - {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfExacltyType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfExacltyType" - {
        shouldThrow<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfExacltyType<IOException>()
      }
    }
    "shouldThrowExactly" - {
      "shouldHaveMessage" - {
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") } shouldHaveMessage "this_file.txt not found"
        shouldThrowExactly<TestException> { throw TestException() } shouldHaveMessage "This is a test exception"
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() } shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" - {
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") } shouldNotHaveMessage "random message"
        shouldThrowExactly<TestException> { throw TestException() } shouldNotHaveMessage "This is a complete test exception"
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() } shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" - {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause()
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" - {
        shouldThrowExactly<TestException> { throw TestException() }.shouldNotHaveCause()
        shouldThrowExactly<FileNotFoundException> { throw FileNotFoundException("this_file.txt not found") }.shouldNotHaveCause()
      }
      "shouldHaveCauseOfType" - {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<FileNotFoundException>()
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfType<IOException>()
      }
      "shouldNotHaveCauseOfType" - {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfType<TestException>()
      }
      "shouldHaveCauseOfExacltyType" - {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldHaveCauseOfExacltyType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfExacltyType" - {
        shouldThrowExactly<CompleteTestException> { throw CompleteTestException() }.shouldNotHaveCauseOfExacltyType<IOException>()
      }
    }
    "result" - {
      "shouldBeOfType" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldBeOfType<FileNotFoundException>()
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldBeOfType<IOException>()
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldBeOfType<TestException>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldBeOfType<CompleteTestException>()
      }
      "shouldNotBeOfType" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldNotBeOfType<WriteAbortedException>()
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldNotBeOfType<FileNotFoundException>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotBeOfType<TestException>()
      }
      "shouldBeExactlyOfType" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldBeExactlyOfType<FileNotFoundException>()
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldBeExactlyOfType<TestException>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldBeExactlyOfType<CompleteTestException>()
      }
      "shouldNotBeExactlyOfType" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldNotBeExactlyOfType<IOException>()
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldNotBeExactlyOfType<Throwable>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotBeExactlyOfType<Throwable>()
      }
      "shouldHaveMessage" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!! shouldHaveMessage "this_file.txt not found"
        Result.failure<Any>(TestException()).exceptionOrNull()!! shouldHaveMessage "This is a test exception"
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!! shouldHaveMessage "This is a complete test exception"
      }
      "shouldNotHaveMessage" - {
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!! shouldNotHaveMessage "random message"
        Result.failure<Any>(TestException()).exceptionOrNull()!! shouldNotHaveMessage "This is a complete test exception"
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!! shouldNotHaveMessage "This is a test exception"
      }
      "shouldHaveCause" - {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCause()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCause {
          it shouldHaveMessage "file.txt not found"
        }
      }
      "shouldNotHaveCause" - {
        Result.failure<Any>(TestException()).exceptionOrNull()!!.shouldNotHaveCause()
        Result.failure<Any>(FileNotFoundException("this_file.txt not found")).exceptionOrNull()!!.shouldNotHaveCause()
      }
      "shouldHaveCauseOfType" - {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseOfType<FileNotFoundException>()
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseOfType<IOException>()
      }
      "shouldNotHaveCauseOfType" - {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotHaveCauseOfType<TestException>()
      }
      "shouldHaveCauseOfExacltyType" - {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldHaveCauseOfExacltyType<FileNotFoundException>()
      }
      "shouldNotHaveCauseOfExacltyType" - {
        Result.failure<Any>(CompleteTestException()).exceptionOrNull()!!.shouldNotHaveCauseOfExacltyType<IOException>()
      }
    }
  }


  class TestException : Throwable("This is a test exception")
  class CompleteTestException : Throwable("This is a complete test exception", FileNotFoundException("file.txt not found"))
}