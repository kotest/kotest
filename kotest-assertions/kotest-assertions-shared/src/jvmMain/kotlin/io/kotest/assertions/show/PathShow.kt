package io.kotest.assertions.show

import java.nio.file.Path

object PathShow : Show<Path> {
   override fun show(a: Path): Printed = a.toString().printed()
}
