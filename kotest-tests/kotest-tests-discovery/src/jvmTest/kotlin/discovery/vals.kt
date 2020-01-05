package discovery

import io.kotest.core.specs.funSpec
import io.kotest.core.specs.stringSpec
import io.kotest.shouldBe

val tests1 = funSpec {
   test("foo") {
      1 + 1 shouldBe 2
   }
   test("bar") {
      "a" + "b" shouldBe "ab"
   }
}

val tests2 = stringSpec {
   "fizz" {
      1 + 1 shouldBe 2
   }
   "buzz" {
      "a" + "b" shouldBe "ab"
   }
}
