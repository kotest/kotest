package io.kotest.extensions.locale

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestType
import java.util.TimeZone

/**
 * Replaces the default TimeZone
 *
 * This function replaces the current default timeZone with [timeZone], then executes [block] and finally
 * returns the original default timeZone to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default timeZone, and if you
 * change the timeZone while it was already changed, the result may be inconsistent.
 */
inline fun <reified T> withDefaultTimeZone(timeZone: TimeZone, block: () -> T): T {
   val previous = TimeZone.getDefault()

   TimeZone.setDefault(timeZone)

   try {
      return block()
   } finally {
      TimeZone.setDefault(previous)
   }
}

abstract class TimeZoneListener(private val timeZone: TimeZone) {

   private val originalTimeZone = TimeZone.getDefault()

   protected fun changeTimeZone() {
      TimeZone.setDefault(timeZone)
   }

   protected fun resetTimeZone() {
      TimeZone.setDefault(originalTimeZone)
   }
}

/**
 * Replaces the default TimeZone
 *
 * This function replaces the current default timezone with [timeZone], then executes the test and finally
 * returns the original default timezone to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default timezone, and if you
 * change the timezone while it was already changed, the result may be inconsistent.
 */
class TimeZoneTestListener(timeZone: TimeZone) : TimeZoneListener(timeZone), TestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      changeTimeZone()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      resetTimeZone()
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.type == TestType.Container) changeTimeZone()
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Container) resetTimeZone()
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.type == TestType.Test) changeTimeZone()
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Test) resetTimeZone()
   }

   override suspend fun beforeAny(testCase: TestCase) {
      changeTimeZone()
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      resetTimeZone()
   }
}

/**
 * Replaces the default TimeZone
 *
 * This function replaces the current default timezone with [timeZone], then executes the project and finally
 * returns the original default timezone to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default timezone, and if you
 * change the timezone while it was already changed, the result may be inconsistent.
 */
class TimeZoneProjectListener(timeZone: TimeZone) : TimeZoneListener(timeZone),
   ProjectListener {

   override suspend fun beforeProject() {
      changeTimeZone()
   }

   override suspend fun afterProject() {
      resetTimeZone()
   }
}
