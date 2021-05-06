package io.kotest.core

import io.kotest.core.plan.Source

data class SourceRef(val lineNumber: Int, val fileName: String)

/**
 * Returns a [SourceRef] for the current execution point.
 */
expect fun sourceRef(): SourceRef

fun source(): Source {
   val ref = sourceRef()
   return if (ref.lineNumber < 1) Source.File(ref.fileName) else Source.Line(ref.fileName, ref.lineNumber)
}
