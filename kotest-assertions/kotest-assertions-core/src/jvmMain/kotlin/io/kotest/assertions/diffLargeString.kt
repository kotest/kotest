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

   fun typeString(deltaType: com.github.difflib.patch.DeltaType): String = when (deltaType) {
      com.github.difflib.patch.DeltaType.CHANGE -> "Change"
      com.github.difflib.patch.DeltaType.INSERT -> "Addition"
      com.github.difflib.patch.DeltaType.DELETE -> "Deletion"
      com.github.difflib.patch.DeltaType.EQUAL -> ""
   }

   fun diffs(
      lines: List<String>,
      deltas: MutableList<com.github.difflib.patch.AbstractDelta<String>>,
      chunker: (com.github.difflib.patch.AbstractDelta<String>) -> com.github.difflib.patch.Chunk<String>
   ): String {
      return deltas.joinToString("\n\n") { delta ->
         val chunk = chunker(delta)
         // include a line before and after to give some context on deletes
         val snippet = lines.drop(max(chunk.position - 1, 0)).take(chunk.position + chunk.size()).joinToString("\n")
         "[${typeString(delta.type)} at line ${chunk.position}] $snippet"
      }
   }


   val patch = com.github.difflib.DiffUtils.diff(actual, expected, object :
      com.github.difflib.algorithm.DiffAlgorithmListener {
      override fun diffStart() {}
      override fun diffStep(value: Int, max: Int) {}
      override fun diffEnd() {}
   })

   return if (patch.deltas.isEmpty()) Pair(expected, actual) else {
      Pair(diffs(expected.lines(), patch.deltas) { it.source }, diffs(actual.lines(), patch.deltas) { it.target })
   }
}
