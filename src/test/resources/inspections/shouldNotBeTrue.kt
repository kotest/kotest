package inspections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank

class ShouldNotBeTrueExample : FunSpec({

   test("a string should not be blank") {
      "qweqe".shouldNotBeBlank()
      "sdfdsf".isBlank() shouldNotBe true
   }
})
