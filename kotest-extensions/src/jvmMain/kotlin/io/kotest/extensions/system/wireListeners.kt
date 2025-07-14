package io.kotest.extensions.system

import io.kotest.common.KotestInternal
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

/**
 * A wrapper function that captures any writes to standard out.
 */
inline fun captureStandardOut(fn: () -> Unit): String {
   val previous = System.out
   val buffer = ByteArrayOutputStream()
   previous.flush()
   System.setOut(PrintStream(buffer))
   try {
      fn()
      System.out.flush()
      return buffer.asCanonicalString()
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
   previous.flush()
   System.setErr(PrintStream(buffer))
   try {
      fn()
      System.err.flush()
      return buffer.asCanonicalString()
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

   fun output(): String = buffer.asCanonicalString()

   override suspend fun beforeAny(testCase: TestCase) {
      buffer = ByteArrayOutputStream()
      previous = System.out
      previous.flush()
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

   fun output(): String = buffer.asCanonicalString()

   override suspend fun beforeAny(testCase: TestCase) {
      buffer = ByteArrayOutputStream()
      previous = System.err
      previous.flush()
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

/**
 * Write to multiple output streams at once
 *
 * (avoids pulling in commons-io:commons-io just for a single class)
 */
internal class TeeOutputStream(
   private val firstOutput: OutputStream,
   private val secondOutput: OutputStream
) : OutputStream() {

   override fun write(data: Int) = synchronized(this) {
      firstOutput.write(data)
      secondOutput.write(data)
   }

   override fun write(data: ByteArray) = synchronized(this) {
      firstOutput.write(data)
      secondOutput.write(data)
   }

   override fun write(data: ByteArray, offset: Int, length: Int) = synchronized(this) {
      firstOutput.write(data, offset, length)
      secondOutput.write(data, offset, length)
   }

   override fun flush() {
      firstOutput.flush()
      secondOutput.flush()
   }

   override fun close() {
      firstOutput.close()
      secondOutput.close()
   }
}

/** Returns the stream's content as a UTF-8 string with normalized line breaks. */
@KotestInternal
fun ByteArrayOutputStream.asCanonicalString(): String = String(toByteArray()).replace("\r\n", "\n")
