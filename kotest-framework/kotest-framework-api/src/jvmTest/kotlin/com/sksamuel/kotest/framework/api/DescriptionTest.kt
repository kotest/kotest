package com.sksamuel.kotest.framework.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.DisplayPath
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class DescriptionTest : FunSpec({

   val spec = DescriptionTest::class.toDescription()
   val container = spec.appendContainer(DescriptionName.TestName("a context"))
   val test = container.appendTest(DescriptionName.TestName("nested test"))
   val testWithPrefix = container.appendTest(DescriptionName.TestName("given", "nested test"))
   val rootTest = spec.appendTest(DescriptionName.TestName("root test"))

   test("displayPath should include the spec") {
      rootTest.testDisplayPath() shouldBe DisplayPath("root test")
      test.testDisplayPath() shouldBe DisplayPath("a context nested test")
      container.testDisplayPath() shouldBe DisplayPath("a context")
      spec.testDisplayPath() shouldBe DisplayPath("")
   }

   test("testDisplayPath should not include the spec") {
      rootTest.testDisplayPath() shouldBe DisplayPath("root test")
      test.testDisplayPath() shouldBe DisplayPath("a context nested test")
      container.testDisplayPath() shouldBe DisplayPath("a context")
      spec.testDisplayPath() shouldBe DisplayPath("")
   }

   test("testPath should not include the spec") {
      rootTest.testPath() shouldBe TestPath("root test")
      test.testPath() shouldBe TestPath("a context -- nested test")
      container.testPath() shouldBe TestPath("a context")
      spec.testPath() shouldBe TestPath("")
   }

   test("names should include the spec") {
      rootTest.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "com.sksamuel.kotest.framework.api.DescriptionTest"),
         DescriptionName.TestName("root test")
      )
      test.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "com.sksamuel.kotest.framework.api.DescriptionTest"),
         DescriptionName.TestName("a context"),
         DescriptionName.TestName("nested test")
      )
      container.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "com.sksamuel.kotest.framework.api.DescriptionTest"),
         DescriptionName.TestName("a context")
      )
      spec.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "com.sksamuel.kotest.framework.api.DescriptionTest")
      )
   }

   test("test names should not include the spec") {
      rootTest.testNames() shouldBe listOf(
         DescriptionName.TestName("root test")
      )
      test.testNames() shouldBe listOf(
         DescriptionName.TestName("a context"),
         DescriptionName.TestName("nested test")
      )
      container.testNames() shouldBe listOf(
         DescriptionName.TestName("a context")
      )
      spec.testNames() shouldBe emptyList()
   }

   test("append") {
      container.appendContainer(DescriptionName.TestName("foo")) shouldBe
         Description.TestDescription(
            container,
            DescriptionName.TestName("foo"),
            TestType.Container
         )
      test.appendTest(DescriptionName.TestName("foo")) shouldBe
         Description.TestDescription(
            test,
            DescriptionName.TestName("foo"),
            TestType.Test
         )
   }

   test("isParentOf") {
      container.isParentOf(test) shouldBe true
      test.isParentOf(test) shouldBe false
      container.isParentOf(spec) shouldBe false
      spec.isParentOf(container) shouldBe true
      spec.isParentOf(rootTest) shouldBe true
      container.isParentOf(rootTest) shouldBe false
   }

   test("isAncestorOf") {
      container.isAncestorOf(test) shouldBe true
      container.isAncestorOf(testWithPrefix) shouldBe true
      spec.isAncestorOf(test) shouldBe true
      spec.isAncestorOf(rootTest) shouldBe true
      spec.isAncestorOf(testWithPrefix) shouldBe true
      test.isAncestorOf(rootTest) shouldBe false
      container.isAncestorOf(rootTest) shouldBe false
      container.isAncestorOf(spec) shouldBe false
      test.isAncestorOf(spec) shouldBe false
      rootTest.isAncestorOf(spec) shouldBe false
   }

   test("isDescendentOf") {
      container.isDescendentOf(test) shouldBe false
      spec.isDescendentOf(test) shouldBe false
      spec.isDescendentOf(rootTest) shouldBe false
      test.isDescendentOf(rootTest) shouldBe false
      container.isDescendentOf(rootTest) shouldBe false
      container.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(spec) shouldBe true
      testWithPrefix.isDescendentOf(spec) shouldBe true
      test.isDescendentOf(container) shouldBe true
      testWithPrefix.isDescendentOf(container) shouldBe true
      rootTest.isDescendentOf(spec) shouldBe true
   }
})
