package io.kotest.core

sealed interface SourceRef {

   /**
    * No source information was information. For example, on platforms where
    * reflective capabiltities don't exist.
    */
   object None : SourceRef

   /**
    * A [SourceRef] for when only the filename is known.
    */
   data class FileSource(val fileName: String) : SourceRef
   data class ClassSource(val fqn: String) : SourceRef

   /**
    * A [SourceRef] for when the line number and the filename is known.
    */
   data class FileLineSource(val fileName: String, val lineNumber: Int) : SourceRef
   data class ClassLineSource(val fqn: String, val lineNumber: Int) : SourceRef
}

/**
 * Returns a [SourceRef] for the current execution point.
 */
expect fun sourceRef(): SourceRef
