package io.kotest.core.spec

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.createTestCases

abstract class CompositeSpec(private vararg val factories: TestFactory) : Spec() {

   private val testCases = factories.flatMap { it.createTestCases(this::class.toDescriptor(), this) }

   override fun materializeRootTests(): List<RootTest> {
      return testCases.withIndex().map { RootTest(it.value, it.index) }
   }
}
