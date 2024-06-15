package io.kotest.assertions.print

import java.io.File

object FilePrint : Print<File> {
   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: File): Printed = a.path.printed()
}
