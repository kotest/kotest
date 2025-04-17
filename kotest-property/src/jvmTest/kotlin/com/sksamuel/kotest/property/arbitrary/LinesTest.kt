package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.lines
import java.io.File

@EnabledIf(LinuxOnlyGithubCondition::class)
class LinesTest : FunSpec({
   test("Arb.lines should generate a line from the file") {
      val file = File(javaClass.getResource("/lines.txt").file)
      Arb.lines(file).generate(RandomSource.default()).take(1000).map { it.value }.toSet() shouldBe
         setOf("a", "b", "c", "d", "e", "f")
   }
})
