package com.sksamuel.kotest.engine

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.EnabledTestConfigFlagTest
import com.sksamuel.kotest.engine.active.FocusTest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.TestEngineInitializedContext
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

      var str = ""
      val listener = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            str += ref.fqn
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

      str shouldBe "abc"
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
      }

      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(FocusTest::class))
         .execute()

      str.shouldBe("ba")
   }

   test("should invoke engineFinished with errors") {

      var errors = emptyList<Throwable>()
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors = t
         }
      }

      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(FocusTest::class))
         .execute()

      errors.shouldHaveSize(1)
   }
})
