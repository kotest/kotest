package io.kotest.matchers.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class ShouldCompleteWithinTest : FunSpec({

   test("shouldCompleteWithin should not swallow threads #4892") {
      shouldCompleteWithin(5.seconds) {
         1 shouldBe 2
      }
   }

})
