package io.kotest.core.spec

import io.kotest.core.factory.TestFactory

abstract class CompositeSpec(vararg factories: TestFactory) : SpecConfiguration() {
   init {
      factories.forEach { include(it) }
   }
}
