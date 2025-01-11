package com.sksamuel.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Tags
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.datatest.withData
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.spec.Materializer
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxCondition::class)
class TagsAnnotationInheritenceTest : FunSpec() {
   init {

      test("simple tag") {

         val ext = TagExtension { TagExpression.include(Linux) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "b", "c", "d")
      }

      test("simple exclude tag") {
         val ext = TagExtension { TagExpression.exclude(Linux) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         // all tests should be filtered out because of the @Tags
         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe emptySet()
      }

      test("inheritence with OR") {
         val ext = TagExtension { TagExpression("Linux | Mysql") }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         // linux is included for all, and we're using an 'or'
         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "b", "c", "d")
      }

      test("inheritence with AND") {
         val ext = TagExtension { TagExpression.include(Linux).exclude(Postgres) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         // linux should be included for all, but then postgres tests excluded as well
         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "d")
      }

      test("@Tags should be ignored when not applicable to an exclude") {
         val ext = TagExtension { TagExpression.exclude(Mysql) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         // Mysql tests should be excluded
         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("b", "d")
      }

      test("@Tags should be ignored when not applicable to an test") {
         val ext = TagExtension { TagExpression.include(Postgres) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override fun extensions(): List<Extension> = listOf(ext)
         }

         // Mysql tests should be excluded
         Materializer(SpecConfigResolver(c)).materialize(MyTestClass())
            .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("b", "c")
      }

      context("Inheritance of @Tags") {
         withData(
            nameFn = { "inheritanceEnabled=${it.first}, expectedTests=${it.second}" },
            true to setOf("a"),
            false to setOf()
         ) { (inheritanceEnabled, expectedTests) ->
            val ext = TagExtension { TagExpression.include(NamedTag("SuperSuper")) }

            val c = object : AbstractProjectConfig() {
               override val testCaseOrder = TestCaseOrder.Random
               override val tagInheritance = inheritanceEnabled
               override fun extensions(): List<Extension> = listOf(ext)
            }

            Materializer(SpecConfigResolver(c)).materialize(InheritingTest())
               .filter { it.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled }
               .map { it.name.name }
               .toSet() shouldBe expectedTests
         }
      }
   }
}

object Linux : Tag()
object UnitTest : Tag()
object Mysql : Tag()
object Postgres : Tag()

@Tags("Linux")
private class MyTestClass : FunSpec() {
   init {
      tags(UnitTest)
      test("a").config(tags = setOf(Mysql)) { }
      test("b").config(tags = setOf(Postgres)) { }
      test("c").config(tags = setOf(Postgres, Mysql)) { }
      test("d") { }
   }
}

private class InheritingTest : SlowTest, CustomSpec({
   test("a") {}
})

@Tags("SuperTag")
private open class CustomSpec(block: FunSpec.() -> Unit) : SuperSuper, FunSpec(block)

@Tags("Slow")
private interface SlowTest

@Tags("SuperSuper")
private interface SuperSuper
