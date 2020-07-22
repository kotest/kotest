package io.kotest.extensions.locale

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestType
import java.util.Locale

/**
 * Replaces the default Locale
 *
 * This function replaces the current default locale with [locale], then executes [block] and finally
 * returns the original default locale to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default locale, and if you
 * change the locale while it was already changed, the result may be inconsistent.
 */
inline fun <reified T> withDefaultLocale(locale: Locale, block: () -> T): T {
   val previous = Locale.getDefault()
   Locale.setDefault(locale)

   try {
      return block()
   } finally {
      Locale.setDefault(previous)
   }
}

abstract class LocaleListener(private val locale: Locale) {

   private val originalLocale = Locale.getDefault()

   protected fun changeLocale() {
      Locale.setDefault(locale)
   }

   protected fun resetLocale() {
      Locale.setDefault(originalLocale)
   }

}

/**
 * Replaces the default Locale
 *
 * This listener replaces the current default locale with [locale], then executes the test and finally
 * returns the original default locale to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default locale, and if you
 * change the locale while it was already changed, the result may be inconsistent.
 */
class LocaleTestListener(locale: Locale) : LocaleListener(locale), TestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      changeLocale()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      resetLocale()
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.type == TestType.Container) changeLocale()
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Container) resetLocale()
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.type == TestType.Test) changeLocale()
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.type == TestType.Test) resetLocale()
   }

   override suspend fun beforeAny(testCase: TestCase) {
      changeLocale()
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      resetLocale()
   }
}

/**
 * Replaces the default Locale
 *
 * This listener replaces the current default locale with [locale], then executes the project and finally
 * returns the original default locale to it's place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default locale, and if you
 * change the locale while it was already changed, the result may be inconsistent.
 */
class LocaleProjectListener(newLocale: Locale) : LocaleListener(newLocale),
   ProjectListener {

   override suspend fun beforeProject() {
      changeLocale()
   }

   override suspend fun afterProject() {
      resetLocale()
   }
}
