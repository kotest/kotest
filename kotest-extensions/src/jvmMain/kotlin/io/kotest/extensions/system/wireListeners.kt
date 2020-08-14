package io.kotest.extensions.system

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * A wrapper function that captures any writes to standard out.
 */
inline fun captureStandardOut(fn: () -> Unit): String {
   val previous = System.out
   val buffer = ByteArrayOutputStream()
   System.setOut(PrintStream(buffer))
   try {
      fn()
      return String(buffer.toByteArray())
   } finally {
      System.setOut(previous)
   }
}

/**
 * A wrapper function that captures any writes to standard error.
 */
inline fun captureStandardErr(fn: () -> Unit): String {
   val previous = System.err
   val buffer = ByteArrayOutputStream()
   System.setErr(PrintStream(buffer))
   try {
      fn()
      return String(buffer.toByteArray())
   } finally {
      System.setErr(previous)
   }
}

/**
 * A Kotest listener that facilities testing writes to standard out,
 * by redirecting any data written to standard out to an internal buffer.
 *
 * Users can query the written data by fetching the buffer by invoking [output].
 *
 * @param tee If true then any data written to standard out will be captured as well as written out.
 *            If false then the data written will be captured only.
 */
class SystemOutWireListener(private val tee: Boolean = true) : TestListener {

   private var buffer = ByteArrayOutputStream()
   private var previous = System.out

   fun output(): String = String(buffer.toByteArray())

   override suspend fun beforeAny(testCase: TestCase) {
      buffer = ByteArrayOutputStream()
      previous = System.out
      if (tee) {
         System.setOut(PrintStream(TeeOutputStream(previous, buffer)))
      } else {
         System.setOut(PrintStream(buffer))
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      System.setOut(previous)
   }
}

/**
 * A Kotest listener that facilities testing writes to standard err,
 * by copying any data written to standard err to an internal buffer.
 *
 * Users can query the written data by fetching the buffer by invoking [output].
 */
class SystemErrWireListener(private val tee: Boolean = true) : TestListener {

   private var buffer = ByteArrayOutputStream()
   private var previous = System.err

   fun output(): String = String(buffer.toByteArray())

   override suspend fun beforeAny(testCase: TestCase) {
      buffer = ByteArrayOutputStream()
      previous = System.err
      if (tee) {
         System.setErr(PrintStream(TeeOutputStream(previous, buffer)))
      } else {
         System.setErr(PrintStream(buffer))
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      System.setErr(previous)
   }
}
