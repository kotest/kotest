package com.sksamuel.kotest.engine

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.EnabledTestConfigFlagTest
import com.sksamuel.kotest.engine.active.FocusTest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineInitializedContext
import io.kotest.engine.tags.TagExpression
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestEngineTest : FunSpec({

   test("should sort specs using project config") {

      val c = object : AbstractProjectConfig() {
         override val specExecutionOrder = SpecExecutionOrder.Lexicographic
      }

      var specs = mutableListOf<String>()
      val listener = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            specs.add(ref.fqn)
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(c)
         .withSpecRefs(
            listOf(
               SpecRef.Reference(EnabledTestConfigFlagTest::class),
               SpecRef.Reference(BangDisableFunSpec::class),
               SpecRef.Reference(FocusTest::class),
            )
         ).execute()

      specs.joinToString(";") shouldBe "com.sksamuel.kotest.engine.active.BangDisableFunSpec;com.sksamuel.kotest.engine.active.EnabledTestConfigFlagTest;com.sksamuel.kotest.engine.active.FocusTest"
   }


   test("should invoke engineInitialized") {

      var fired = false
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineInitialized(context: TestEngineInitializedContext) {
            fired = true
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(FocusTest::class))
         .execute()

      fired.shouldBeTrue()
   }

   test("should invoke engineStarted before downstream") {

      var str = ""
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineStarted() {
            str += "a"
         }

         override suspend fun specStarted(ref: SpecRef) {
            str += "b"
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(FocusTest::class))
         .execute()

      str.shouldBe("ab")
   }

   test("should invoke engineFinished after downstream") {

      var str = ""
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            str += "a"
         }

         override suspend fun specStarted(ref: SpecRef) {
            str += "b"
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(FocusTest::class))
         .execute()

      str.shouldBe("ba")
   }

   test("should invoke engineFinished with errors") {

      var errors = emptyList<Throwable>()

      val ext = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            error("boom")
         }
      }


      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors = t
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext)
      }

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(c)
         .execute()

      errors.shouldHaveSize(1)
   }

   test("should invoke all project extensions") {

      var fired1 = false
      var fired2 = false

      val ext1 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            fired1 = true
            callback(context)
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            fired2 = true
            callback(context)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext1, ext2)
      }

      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should invoke specs after project extensions") {

      var str = ""

      val ext1 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            str += "ext1"
            callback(context)
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            str += "ext2"
            callback(context)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext1, ext2)
      }

      val listener = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            str += "spec"
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      str shouldBe "ext1ext2spec"
   }

   test("should invoke specs if no project extensions") {
      var fired = false
      val listener = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            fired = true
         }
      }
      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()
      fired shouldBe true
   }

   test("should propagate tag changes in project extensions") {

      val ext = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            callback(context.copy(tags = context.tags.include("bar")))
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext)
      }

      var tags = TagExpression("none")

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineInitialized(context: TestEngineInitializedContext) {
            tags = context.tags
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      tags.expression shouldBe "bar"
   }
})

private class DummySpec2 : FunSpec() {
   init {
      test("foo") {}
   }
}
