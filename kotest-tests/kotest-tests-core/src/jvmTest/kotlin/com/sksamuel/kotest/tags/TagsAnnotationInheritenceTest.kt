package com.sksamuel.kotest.tags

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.engine.config.Project
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.isActive
import io.kotest.matchers.shouldBe

class TagsAnnotationInheritenceTest : FunSpec() {
   init {

      test("simple tag") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Linux)
         }
         Project.registerExtension(ext)
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe listOf("a", "b", "c", "d")
         Project.deregisterExtension(ext)
      }

      test("simple exclude tag") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.exclude(Linux)
         }
         Project.registerExtension(ext)
         // all tests should be filtered out because of the @Tags
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe emptyList()
         Project.deregisterExtension(ext)
      }

      test("inheritence with OR") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("Linux | Mysql")
         }
         Project.registerExtension(ext)
         // linux is included for all and we're using an or
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe listOf("a", "b", "c", "d")
         Project.deregisterExtension(ext)
      }

      test("inheritence with AND") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Linux).exclude(Postgres)
         }
         Project.registerExtension(ext)
         // linux should be included for all, but then postgres tests excluded as well
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe listOf("a", "d")
         Project.deregisterExtension(ext)
      }

      test("@Tags should be ignored when not applicable to an exclude") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.exclude(Mysql)
         }
         Project.registerExtension(ext)
         // Mysql tests should be excluded
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe listOf("b", "d")
         Project.deregisterExtension(ext)
      }

      test("@Tags should be ignored when not applicable to an test") {
         val ext = object : TagExtension {
            override fun tags(): Tags = Tags.include(Postgres)
         }
         Project.registerExtension(ext)
         // Mysql tests should be excluded
         MyTestClass().rootTests()
            .filter { it.testCase.isActive() }
            .map { it.testCase.displayName } shouldBe listOf("b", "c")
         Project.deregisterExtension(ext)
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
