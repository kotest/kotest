package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import kotlin.reflect.KClass

interface ExceptionMatchers {

  fun expecting(kclass: KClass<*>, thunk: () -> Any): Unit {
    val exception = try {
      thunk()
      null
    } catch (exception: Exception) {
      exception
    }
    if (exception == null)
      throw TestFailedException("Expected exception $kclass but no exception was thrown")
    else if (exception.javaClass != kclass.java) {
      throw TestFailedException("Expected exception $kclass but $exception was thrown")
    }
  }
}