package io.kotest.assertions.show

import java.io.File

object FileShow : Show<File> {
  override fun show(a: File): Printed = a.path.printed()
}