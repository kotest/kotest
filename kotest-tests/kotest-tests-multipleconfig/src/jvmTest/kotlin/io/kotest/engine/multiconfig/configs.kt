package io.kotest.engine.multiconfig

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import kotlin.reflect.KClass

var listeners = 0
var beforeAll = 0

var inlineListeners = 0
var inlineExtensions = 0

object Listener : TestListener {
   override suspend fun beforeEach(testCase: TestCase) {
      listeners++
   }
}

object InlineListener : TestListener {
   override suspend fun beforeSpec(spec: Spec) {
      inlineListeners++
   }
}

object InlineExtension : SpecExtension {
   override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      inlineExtensions++
      process()
   }
}


class Config1 : AbstractProjectConfig() {
   override val testCaseOrder = TestCaseOrder.Random
   override fun listeners(): List<Listener> = listOf(Listener)
   override fun beforeAll() {
      beforeAll++
   }

   init {
      listener(InlineListener)
      listeners(InlineListener)
      extension(InlineExtension)
      extensions(InlineExtension)
   }
}

class Config2 : AbstractProjectConfig() {
   override val specExecutionOrder: SpecExecutionOrder = SpecExecutionOrder.Random
   override fun listeners(): List<Listener> = listOf(Listener)
   override fun beforeAll() {
      beforeAll++
   }

   init {
      listener(InlineListener)
      listeners(InlineListener)
      extension(InlineExtension)
      extensions(InlineExtension)
   }
}
