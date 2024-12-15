package io.kotest.core.source

sealed interface SourceRef {

   /**
    * No source information was information. For example, on platforms where
    * reflective capabiltities don't exist.
    */
   object None : SourceRef

   /**
    * A [SourceRef] for the line number and filename.
    */
   data class FileSource(val fileName: String, val lineNumber: Int?) : SourceRef

   /**
    * A [SourceRef] for the line number and the fully qualified class.
    */
   data class ClassSource(val fqn: String, val lineNumber: Int?) : SourceRef
}

/**
 * Returns a [SourceRef] for the current execution point.
 */
expect fun sourceRef(): SourceRef
