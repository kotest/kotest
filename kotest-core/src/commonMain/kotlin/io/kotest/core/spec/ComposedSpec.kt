package io.kotest.core.spec

abstract class ComposedSpec(vararg factories: TestFactory) : SpecConfiguration() {
   init {
      factories.forEach { include(it) }
   }
}
