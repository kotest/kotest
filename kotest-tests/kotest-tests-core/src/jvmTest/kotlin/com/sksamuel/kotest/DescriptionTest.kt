package com.sksamuel.kotest

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestName
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class DescriptionTest : FunSpec({

   val spec = Description.spec(DescriptionTest::class)
   val container = spec.append(TestName("a context"), DescriptionType.Container)
   val test = container.append(TestName("nested test"), DescriptionType.Test)
   val testWithPrefix = container.append(TestName("given", "nested test"), DescriptionType.Test)
   val rootTest = spec.append(TestName("root test"), DescriptionType.Test)

   test("full name") {
      rootTest.fullName() shouldBe "com.sksamuel.kotest.DescriptionTest root test"
      test.fullName() shouldBe "com.sksamuel.kotest.DescriptionTest a context nested test"
      container.fullName() shouldBe "com.sksamuel.kotest.DescriptionTest a context"
      spec.fullName() shouldBe "com.sksamuel.kotest.DescriptionTest"
   }

   test("full name without spec") {
      rootTest.fullNameWithoutSpec() shouldBe "root test"
      test.fullNameWithoutSpec() shouldBe "a context nested test"
      container.fullNameWithoutSpec() shouldBe "a context"
      spec.fullNameWithoutSpec() shouldBe ""
   }

   test("path") {
      rootTest.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- root test"
      test.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- a context -- nested test"
      container.path() shouldBe "com.sksamuel.kotest.DescriptionTest -- a context"
      spec.path() shouldBe "com.sksamuel.kotest.DescriptionTest"
   }

   test("names") {
      rootTest.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"), TestName("root test"))
      test.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"), TestName("a context"), TestName("nested test"))
      container.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"), TestName("a context"))
      spec.names() shouldBe listOf(TestName("com.sksamuel.kotest.DescriptionTest"))
   }

   test("append") {
      container.append(TestName("foo"), DescriptionType.Container) shouldBe
         Description(container, DescriptionTest::class as KClass<out Spec>, TestName("foo"), DescriptionType.Container)
      test.append(TestName("foo"), DescriptionType.Container) shouldBe
         Description(test, DescriptionTest::class as KClass<out Spec>, TestName("foo"), DescriptionType.Container)
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

   test("isAncestorOf with backwards compatible append") {
      val testPath = "a context -- nested test"
      val target = testPath.split(" -- ").fold(Description.spec(DescriptionTest::class)) { desc, name -> desc.append(name) }
      container.isAncestorOf(target) shouldBe true
      spec.isAncestorOf(target) shouldBe true
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
