package com.sksamuel.kotest.engine

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvalidTestNameTest : FunSpec({

   test("empty test name should error") {
      shouldThrowAny {
         EmptyTestName()
      }.message.shouldBe("Cannot create test with blank or empty name")
   }

   test("blank test name should error") {
      shouldThrowAny {
         BlankTestName()
      }.message.shouldBe("Cannot create test with blank or empty name")
   }
})

private class EmptyTestName : FunSpec() {
   init {
      test("") {

      }
   }
}


private class BlankTestName : FunSpec() {
   init {
      test("  ") {

      }
   }
}
