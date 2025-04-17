package io.kotest.core.spec

import io.kotest.core.factory.TestFactory

abstract class CompositeSpec(private vararg val factories: TestFactory) : Spec() {
   override fun rootTests(): List<RootTest> {
      return factories.flatMap { it.tests }
   }
}
