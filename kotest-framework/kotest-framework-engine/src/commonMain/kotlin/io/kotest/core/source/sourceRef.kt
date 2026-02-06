package io.kotest.core.source

sealed interface SourceRef {

   /**
    * Source information was not available. For example, on platforms where
    * stack traces are not generated.
    */
   object None : SourceRef

   /**
    * A [SourceRef] for a fully qualified class name.
    */
   data class ClassSource(val fqn: String) : SourceRef

   /**
    * A [SourceRef] for a line number in a fully qualified class name.
    */
   data class ClassLineSource(val fqn: String, val lineNumber: Int?) : SourceRef
}

/**
 * Returns a [SourceRef] for the current execution point.
 */
internal expect fun sourceRef(): SourceRef
