package com.sksamuel.kotest.show

import io.kotest.assertions.show.show
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CollectionsShowTest : FunSpec({

   test("Show should support iterables") {
      listOf("a", "b", "c").asIterable().show().value shouldBe """["a", "b", "c"]"""
      arrayOf("a", "b", "c").asIterable().show().value shouldBe """["a", "b", "c"]"""
      sequenceOf("a", "b", "c").asIterable().show().value shouldBe """["a", "b", "c"]"""
   }

   test("Show should support lists") {
      listOf("a", "b", "c").show().value shouldBe """["a", "b", "c"]"""
   }
})
