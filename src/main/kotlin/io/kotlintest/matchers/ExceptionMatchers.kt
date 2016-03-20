package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import kotlin.reflect.KClass

interface ExceptionMatchers {

  fun expecting(kclass: KClass<*>, thunk: () -> Any): Unit {
    try {
      thunk()
      throw TestFailedException("Expected exception $kclass but no exception was thrown")
    } catch (exception: Exception) {
      if (exception.javaClass != kclass.java) {
        throw TestFailedException("Expected exception $kclass but $exception was thrown")
      }
    }
  }

}