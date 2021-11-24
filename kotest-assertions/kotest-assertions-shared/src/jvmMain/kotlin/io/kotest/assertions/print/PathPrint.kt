package io.kotest.assertions.print

import java.nio.file.Path

object PathPrint : Print<Path> {
   override fun print(a: Path): Printed = a.toString().printed()
}
