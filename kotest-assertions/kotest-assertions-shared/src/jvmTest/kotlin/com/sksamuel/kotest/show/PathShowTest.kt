package com.sksamuel.kotest.show

import io.kotest.assertions.show.show
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Paths

private val sep = File.separator

class PathShowTest : FunSpec({
   test("Show should support path") {
      Paths.get("/a/b/c").show().value shouldBe "${sep}a${sep}b${sep}c"
   }
})
