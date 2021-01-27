package com.sksamuel.kotest.engine

import io.kotest.core.SpecInstantiationException
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

// tests that a java.lang.NoClassDefFoundError is caught
class NoClassDefFoundTest : FunSpec() {
   init {
      test("java.lang.NoClassDefFoundError should be caught") {
         val listener = CapturingTestListener()
         KotestEngineLauncher().withListener(listener).withSpecs(SomeSpec1::class, SomeSpec2::class).launch()
         listener.specsFinished.shouldHaveSize(2)
         listener.specsFinished[SomeSpec1::class].shouldBeInstanceOf<SpecInstantiationException>()
         listener.specsFinished[SomeSpec2::class].shouldBeInstanceOf<SpecInstantiationException>()
      }
   }
}

class FailingClass {
   companion object {
      init {
         error("boom")
      }
   }
}

private class SomeSpec1 : FunSpec() {
   private val failure = FailingClass()

   init {
      test("foo") {

      }
   }
}

// we try to use failure class twice, so the 2nd time it triggers a java.lang.NoClassDefFoundError
private class SomeSpec2 : FunSpec() {
   private val failure = FailingClass()

   init {
      test("foo") {

      }
   }
}
