package com.sksamuel.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class CompositeTestEngineListenerTest : FunSpec({

   test("specEnter should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : TestEngineListener {
         override suspend fun specEnter(kclass: KClass<out Spec>) {
            fired1 = true
         }
      }
      val l2 = object : TestEngineListener {
         override suspend fun specEnter(kclass: KClass<out Spec>) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specEnter(CompositeTestEngineListenerTest::class)
      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("specExit should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : TestEngineListener {
         override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
            fired1 = true
         }
      }
      val l2 = object : TestEngineListener {
         override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
            fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).specExit(CompositeTestEngineListenerTest::class, null)
      fired1 shouldBe true
      fired2 shouldBe true
   }
})
