package io.kotest.engine.test

/**
 * The output a single test wrote to the standard streams while
 * [io.kotest.core.config.AbstractProjectConfig.outputCapture] was enabled.
 *
 * @param chunks the output in the order it was written, with consecutive writes to the same stream merged.
 * @param truncated true if the test wrote more than the configured limit, so its oldest output was dropped.
 */
data class CapturedOutput(
   val chunks: List<Chunk>,
   val truncated: Boolean,
) {

   enum class Stream { Out, Err }

   data class Chunk(val stream: Stream, val text: String)

   /**
    * Everything written to `System.out`.
    */
   val stdout: String
      get() = chunks.filter { it.stream == Stream.Out }.joinToString("") { it.text }

   /**
    * Everything written to `System.err`.
    */
   val stderr: String
      get() = chunks.filter { it.stream == Stream.Err }.joinToString("") { it.text }

   fun isEmpty(): Boolean = chunks.isEmpty()
}
