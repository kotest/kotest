package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.lines
import java.io.File

@EnabledIf(LinuxOnlyGithubCondition::class)
class LinesTest : FunSpec({
   test("Exhaustive.lines should generate a line from the file") {
      val file = File(javaClass.getResource("/lines.txt").file)
      Exhaustive.lines(file).values.toList() shouldBe listOf("a", "b", "c", "d", "e", "f")
   }
})
