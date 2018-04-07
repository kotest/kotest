package io.kotlintest.runner.junit5

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor

/**
 * An implementation of [TestContext] that talks back to a JUnit5
 * Test Runner whenever a scope is added.
 *
 * This context should not be shared between multiple [TestScope] instances.
 */
class JUnit5TestContext(private val descriptor: TestDescriptor,
                        private val listener: EngineExecutionListener,
                        private val scope: TestScope) : FutureAwareTestContext() {

  override fun description(): Description = scope.description()

  override fun currentScope(): TestScope = scope

  override fun addScope(scope: TestScope): TestScope {
    val newDescriptor = when (scope) {
      is TestContainer -> TestContainerDescriptor.fromTestContainer(descriptor.uniqueId, scope)
      is TestCase -> TestCaseDescriptor.fromTestCase(descriptor.uniqueId, scope)
      else -> throw IllegalArgumentException()
    }
    descriptor.addChild(newDescriptor)
    synchronized(listener) {
      listener.dynamicTestRegistered(newDescriptor)
    }
    return scope
  }
}