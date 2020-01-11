package io.kotest.core.spec

abstract class CompositeSpec(vararg factories: TestFactory) : SpecConfiguration() {
   init {
      factories.forEach { include(it) }
   }
}
