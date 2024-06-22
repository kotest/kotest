package io.kotest.assertions.print

import java.nio.file.Path

object PathPrint : Print<Path> {
   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: Path): Printed = a.toString().printed()
}
