package io.kotest.engine.multiconfig

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import java.util.concurrent.atomic.AtomicInteger

val counter = AtomicInteger(0)
val beforeAll = AtomicInteger(0)

object MyExtension : TestListener {
   override suspend fun beforeEach(testCase: TestCase) {
      counter.incrementAndGet()
   }
}

class Config1 : AbstractProjectConfig() {
   override val testCaseOrder = TestCaseOrder.Random
   override fun extensions(): List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}

class Config2 : AbstractProjectConfig() {
   override val specExecutionOrder: SpecExecutionOrder = SpecExecutionOrder.Random
   override fun extensions(): List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}
