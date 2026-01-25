package com.sksamuel.kotest.runner.junit4

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.Descriptions
import io.kotest.runner.junit4.FreeSpec

class DescriptionHandlerTest : FreeSpec() {
   init {
      "test root test descriptions" {
         val tc = TestCase(
            DescriptionHandlerTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            DescriptionHandlerTest(),
            {},
            SourceRef.None,
            TestType.Test
         )
         val d = Descriptions.createTestDescription(tc)
         d.isTest shouldBe true
         d.isSuite shouldBe false
         d.className shouldBe "com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest"
         d.displayName shouldBe "foo(com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest)"
         d.methodName shouldBe "foo"
      }
      "test nested test descriptions" {
         val tc = TestCase(
            DescriptionHandlerTest::class.toDescriptor().append("foo").append("bar"),
            TestNameBuilder.builder("bar").build(),
            DescriptionHandlerTest(),
            {},
            SourceRef.None,
            TestType.Test
         )
         val d = Descriptions.createTestDescription(tc)
         d.isTest shouldBe true
         d.isSuite shouldBe false
         d.className shouldBe "com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest"
         d.displayName shouldBe "bar(com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest)"
         d.methodName shouldBe "bar"
      }
      "escapes for Android" {
         val tc = TestCase(
            DescriptionHandlerTest::class.toDescriptor().append("foo").append("silly/test"),
            TestNameBuilder.builder("silly/test").build(),
            DescriptionHandlerTest(),
            {},
            SourceRef.None,
            TestType.Test
         )
         val d = Descriptions.createTestDescription(tc)
         d.className shouldBe "com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest"
         d.displayName shouldBe "silly test(com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest)"
         d.methodName shouldBe "silly test"
      }
      "placeholder test names" {
         val d = Descriptions.createPlaceholderErrorDescription(DescriptionHandlerTest::class, Throwable("splat"))
         d.className shouldBe "com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest"
         d.displayName shouldBe "Throwable(com.sksamuel.kotest.runner.junit4.DescriptionHandlerTest)"
         d.methodName shouldBe "Throwable"
      }
   }
}
