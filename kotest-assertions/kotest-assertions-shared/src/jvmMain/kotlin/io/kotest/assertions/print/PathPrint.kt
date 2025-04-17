package io.kotest.assertions.print

import java.nio.file.Path

object PathPrint : Print<Path> {
   override fun print(a: Path, level: Int): Printed = a.toString().printed()
}
