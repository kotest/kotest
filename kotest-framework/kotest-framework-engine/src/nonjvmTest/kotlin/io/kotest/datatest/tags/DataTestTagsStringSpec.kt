package io.kotest.datatest.tags
// StringSpec does not support nested tests, so this file only contains root-level data tests
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class DataTestTagsStringSpec : StringSpec({

   // StringSpec only supports root-level tests, no nesting
   withData("test1", "test2") { // line 10 -> kotest.data.13
      1 + 1 shouldBe 2
   }
   withData("test3", "test4") { // line 13 -> kotest.data.13
      1 + 1 shouldBe 2
   }
})
