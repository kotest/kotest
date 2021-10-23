package com.sksamuel.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.core.spec.Isolate
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe

@Isolate
class TagsAnnotationInheritenceTest : FunSpec() {
   init {

      test("simple tag") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Linux)
         }
         configuration.registerExtension(ext)
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe listOf("a", "b", "c", "d")
         configuration.deregisterExtension(ext)
      }

      test("simple exclude tag") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.exclude(Linux)
         }
         configuration.registerExtension(ext)
         // all tests should be filtered out because of the @Tags
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe emptyList()
         configuration.deregisterExtension(ext)
      }

      test("inheritence with OR") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("Linux | Mysql")
         }
         configuration.registerExtension(ext)
         // linux is included for all and we're using an or
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe listOf("a", "b", "c", "d")
         configuration.deregisterExtension(ext)
      }

      test("inheritence with AND") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Linux).exclude(Postgres)
         }
         configuration.registerExtension(ext)
         // linux should be included for all, but then postgres tests excluded as well
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe listOf("a", "d")
         configuration.deregisterExtension(ext)
      }

      test("@Tags should be ignored when not applicable to an exclude") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.exclude(Mysql)
         }
         configuration.registerExtension(ext)
         // Mysql tests should be excluded
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe listOf("b", "d")
         configuration.deregisterExtension(ext)
      }

      test("@Tags should be ignored when not applicable to an test") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Postgres)
         }
         configuration.registerExtension(ext)
         // Mysql tests should be excluded
         MyTestClass().materializeAndOrderRootTests(TestCaseOrder.Random)
            .filter { it.testCase.isEnabledInternal(configuration).isEnabled }
            .map { it.testCase.name.testName } shouldBe listOf("b", "c")
         configuration.deregisterExtension(ext)
      }
   }
}

object Linux : Tag()
object UnitTest : Tag()
object Mysql : Tag()
object Postgres : Tag()

@io.kotest.core.annotation.Tags("Linux")
private class MyTestClass : FunSpec() {
   init {
      tags(UnitTest)
      test("a").config(tags = setOf(Mysql)) { }
      test("b").config(tags = setOf(Postgres)) { }
      test("c").config(tags = setOf(Postgres, Mysql)) { }
      test("d") { }
   }
}
