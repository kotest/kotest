package com.sksamuel.kotest.show

import io.kotest.assertions.show.show
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class PathShowTest : FunSpec({
   test("Show should support path") {
      Paths.get("/a/b/c").show().value shouldBe "/a/b/c"
   }
})
