package inspections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldBeBlank

class ShouldNotBeFalseExample : FunSpec({

   test("a string should be blank") {
      "".shouldBeBlank()
      "".isBlank() shouldNotBe false
   }
})
