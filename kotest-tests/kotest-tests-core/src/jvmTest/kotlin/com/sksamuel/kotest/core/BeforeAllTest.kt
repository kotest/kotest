package com.sksamuel.kotest.core

import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.BeforeBeforeListenerException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class BeforeAllTest : FunSpec({
   test("before all error should use BeforeAllListenerException") {
      var throwable: Throwable? = null
      val listener = object : TestEngineListener {
         override fun engineFinished(t: Throwable?) {
            throwable = t
         }
      }
      val listeners = listOf(object : ProjectListener {
         override fun beforeProject() {
            error("boom")
         }
      })
      val engine = KotestEngine(listOf(DummySpec::class), emptyList(), 1, emptySet(), emptySet(), listener, listeners)
      engine.execute()
      throwable.shouldBeInstanceOf<BeforeBeforeListenerException>()
   }
})

internal class DummySpec : FunSpec({
   test("foo") {}
})
