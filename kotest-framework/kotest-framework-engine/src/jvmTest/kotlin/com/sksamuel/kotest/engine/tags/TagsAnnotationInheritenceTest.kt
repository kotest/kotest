package com.sksamuel.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.annotation.Tags
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
import io.kotest.engine.test.enabled.TestEnabledChecker
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class TagsAnnotationInheritenceTest : FunSpec() {
   init {

      test("simple tag") {

         val ext = TagExtension { TagExpression.include(Linux) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "b", "c", "d")

      }

      test("simple exclude tag") {
         val ext = TagExtension { TagExpression.exclude(Linux) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         // all tests should be filtered out because of the @Tags
         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe emptySet()

      }

      test("inheritence with OR") {
         val ext = TagExtension { TagExpression("Linux | Mysql") }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         // linux is included for all, and we're using an 'or'
         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "b", "c", "d")

      }

      test("inheritence with AND") {
         val ext = TagExtension { TagExpression.include(Linux).exclude(Postgres) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         // linux should be included for all, but then postgres tests excluded as well
         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a", "d")
      }

      test("@Tags should be ignored when not applicable to an exclude") {
         val ext = TagExtension { TagExpression.exclude(Mysql) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         // Mysql tests should be excluded
         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("b", "d")
      }

      test("@Tags should be ignored when not applicable to an test") {
         val ext = TagExtension { TagExpression.include(Postgres) }

         val c = object : AbstractProjectConfig() {
            override val testCaseOrder = TestCaseOrder.Random
            override val extensions: List<Extension> = listOf(ext)
         }

         val tests = Materializer(SpecConfigResolver(c)).materialize(MyTestClass())

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         // Mysql tests should be excluded
         tests.filter { checker.isEnabled(it).isEnabled }
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
               override val extensions: List<Extension> = listOf(ext)
            }

            val tests = Materializer(SpecConfigResolver(c)).materialize(InheritingTest())

            val checker = TestEnabledChecker(
               ProjectConfigResolver(c),
               SpecConfigResolver(c),
               TestConfigResolver(c)
            )

            // Mysql tests should be excluded
            tests.filter { checker.isEnabled(it).isEnabled }
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
