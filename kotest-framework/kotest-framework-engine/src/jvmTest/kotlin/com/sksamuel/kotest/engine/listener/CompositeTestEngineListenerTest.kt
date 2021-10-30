package com.sksamuel.kotest.engine.listener

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class CompositeTestEngineListenerTest : FunSpec({

   test("specStarted should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun specStarted(kclass: KClass<*>) {
            fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun specStarted(kclass: KClass<*>) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specStarted(CompositeTestEngineListenerTest::class)
      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("specFinished should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
            fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specFinished(CompositeTestEngineListenerTest::class, null)
      fired1 shouldBe true
      fired2 shouldBe true
   }
})
