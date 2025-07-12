package com.sksamuel.kotest.engine.listener

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class CompositeTestEngineListenerTest : FunSpec({

   test("specStarted should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun specStarted(kclass: KClass<*>) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specStarted(SpecRef.Reference(CompositeTestEngineListenerTest::class))
      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("specFinished should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
            fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specFinished(
         CompositeTestEngineListenerTest::class,
         TestResult.Success(0.seconds)
      )
      fired1 shouldBe true
      fired2 shouldBe true
   }
})
