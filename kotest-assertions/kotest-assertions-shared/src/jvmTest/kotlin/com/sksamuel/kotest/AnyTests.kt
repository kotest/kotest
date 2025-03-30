package com.sksamuel.kotest

import io.kotest.assertions.any
import io.kotest.assertions.shouldFail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@EnabledIf(LinuxOnlyGithubCondition::class)
class AnyTests : FunSpec({
   test("any succeeds as long as a single assertion succeeds") {
      any {
         1 shouldBe 2
         10 shouldBe 10
      }
   }

   test("any fails if all assertions fail") {
      val message = shouldFail {
         any {
            1 shouldBe 2
            2 shouldBe 3
         }
      }.message

      message shouldContain "The following 3 assertions failed"
      message shouldContain "expected:<2> but was:<1>"
      message shouldContain "expected:<3> but was:<2>"
      message shouldContain "Any expected at least one assertion to succeed but they all failed"
   }
})
