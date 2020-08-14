package io.kotest.core.spec

import io.kotest.core.factory.TestFactory
import io.kotest.core.test.TestCase
import io.kotest.engine.factory.generateTests
import io.kotest.engine.test.toDescription

abstract class CompositeSpec(private vararg val factories: TestFactory) : Spec {

   init {
      factories.forEach { include(it) }
   }

   override fun materializeRootTests(): List<TestCase> {
      return factories.flatMap { it.generateTests(this::class.toDescription(), this) }
   }
}
