package io.kotest.assertions

import com.github.difflib.DiffUtils
import com.github.difflib.algorithm.DiffAlgorithmListener
import com.github.difflib.patch.AbstractDelta
import com.github.difflib.patch.Chunk
import com.github.difflib.patch.DeltaType
import kotlin.math.max

/**
 * Returns a formatted diff of the expected and actual input, unless there are no differences,
 * or the input is too small to bother with diffing, return it returns the input as is.
 */
actual fun diffLargeString(expected: String, actual: String): Pair<String, String>? {

   fun typeString(deltaType: DeltaType): String = when (deltaType) {
      DeltaType.CHANGE -> "Change"
      DeltaType.INSERT -> "Addition"
      DeltaType.DELETE -> "Deletion"
      DeltaType.EQUAL -> ""
   }

   fun diffs(
      lines: List<String>,
      deltas: MutableList<AbstractDelta<String>>,
      chunker: (AbstractDelta<String>) -> Chunk<String>
   ): String {
      return deltas.joinToString("\n\n") { delta ->
         val chunk = chunker(delta)
         // include a line before and after to give some context on deletes
         val snippet = lines.drop(max(chunk.position - 1, 0)).take(chunk.position + chunk.size()).joinToString("\n")
         "[${typeString(delta.type)} at line ${chunk.position}] $snippet"
      }
   }


   val patch = DiffUtils.diff(actual, expected, object : DiffAlgorithmListener {
      override fun diffStart() {}
      override fun diffStep(value: Int, max: Int) {}
      override fun diffEnd() {}
   })

   return if (patch.deltas.isEmpty()) Pair(expected, actual) else {
      Pair(diffs(expected.lines(), patch.deltas) { it.source }, diffs(actual.lines(), patch.deltas) { it.target })
   }
}
