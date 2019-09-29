package io.kotest.assertions

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Chunk
import com.github.difflib.patch.Delta
import com.github.difflib.patch.DeltaType
import kotlin.math.max

/**
 * Returns a formatted diff of the expected and actual input, unless there are no differences,
 * or the input is too small to bother with diffing, return it returns the input as is.
 */
actual fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int): Pair<String, String> {

  fun typeString(deltaType: DeltaType): String = when (deltaType) {
    DeltaType.CHANGE -> "Change"
    DeltaType.INSERT -> "Addition"
    DeltaType.DELETE -> "Deletion"
    DeltaType.EQUAL -> ""
  }

  fun diffs(lines: List<String>, deltas: List<Delta<String>>, chunker: (Delta<String>) -> Chunk<String>): String {
    return deltas.joinToString("\n\n") { delta ->
      val chunk = chunker(delta)
      // include a line before and after to give some context on deletes
      val snippet = lines.drop(max(chunk.position - 1, 0)).take(chunk.position + chunk.size()).joinToString("\n")
      "[${typeString(delta.type)} at line ${chunk.position}] $snippet"
    }
  }

  val useDiff = expected.lines().size >= minSizeForDiff
    && actual.lines().size >= minSizeForDiff &&
    System.getProperty("kotest.assertions.multi-line-diff") != "simple"

  return if (useDiff) {
    val patch = DiffUtils.diff(actual, expected)
    return if (patch.deltas.isEmpty()) Pair(expected, actual) else {
      Pair(diffs(expected.lines(), patch.deltas, { it.original }), diffs(actual.lines(), patch.deltas, { it.revised }))
    }
  } else Pair(expected, actual)

}
