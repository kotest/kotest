package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CollectionsPrintTest : FunSpec({

   test("print should support iterables") {
      listOf("a", "b", "c").asIterable().print().value shouldBe """["a", "b", "c"]"""
      arrayOf("a", "b", "c").asIterable().print().value shouldBe """["a", "b", "c"]"""
      sequenceOf("a", "b", "c").asIterable().print().value shouldBe """["a", "b", "c"]"""
   }

   test("print should support lists") {
      listOf("a", "b", "c").print().value shouldBe """["a", "b", "c"]"""
   }

   test("detect print for IntArray") {
      intArrayOf(2, 4).print().value shouldBe "[2, 4]"
   }

   test("detect print for DoubleArray") {
      doubleArrayOf(1.2, 3.4).print().value shouldBe "[1.2, 3.4]"
   }

   test("detect print for Array<String>") {
      arrayOf("asd", "gsd", "fjfh").print().value shouldBe """["asd", "gsd", "fjfh"]"""
   }

   test("print should limit items") {
      List(1000) { it }.print().value shouldBe "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, ...and 980 more (set 'kotest.assertions.collection.print.size' to see more / less items)]"
   }
})
