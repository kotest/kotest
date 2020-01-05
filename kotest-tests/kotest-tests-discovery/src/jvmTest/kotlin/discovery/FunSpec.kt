package discovery

import io.kotest.core.specs.FunSpec
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.shouldBe

class MyFunSuite : FunSpec() {
   init {
      test("faz") {
         1 - 1 shouldBe 0
      }
      test("baz") {
         "abc".shouldHaveLength(3)
      }
   }
}
