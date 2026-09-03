package com.sksamuel.kt.koin

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declare

private class Counter {
   var count = 0
}

// a factory that builds a fresh module instance on every invocation, so each test
// gets a brand new singleton rather than reusing a cached one
private fun counterModule() = module {
   single { Counter() }
}

/**
 * Regression test for https://github.com/kotest/kotest/issues/6006.
 *
 * When the Koin context is mutated during a test (here via [declare]), reusing the same
 * [org.koin.core.module.Module] instance across tests can leak singleton state. Supplying a
 * module factory gives each test a fresh module instance, so state does not leak between tests.
 */
class KoinExtensionModuleFactoryTest : FunSpec(), KoinTest {

   init {

      // factory form: a fresh module (and therefore a fresh singleton) for every test
      extensions(KoinExtension { listOf(counterModule()) })

      test("first test mutates the singleton and overrides it via declare") {
         val counter = get<Counter>()
         counter.count shouldBe 0
         counter.count = 5

         declare { Counter().apply { count = 99 } }
         get<Counter>().count shouldBe 99
      }

      test("second test sees a fresh singleton, with no leaked state from the first test") {
         get<Counter>().count shouldBe 0
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      GlobalContext.getOrNull() shouldBe null // koin should be stopped after the spec completes
   }
}
