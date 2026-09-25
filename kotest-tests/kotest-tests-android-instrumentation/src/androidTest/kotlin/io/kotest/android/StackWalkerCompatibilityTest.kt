package io.kotest.android

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class StackWalkerCompatibilityTest : FunSpec({

   test("ordinary tests can be registered on Android") {
      true shouldBe true
   }

   withData(1, 2) { value ->
      value shouldBe value
   }
})
