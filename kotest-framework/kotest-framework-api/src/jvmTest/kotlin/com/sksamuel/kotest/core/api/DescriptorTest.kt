package com.sksamuel.kotest.core.api

import io.kotest.core.descriptors.append
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.matchers.shouldBe

class DescriptorTest : FunSpec({

   val spec = DescriptorTest::class.toDescriptor()
   val container = spec.append("a context")
   val test = container.append("nested test")

   test("isParentOf") {
      container.isParentOf(test) shouldBe true
      test.isParentOf(test) shouldBe false
      container.isParentOf(spec) shouldBe false
      spec.isParentOf(container) shouldBe true
   }

   test("isAncestorOf") {
      container.isAncestorOf(test) shouldBe true
      spec.isAncestorOf(test) shouldBe true
      container.isAncestorOf(spec) shouldBe false
      test.isAncestorOf(spec) shouldBe false
   }

   test("isDescendentOf") {
      container.isDescendentOf(test) shouldBe false
      spec.isDescendentOf(test) shouldBe false
      container.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(container) shouldBe true
   }
})
