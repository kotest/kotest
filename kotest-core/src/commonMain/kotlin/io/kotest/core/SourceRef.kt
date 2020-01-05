package io.kotest.core

/**
 * Gives the position of a piece of source code via a line number and filename.
 */
data class SourceRef(val lineNumber: Int, val fileName: String)

/**
 * Returns a [SourceRef] for the current execution point.
 */
expect fun sourceRef(): SourceRef
