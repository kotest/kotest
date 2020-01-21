package io.kotest.core.spec

import io.kotest.core.factory.TestFactory

abstract class CompositeSpec(vararg factories: TestFactory) : Spec() {
   init {
      factories.forEach { include(it) }
   }
}
