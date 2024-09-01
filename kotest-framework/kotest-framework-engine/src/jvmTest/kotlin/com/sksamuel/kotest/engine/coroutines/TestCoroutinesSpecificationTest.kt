package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * This test is meant to be the definite source of truth for the behavior of coroutines in tests.
 */
class TestCoroutinesSpecificationTest : FunSpec() {
   init {

      test("each root test should run in its own coroutine") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestCoroutineInheritance::class)
            .launch()
         coroutines shouldBe setOf("kotest-test-a", "kotest-test-b")
      }

      test("child tests should inheritent context from their containers") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestCoroutineInheritance::class)
            .launch()
         elementValue shouldBe "wobble"
      }
   }
}
private var elementValue: String? = null
private val coroutines = mutableSetOf<String>()

private class TestCoroutineInheritance : FunSpec() {
   init {

      test("a") {
         coroutines.add(coroutineContext[CoroutineName]?.name ?: "")
      }

      test("b") {
         coroutines.add(coroutineContext[CoroutineName]?.name ?: "")
      }

      context("foo") {
         withContext(MyElement("wobble")) {
            test("bar") {
               elementValue = coroutineContext[MyElement]?.name
            }
         }
      }
   }
}

data class MyElement(val name: String) : AbstractCoroutineContextElement(MyElement) {
   companion object Key : CoroutineContext.Key<MyElement>
}
