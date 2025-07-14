package io.kotest.extensions.locale

import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
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
 * returns the original default locale to its place.
 *
 * **Attention:** This code is subject to race conditions. The System can only have one default locale, and if you
 * change the locale while it was already changed, the result may be inconsistent.
 */
class LocaleTestListener(locale: Locale) : LocaleListener(locale), TestListener {

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
 * returns the original default locale to its place.
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
