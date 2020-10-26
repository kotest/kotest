package io.kotest.engine.multiconfig

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder

var listeners = 0
var beforeAll = 0

object Listener : TestListener {
   override suspend fun beforeEach(testCase: TestCase) {
      listeners++
   }
}

class Config1 : AbstractProjectConfig() {
   override val testCaseOrder = TestCaseOrder.Random
   override fun listeners(): List<Listener> = listOf(Listener)
   override fun beforeAll() {
      beforeAll++
   }
}

class Config2 : AbstractProjectConfig() {
   override val specExecutionOrder: SpecExecutionOrder = SpecExecutionOrder.Random
   override fun listeners(): List<Listener> = listOf(Listener)
   override fun beforeAll() {
      beforeAll++
   }
}
