package io.kotest.core.spec

import io.kotest.core.factory.TestFactory

abstract class CompositeSpec(private vararg val factories: TestFactory) : RegisterableSpec() {
   init {
      factories.forEach { include(it) }
   }
}
