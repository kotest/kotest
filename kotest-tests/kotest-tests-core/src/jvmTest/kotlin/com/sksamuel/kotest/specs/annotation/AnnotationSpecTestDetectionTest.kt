package com.sksamuel.kotest.specs.annotation

import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

private var tests = 0

class AnnotationSpecTestDetectionTest : FunSpec({

   test("An annotation spec should detect annotation tests") {

      val listener = object : TestEngineListener {
         override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
            tests shouldBe 2
         }
      }
      val executor = SpecExecutor(listener)
      executor.execute(AnnotationSpecClass::class)
   }

})

internal class AnnotationSpecClass : AnnotationSpec() {

   @Test
   fun myTest() {
      tests += 1
   }

   @Test
   fun test2() {
      tests += 1
   }

}
