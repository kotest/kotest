package io.kotest.extensions.system

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * A [io.kotest.core.listeners.TestListener] that throws an error if anything is written to standard out.
 */
object NoSystemOutListener : TestListener {

   private val stream = object : PrintStream(ByteArrayOutputStream()) {
      private fun error(a: Any?): Nothing = throw SystemOutWriteException(a?.toString())
      override fun print(b: Boolean) = error(b)
      override fun print(c: Char) = error(c)
      override fun print(i: Int) = error(i)
      override fun print(l: Long) = error(l)
      override fun print(s: String?) = error(s)
      override fun print(o: Any?) = error(o)
      override fun print(c: CharArray) = error(c)
      override fun print(d: Double) = error(d)
      override fun print(f: Float) = error(f)
   }

   private val stdout = System.out

   override suspend fun beforeTest(testCase: TestCase) {
      System.setOut(stream)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      System.setOut(stdout)
   }
}

class SystemOutWriteException(val str: String?) : RuntimeException()
