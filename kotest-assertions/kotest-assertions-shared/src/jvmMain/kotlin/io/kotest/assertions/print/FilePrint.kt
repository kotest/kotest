package io.kotest.assertions.print

import java.io.File

object FilePrint : Print<File> {
  override fun print(a: File): Printed = a.path.printed()
}
