package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Paths

private val sep = File.separator

class PathShowTest : FunSpec({
   test("print should support path") {
      Paths.get("/a/b/c").print().value shouldBe "${sep}a${sep}b${sep}c"
   }
})
