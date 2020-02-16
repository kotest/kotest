package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.lines
import java.io.File

class LinesTest : FunSpec({
   test("Exhaustive.lines should generate a line from the file") {
      val file = File(javaClass.getResource("/lines.txt").file)
      Exhaustive.lines(file).values.toList() shouldBe listOf("a", "b", "c", "d", "e", "f")
   }
})
