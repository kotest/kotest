package com.sksamuel.kotest.engine.exts

import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

/**
 * Asserts that an exception through in a [DiscoveryExtension] is correctly handled.
 */
@Isolate
class DiscoveryExtensionExceptionTest : FunSpec() {
   init {
      test("an exception in a discovery extension should be handled") {
         configuration.registerExtension(ext)
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector).withClasses(Dummy::class).launch()
         collector.errors.shouldBeTrue()
         configuration.deregisterExtension(ext)
      }
   }
}

private class EmptySpec : FunSpec()

private val ext = object : DiscoveryExtension {
   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      error("discovery goes boom")
   }
}

private object Dummy : FunSpec() {
   init {
      test("a") {}
   }
}
