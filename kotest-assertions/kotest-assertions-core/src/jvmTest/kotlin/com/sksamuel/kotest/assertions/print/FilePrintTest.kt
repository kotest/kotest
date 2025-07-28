package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

private val sep = File.separator

class FileShowTest : FunSpec({
  test("Show should support File") {
    File("/a/b/c").print().value shouldBe "${sep}a${sep}b${sep}c"
  }
})
