package io.kotest.core.plan

/**
 * An ADT that defines the source where a [Descriptor] is defined.
 */
sealed class Source {

   object Unknown : Source()

   /**
    * A class file source.
    */
   data class FileSource(val filename: String) : Source()

   /**
    * A file source with specified line number.
    */
   data class FileAndLineSource(val filename: String, val lineNumber: Int) : Source()
}

fun Source?.fileNameOrUnknown() = when (this) {
   is Source.FileSource -> filename
   is Source.FileAndLineSource -> filename
   else -> "<none>"
}

fun Source?.lineNumberOrDefault(): Int = when (this) {
   is Source.FileAndLineSource -> lineNumber
   else -> 0
}
