package io.kotlintest

data class SourceRef(val lineNumber: Int, val fileName: String)

/**
 * Returns a [SourceRef] for the current execution point.
 */
expect fun sourceRef(): SourceRef
