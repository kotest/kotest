package com.sksamuel.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.matchers.shouldBe

@Isolate
class TagsAnnotationInheritenceTest : FunSpec() {
   init {

      test("simple tag") {

         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression.include(Linux)
         }

         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe setOf("a", "b", "c", "d")
      }

      test("simple exclude tag") {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression.exclude(Linux)
         }

         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         // all tests should be filtered out because of the @Tags
         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe emptySet()
      }

      test("inheritence with OR") {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression("Linux | Mysql")
         }
         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         // linux is included for all, and we're using an 'or'
         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe setOf("a", "b", "c", "d")
      }

      test("inheritence with AND") {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression.include(Linux).exclude(Postgres)
         }
         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         // linux should be included for all, but then postgres tests excluded as well
         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe setOf("a", "d")
      }

      test("@Tags should be ignored when not applicable to an exclude") {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression.exclude(Mysql)
         }
         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         // Mysql tests should be excluded
         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe setOf("b", "d")
      }

      test("@Tags should be ignored when not applicable to an test") {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression.include(Postgres)
         }
         val conf = Configuration()
         conf.registry.add(ext)
         conf.testCaseOrder = TestCaseOrder.Random

         // Mysql tests should be excluded
         Materializer(conf).materialize(MyTestClass())
            .filter { it.isEnabledInternal(conf).isEnabled }
            .map { it.name.testName }
            .toSet() shouldBe setOf("b", "c")
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
