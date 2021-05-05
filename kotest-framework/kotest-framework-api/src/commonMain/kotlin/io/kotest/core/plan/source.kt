package io.kotest.core.plan

/**
 * An ADT that describes where a spec or test is defined.
 */
sealed class Source {

   /**
    * Links only to a file.
    */
   data class File(val filename: String) : Source()

   /**
    * Links to a file and line number.
    */
   data class Line(val filename: String, val lineNumber: Int) : Source()
}
