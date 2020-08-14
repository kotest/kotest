package com.sksamuel.kotest

import io.kotest.engine.spec.AbstractSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestName
import io.kotest.engine.test.toDescription
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class DescriptionTest : FunSpec({

   val spec = DescriptionTest::class.toDescription()
   val container = spec.appendContainer(TestName("a context"))
   val test = container.appendTest(TestName("nested test"))
   val testWithPrefix = container.appendTest(TestName("given", "nested test"))
   val rootTest = spec.appendTest(TestName("root test"))

   test("displayPath") {
      rootTest.displayPath() shouldBe "com.sksamuel.kotest.DescriptionTest root test"
      test.displayPath() shouldBe "com.sksamuel.kotest.DescriptionTest a context nested test"
      container.displayPath() shouldBe "com.sksamuel.kotest.DescriptionTest a context"
      spec.displayPath() shouldBe "com.sksamuel.kotest.DescriptionTest"

      rootTest.displayPath(false) shouldBe "root test"
      test.displayPath(false) shouldBe "a context nested test"
      container.displayPath(false) shouldBe "a context"
      spec.displayPath(false) shouldBe ""
   }

   test("path") {
      rootTest.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- root test"
      test.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- a context -- nested test"
      container.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- a context"
      spec.path() shouldBe "com.sksamuel.kotest.DescriptionTest"
   }

   test("names") {
      rootTest.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"), TestName("root test"))
      test.names() shouldBe listOf(
         TestName("com.sksamuel.kotest.DescriptionTest"),
         TestName("a context"),
         TestName("nested test")
      )
      container.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"), TestName("a context"))
      spec.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"))
   }

   test("append") {
      container.append(TestName("foo"), DescriptionType.Container) shouldBe
         Description(container, TestName("foo"), DescriptionType.Container, DescriptionTest::class as KClass<out AbstractSpec>)
      test.append(TestName("foo"), DescriptionType.Container) shouldBe
         Description(test, TestName("foo"), DescriptionType.Container, DescriptionTest::class as KClass<out AbstractSpec>)
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
