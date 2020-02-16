package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.lines
import java.io.File

class LinesTest : FunSpec({
   test("Arb.lines should generate a line from the file") {
      val file = File(javaClass.getResource("/lines.txt").file)
      Arb.lines(file).samples(RandomSource.Default).take(1000).map { it.value }.toSet() shouldBe
         setOf("a", "b", "c", "d", "e", "f")
   }
})
