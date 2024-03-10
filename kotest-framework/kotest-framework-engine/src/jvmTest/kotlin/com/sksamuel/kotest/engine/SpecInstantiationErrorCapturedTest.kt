package com.sksamuel.kotest.engine

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecInstantiationException
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

class SpecInstantiationErrorCapturedTest : FunSpec() {
   init {
      test("spec instantiation errors should be captured and reported") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(SpecInstantiationFailureSpec::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.specs.shouldHaveSize(1)
         listener.specs[SpecInstantiationFailureSpec::class]!!.errorOrNull.shouldBeInstanceOf<SpecInstantiationException>()
      }
   }
}

private class SpecInstantiationFailureSpec : FunSpec() {
   init {
      error("CLUNK!")
   }
}
