package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.test.TestContext

/**
 * Contains methods to register callbacks and tests in the current scope, whether
 * that be top level tests in a [DslDrivenSpec], top level tests in a [TestFactoryConfiguration],
 * or nested tests via a [TestContext].
 */
interface Lifecycle {

   fun addListener(listener: TestListener)

   companion object {
      fun from(factory: TestFactoryConfiguration): Lifecycle = FactoryLifecycle(factory)
      fun from(spec: DslDrivenSpec) = SpecLifecyle(spec)
   }
}

class SpecLifecyle(private val spec: DslDrivenSpec) : Lifecycle {
   override fun addListener(listener: TestListener) {
      spec.listener(listener)
   }
}

class FactoryLifecycle(private val factory: TestFactoryConfiguration) : Lifecycle {
   override fun addListener(listener: TestListener) {
      factory.listener(listener)
   }
}
