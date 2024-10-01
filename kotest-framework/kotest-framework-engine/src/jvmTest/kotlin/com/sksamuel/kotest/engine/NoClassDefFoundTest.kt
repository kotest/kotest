package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecInstantiationException
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

// tests that a java.lang.NoClassDefFoundError is caught
@EnabledIf(LinuxCondition::class)
class NoClassDefFoundTest : FunSpec() {
   init {
      test("java.lang.NoClassDefFoundError should be caught") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(SomeSpec1::class, SomeSpec2::class).launch()
         listener.specs.shouldHaveSize(2)
         listener.specs[SomeSpec1::class]!!.errorOrNull.shouldBeInstanceOf<SpecInstantiationException>()
         listener.specs[SomeSpec2::class]!!.errorOrNull.shouldBeInstanceOf<SpecInstantiationException>()
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

// we try to use FailingClass twice, so the 2nd time it triggers a java.lang.NoClassDefFoundError
private class SomeSpec2 : FunSpec() {
   private val failure = FailingClass()

   init {
      test("foo") {

      }
   }
}
