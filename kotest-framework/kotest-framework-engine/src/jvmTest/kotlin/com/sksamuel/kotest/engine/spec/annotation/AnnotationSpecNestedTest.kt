package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class AnnotationSpecNestedTest : FunSpec() {
   init {
      test("should detect nested classes") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(listener)
            .withClasses(AnnotationSpecWithNested::class)
            .launch()
         listener.tests.shouldHaveSize(2)
         listener.tests.keys.map { it.name.name }.toSet() shouldBe setOf("foo", "bar")
         listener.result("foo")!!.isSuccess shouldBe true
         listener.result("bar")!!.isSuccess shouldBe true
      }
   }
}

private class AnnotationSpecWithNested : AnnotationSpec() {
   @Test
   fun foo() {
      println("foo")
   }

   @Nested
   class InsideTest : AnnotationSpec() {
      @Test
      fun bar() {
         println("bar")
      }
   }
}
