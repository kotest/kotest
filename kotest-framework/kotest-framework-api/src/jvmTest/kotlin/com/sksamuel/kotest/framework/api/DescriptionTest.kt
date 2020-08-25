package com.sksamuel.kotest.framework.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.DisplayPath
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import io.kotest.matchers.shouldBe

class DescriptionTest : FunSpec({

   val spec = DescriptionTest::class.toDescription()
   val container = spec.appendContainer("a context")
   val test = container.appendTest("nested test")
   val testWithPrefix = container.appendTest(createTestName("given", "nested test", true))
   val rootTest = spec.appendTest("root test")

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
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "DescriptionTest",
            "com.sksamuel.kotest.framework.api.DescriptionTest"),
         createTestName("root test")
      )
      test.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "DescriptionTest",
            "com.sksamuel.kotest.framework.api.DescriptionTest"),
         createTestName("a context"),
         createTestName("nested test")
      )
      container.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "DescriptionTest",
            "com.sksamuel.kotest.framework.api.DescriptionTest"),
         createTestName("a context")
      )
      spec.names() shouldBe listOf(
         DescriptionName.SpecName("com.sksamuel.kotest.framework.api.DescriptionTest", "DescriptionTest",
            "com.sksamuel.kotest.framework.api.DescriptionTest")
      )
   }

   test("test names should not include the spec") {
      rootTest.testNames() shouldBe listOf(
         createTestName("root test")
      )
      test.testNames() shouldBe listOf(
         createTestName("a context"),
         createTestName("nested test")
      )
      container.testNames() shouldBe listOf(
         createTestName("a context")
      )
      spec.testNames() shouldBe emptyList()
   }

   test("append") {
      container.appendContainer(createTestName("foo")) shouldBe
         Description.Test(
            container,
            createTestName("foo"),
            TestType.Container
         )
      test.appendTest(createTestName("foo")) shouldBe
         Description.Test(
            test,
            createTestName("foo"),
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
