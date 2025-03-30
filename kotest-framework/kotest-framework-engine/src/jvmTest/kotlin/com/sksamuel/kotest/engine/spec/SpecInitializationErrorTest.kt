package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecInstantiationException
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecInitializationErrorTest : FunSpec() {
   init {
      test("errors in fields should fail the spec in the engine") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector).withClasses(FieldInitErrorSpec::class).launch()
         collector.specs[FieldInitErrorSpec::class]!!.errorOrNull.shouldBeInstanceOf<SpecInstantiationException>()
      }
   }
}

// tests that errors in fields of a spec actually error the engine
private class FieldInitErrorSpec : FunSpec() {

   private val err = "failme".apply { error("foo") }

   init {
      test("foo") {
      }
   }
}
