package io.kotest.extensions.locale

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.TestListener
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

abstract class TimeZoneListener(private val timeZone: TimeZone) : TestListener {
  
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
class TimeZoneTestListener(timeZone: TimeZone) : TimeZoneListener(timeZone) {
  
  override fun beforeTest(testCase: TestCase) {
    changeTimeZone()
  }
  
  override fun afterTest(testCase: TestCase, result: TestResult) {
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
class TimeZoneProjectListener(timeZone: TimeZone) : TimeZoneListener(timeZone) {
  
  override fun beforeProject() {
    changeTimeZone()
  }
  
  override fun afterProject() {
    resetTimeZone()
  }
}
