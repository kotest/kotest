package io.kotest.core.source

sealed interface SourceRef {

   /**
    * No source information was information. For example, on platforms where
    * reflective capabiltities don't exist.
    */
   object None : SourceRef

   /**
    * A [SourceRef] for a line number in a class.
    */
   data class ClassSource(val fqn: String, val lineNumber: Int?) : SourceRef
}

/**
 * Returns a [SourceRef] for the current execution point.
 */
internal expect fun sourceRef(): SourceRef
