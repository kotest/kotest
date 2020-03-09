package com.sksamuel.kotest.core

import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.AfterProjectListenerException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class AfterAllTest : FunSpec({
   test("after all error should use AfterAllListenerException") {
      var throwable: Throwable? = null
      val listener = object : TestEngineListener {
         override fun engineFinished(t: Throwable?) {
            throwable = t
         }
      }
      val listeners = listOf(object : ProjectListener {
         override fun afterProject() {
            error("boom")
         }
      })
      val engine = KotestEngine(listOf(DummySpec2::class), emptyList(), 1, emptySet(), emptySet(), listener, listeners)
      engine.execute()
      throwable.shouldBeInstanceOf<AfterProjectListenerException>()
   }
})

internal class DummySpec2 : FunSpec({
   test("foo") {}
})
