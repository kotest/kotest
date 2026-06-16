package com.sksamuel.kotest.assertions.eq

import io.kotest.assertions.eq.isOrderedSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IsOrderedSetTest : FunSpec() {
   init {
      test("supports LinkedHashSet") {
         isOrderedSet(java.util.LinkedHashSet(listOf(1, 2, 3))) shouldBe true
      }
      test("supports SortedSet") {
         isOrderedSet(sortedSetOf(1, 2, 3)) shouldBe true
      }
      test("supports empty set") {
         isOrderedSet(emptySet<Int>()) shouldBe true
      }
      test("fails for list") {
         isOrderedSet(listOf(1, 2, 3)) shouldBe false
      }
   }
}
