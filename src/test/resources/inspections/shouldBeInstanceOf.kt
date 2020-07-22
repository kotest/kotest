package inspections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ShouldBeInstanceOfExample : FunSpec({

   test("foo") {
      val a: Number = 1
      (a is Long) shouldBe true
   }
})
