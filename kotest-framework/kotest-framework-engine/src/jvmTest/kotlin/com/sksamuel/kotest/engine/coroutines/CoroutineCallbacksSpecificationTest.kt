package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.coroutineContext

/**
 * This test is meant to be the definite source of truth for the behavior of coroutines and callbacks in kotest.
 */
class CoroutineCallbacksSpecificationTest : FunSpec() {
   init {

      test("beforeSpec should be executed in the spec level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         beforeSpecName shouldBe "kotest-spec-Callbacks"
      }

      test("afterSpec should be executed in the spec level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         beforeSpecName shouldBe "kotest-spec-Callbacks"
      }

      test("beforeTest should be executed in the test level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         beforeTestName shouldBe "kotest-test-baz"
      }

      test("afterTest should be executed in the test level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         afterTestName shouldBe "kotest-test-bar"
      }

      test("beforeContainer should be executed in the test level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         beforeContainerName shouldBe "kotest-test-bar"
      }

      test("afterContainer should be executed in the test level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         afterContainerName shouldBe "kotest-test-bar"
      }

      test("the test coroutine should be executed in the test level coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(Callbacks::class)
            .launch()
         testName shouldBe "kotest-test-foo"
      }
   }
}

private var beforeSpecName: String? = null
private var afterSpecName: String? = null
private var beforeTestName: String? = null
private var afterTestName: String? = null
private var testName: String? = null
private var beforeContainerName: String? = null
private var afterContainerName: String? = null

private class Callbacks : FunSpec() {
   init {

      beforeSpec {
         beforeSpecName = coroutineContext[CoroutineName]?.name
      }

      afterSpec {
         afterSpecName = coroutineContext[CoroutineName]?.name
      }

      beforeTest {
         beforeTestName = coroutineContext[CoroutineName]?.name
      }

      afterTest {
         afterTestName = coroutineContext[CoroutineName]?.name
      }

      beforeContainer {
         beforeContainerName = coroutineContext[CoroutineName]?.name
      }

      afterContainer {
         afterContainerName = coroutineContext[CoroutineName]?.name
      }

      test("foo") {
         testName = coroutineContext[CoroutineName]?.name
      }

      context("bar") {
         test("baz") {
         }
      }
   }
}
