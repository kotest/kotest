package io.kotest.engine.test.output

import io.kotest.common.KotestInternal
import io.kotest.engine.test.CapturedOutput
import io.kotest.engine.test.TestThreadState
import java.io.OutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Replaces `System.out` and `System.err` with streams that send each write to the buffer of the test
 * running on the writing thread (see [io.kotest.engine.test.CurrentTestCase]), and pass any other write
 * through to the stream they replaced.
 *
 * Capturing the standard streams, rather than integrating with a logging library, keeps the engine free of
 * logging dependencies and works for `println` and for any logger that writes to the console, provided that
 * logger looks up `System.out` when it writes. A logger that keeps a reference to the stream it saw when it
 * was configured (for example, Log4j 2's console appender unless `follow="true"`, or JUL's `ConsoleHandler`)
 * bypasses the capture if it was configured before the engine started.
 */
@KotestInternal
object StandardStreamCapture {

   private val streams = AtomicReference<Streams?>(null)
   private val warnedReplaced = AtomicBoolean(false)

   private class Streams(
      val originalOut: PrintStream,
      val originalErr: PrintStream,
      val capturingOut: PrintStream,
      val capturingErr: PrintStream,
   )

   internal val isInstalled: Boolean
      get() = streams.get() != null

   /**
    * The charset of a standard stream, as the JVM chose it for `System.out` and `System.err`.
    */
   internal fun charset(stream: CapturedOutput.Stream): Charset {
      val property = when (stream) {
         CapturedOutput.Stream.Out -> "stdout.encoding"
         CapturedOutput.Stream.Err -> "stderr.encoding"
      }
      return System.getProperty(property)?.let { runCatching { Charset.forName(it) }.getOrNull() }
         ?: Charset.defaultCharset()
   }

   /**
    * Installs the capturing streams, if they are not installed already. Called by the first test that wants its
    * output captured, so the streams are only replaced when some test needs them. Lock-free: if two tests race,
    * one installs and the other sees its streams.
    */
   internal fun ensureInstalled() {
      if (streams.get() != null) return
      val out = System.out
      val err = System.err
      val installed = Streams(
         originalOut = out,
         originalErr = err,
         capturingOut = capturing(CapturedOutput.Stream.Out, out),
         capturingErr = capturing(CapturedOutput.Stream.Err, err),
      )
      if (streams.compareAndSet(null, installed)) {
         warnedReplaced.set(false)
         System.setOut(installed.capturingOut)
         System.setErr(installed.capturingErr)
      }
   }

   /**
    * Runs [f], and afterwards restores the standard streams if they were installed while it ran.
    * Like `withDebugProbe`, an engine started while capture is already installed, such as an engine launched from
    * inside a test, leaves the existing capture in place.
    */
   internal suspend fun <T> withEngineScope(f: suspend () -> T): T {
      if (streams.get() != null) return f()
      try {
         return f()
      } finally {
         val installed = streams.getAndSet(null)
         // only restore a stream we still own: if something else replaced it, leave its replacement in place
         if (installed != null) {
            if (System.out === installed.capturingOut) System.setOut(installed.originalOut)
            if (System.err === installed.capturingErr) System.setErr(installed.originalErr)
         }
      }
   }

   /**
    * Prints a warning, once per install, if some library has replaced `System.out` or `System.err`,
    * since output written to the replacement is not captured.
    */
   internal fun warnIfReplaced() {
      val installed = streams.get() ?: return
      val out = System.out.takeIf { it !== installed.capturingOut }
      val err = System.err.takeIf { it !== installed.capturingErr }
      if ((out != null || err != null) && warnedReplaced.compareAndSet(false, true)) {
         val replaced = listOfNotNull(
            out?.let { "System.out (now ${it.javaClass.name})" },
            err?.let { "System.err (now ${it.javaClass.name})" },
         ).joinToString(" and ")
         installed.originalErr.println(
            "Kotest output capture: $replaced was replaced while tests were running, " +
               "so output written to it is not captured. " +
               "A library that installs its own console stream, such as Jansi, can cause this."
         )
      }
   }

   /**
    * Writes [output] to the streams that were in place before capture was installed, so that it is not
    * captured again. Falls back to the current streams when capture is not installed.
    */
   fun writeThrough(output: CapturedOutput) {
      val installed = streams.get()
      val out = installed?.originalOut ?: System.out
      val err = installed?.originalErr ?: System.err
      if (output.truncated) {
         err.println("[kotest: earlier output of this test was dropped because it exceeded the output capture limit]")
      }
      output.chunks.forEach { chunk ->
         val stream = if (chunk.stream == CapturedOutput.Stream.Out) out else err
         stream.print(chunk.text)
         stream.flush()
      }
   }

   private fun capturing(stream: CapturedOutput.Stream, original: PrintStream): PrintStream =
      PrintStream(RoutingOutputStream(stream, original), true, charset(stream).name())

   private class RoutingOutputStream(
      private val stream: CapturedOutput.Stream,
      private val original: PrintStream,
   ) : OutputStream() {

      override fun write(b: Int) {
         write(byteArrayOf(b.toByte()), 0, 1)
      }

      override fun write(b: ByteArray, off: Int, len: Int) {
         // a thread running a test coroutine (or engine code) is attributed by that; any other thread may belong
         // to a spec, when spec thread attribution is enabled
         val state = TestThreadState.current.get()
         val buffer = if (state != null) state.output else SpecThreadOwner.target()
         if (buffer == null) original.write(b, off, len) else buffer.add(stream, b, off, len)
      }

      override fun flush() {
         original.flush()
      }
   }
}
