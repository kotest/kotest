package com.sksamuel.kotest.engine.extensions.spec.beforespec

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.Collections
import kotlin.reflect.KClass

/**
 * A failing beforeSpec callback aborts the spec, but the afterSpec callbacks must still be
 * invoked so that resources acquired by the callbacks that did run are released.
 */
class BeforeSpecFailureAfterSpecTest : FunSpec() {

   init {

      afterEach {
         recorder.clear()
      }

      test("afterSpec listeners should be invoked when a beforeSpec listener fails") {

         val container = object : BeforeSpecListener, AfterSpecListener {
            override suspend fun beforeSpec(spec: Spec) {
               recorder.add("container started")
            }

            override suspend fun afterSpec(spec: Spec) {
               recorder.add("container stopped")
            }
         }

         val listener = execute(PlainSpec::class, projectConfig(container, FailingBeforeSpecListener))

         recorder shouldContainExactly listOf("container started", "boom", "container stopped")
         listener.specs.values.single().errorOrNull.shouldBeInstanceOf<ExtensionException.BeforeSpecException>()
         listener.tests.keys.shouldBeEmpty()
      }

      context("afterSpec function overrides and DSL functions should be invoked when beforeSpec fails") {
         withTests(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            val config = object : AbstractProjectConfig() {
               override val isolationMode = isolationMode
            }

            val listener = execute(BeforeSpecErrorSpec::class, config)

            listener.specs.values.single().isError.shouldBeTrue()
            listener.tests.keys.shouldBeEmpty()
            recorder shouldContainExactly listOf("beforeSpec", "afterSpec function", "afterSpec DSL")
         }
      }

      test("autocloseables registered before the failure should be closed") {
         execute(AutoCloseSpec::class, projectConfig(FailingBeforeSpecListener))
         closed.shouldBeTrue()
      }

      test("errors from afterSpec should be reported alongside the beforeSpec error") {
         val listener = execute(PlainSpec::class, projectConfig(FailingBeforeSpecListener, FailingAfterSpecListener))

         val error = listener.specs.values.single().errorOrNull
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.map { it::class } shouldContainExactly listOf(
            ExtensionException.BeforeSpecException::class,
            ExtensionException.AfterSpecException::class,
         )
      }
   }
}

private fun projectConfig(vararg exts: Extension) = object : AbstractProjectConfig() {
   override val extensions = exts.toList()
}

private suspend fun execute(
   kclass: KClass<out Spec>,
   config: AbstractProjectConfig,
): CollectingTestEngineListener {
   val listener = CollectingTestEngineListener()
   // the specs under test must run even when this test class was selected by a filter
   TestEngineLauncher().withoutEnvFilters()
      .withListener(listener)
      .withSpecRefs(SpecRef.Reference(kclass))
      .withProjectConfig(config)
      .execute()
   return listener
}

private val recorder = Collections.synchronizedList(mutableListOf<String>())

@Volatile
private var closed = false

private object FailingBeforeSpecListener : BeforeSpecListener {
   override suspend fun beforeSpec(spec: Spec) {
      recorder.add("boom")
      error("boom")
   }
}

private object FailingAfterSpecListener : AfterSpecListener {
   override suspend fun afterSpec(spec: Spec) {
      error("bang")
   }
}

private class PlainSpec : FunSpec() {
   init {
      test("a") {}
   }
}

private class BeforeSpecErrorSpec : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      recorder.add("beforeSpec")
      error("boom")
   }

   override suspend fun afterSpec(spec: Spec) {
      recorder.add("afterSpec function")
   }

   init {
      afterSpec {
         recorder.add("afterSpec DSL")
      }
      test("a") {}
      test("b") {}
   }
}

private class AutoCloseSpec : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      autoClose(AutoCloseable { closed = true })
   }

   init {
      test("a") {}
   }
}
