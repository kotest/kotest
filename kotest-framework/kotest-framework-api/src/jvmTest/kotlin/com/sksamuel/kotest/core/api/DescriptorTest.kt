package com.sksamuel.kotest.core.api

import io.kotest.core.descriptors.TestPath
import io.kotest.core.descriptors.append
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DescriptorTest : FunSpec({

   val spec = DescriptorTest::class.toDescriptor()
   val container = spec.append("a context")
   val test = container.append("nested test")
   val nestedContainer = test.append("a context")
   val deepTest = nestedContainer.append("nested test")

   test("isTestCase") {
      spec.isTestCase() shouldBe false
      container.isTestCase() shouldBe true
      test.isTestCase() shouldBe true
   }

   test("isRoot") {
      spec.isRootTest() shouldBe false
      container.isRootTest() shouldBe true
      test.isRootTest() shouldBe false
   }

   test("isChildOf") {
      container.isChildOf(test) shouldBe false
      test.isChildOf(test) shouldBe false
      test.isChildOf(test) shouldBe false
      container.isChildOf(spec) shouldBe true
      spec.isChildOf(container) shouldBe false
      deepTest.isChildOf(container) shouldBe false
      test.isChildOf(nestedContainer) shouldBe false
   }

   test("isParentOf") {
      container.isParentOf(test) shouldBe true
      container.isParentOf(container) shouldBe false
      test.isParentOf(container) shouldBe false
      test.isParentOf(test) shouldBe false
      container.isParentOf(spec) shouldBe false
      spec.isParentOf(container) shouldBe true
      container.isParentOf(deepTest) shouldBe false
      nestedContainer.isParentOf(test) shouldBe false
   }

   test("isAncestorOf") {
      container.isAncestorOf(test) shouldBe true
      spec.isAncestorOf(test) shouldBe true
      container.isAncestorOf(spec) shouldBe false
      test.isAncestorOf(spec) shouldBe false
      container.isAncestorOf(nestedContainer) shouldBe true
      container.isAncestorOf(deepTest) shouldBe true
   }

   test("isDescendentOf") {
      container.isDescendentOf(test) shouldBe false
      spec.isDescendentOf(test) shouldBe false
      container.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(container) shouldBe true
   }

   test("isOnPath") {
      container.isOnPath(test) shouldBe true
      test.isOnPath(container) shouldBe false
      spec.isOnPath(test) shouldBe true
      test.isOnPath(spec) shouldBe false
      container.isOnPath(spec) shouldBe false
      spec.isOnPath(container) shouldBe true
      spec.isOnPath(spec) shouldBe true
      test.isOnPath(test) shouldBe true
      container.isOnPath(container) shouldBe true
   }

   test("path") {

      spec.path(true) shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest")
      container.path(true) shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest/a context")
      test.path(true) shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest/a context -- nested test")

      container.path(false) shouldBe TestPath("a context")
      test.path(false) shouldBe TestPath("a context -- nested test")
   }
})
