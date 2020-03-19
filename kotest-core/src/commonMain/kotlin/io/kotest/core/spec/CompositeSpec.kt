package io.kotest.core.spec

import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.generate
import io.kotest.core.test.TestCase

abstract class CompositeSpec(vararg factories: TestFactory) : Spec() {
   init {
      factories.forEach { include(it) }
   }

   override fun materializeRootTests(): List<TestCase> {
      return factories.flatMap { it.generate(this::class.description(), this) }
   }
}
