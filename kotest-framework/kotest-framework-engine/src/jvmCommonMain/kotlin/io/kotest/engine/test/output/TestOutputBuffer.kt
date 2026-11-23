package io.kotest.engine.test.output

import io.kotest.engine.test.CapturedOutput
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Holds the bytes one test wrote to the standard streams, in the order they were written.
 *
 * Bounded by [maxBytes]: when full, the oldest writes are dropped and the output is marked truncated.
 * Lock-free, so threads writing for the same test never block each other.
 */
internal class TestOutputBuffer(private val maxBytes: Int) {

   private class Write(val stream: CapturedOutput.Stream, val bytes: ByteArray)

   private val writes = ConcurrentLinkedDeque<Write>()
   private val size = AtomicLong()
   private val truncated = AtomicBoolean(false)

   fun add(stream: CapturedOutput.Stream, bytes: ByteArray, off: Int, len: Int) {
      if (len <= 0) return
      // a single write larger than the limit keeps its newest maxBytes, rather than being evicted whole
      val kept = minOf(len, maxBytes)
      if (kept < len) truncated.set(true)
      writes.addLast(Write(stream, bytes.copyOfRange(off + len - kept, off + len)))
      size.addAndGet(kept.toLong())
      while (size.get() > maxBytes) {
         val evicted = writes.pollFirst() ?: break
         size.addAndGet(-evicted.bytes.size.toLong())
         truncated.set(true)
      }
   }

   /**
    * Decodes the buffered bytes, merging consecutive writes to the same stream before decoding
    * so that a multibyte character split across two writes is decoded intact.
    *
    * Called once the test has finished, so the writes it sees are all the test's own writes, apart from
    * any made by coroutines the test leaked, which may or may not be included.
    */
   fun toCapturedOutput(): CapturedOutput {
      val chunks = mutableListOf<CapturedOutput.Chunk>()
      var stream: CapturedOutput.Stream? = null
      val pending = ByteArrayOutputStream()
      fun flush() {
         val s = stream ?: return
         if (pending.size() > 0) chunks.add(CapturedOutput.Chunk(s, pending.toString(StandardStreamCapture.charset(s).name())))
         pending.reset()
      }
      writes.forEach { write ->
         if (write.stream != stream) {
            flush()
            stream = write.stream
         }
         pending.write(write.bytes)
      }
      flush()
      return CapturedOutput(chunks, truncated.get())
   }
}
