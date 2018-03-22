package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import org.junit.platform.commons.JUnitException
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import java.util.*

/**
 * The top level [TestDescriptor] which is used to
 * hold the root containers of each [io.kotlintest.Spec].
 */
data class RootTestDescriptor(val id: UniqueId) : BranchDescriptor() {
  override fun removeFromHierarchy() = throw JUnitException("Cannot remove from hierarchy for root")
  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = "Test Results"
  override fun getSource(): Optional<TestSource> = Optional.empty()
  override fun mayRegisterTests() = true
}

data class TestContainerDescriptor(val id: UniqueId,
                                   val container: TestContainer) : BranchDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = container.displayName
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(container.spec.javaClass))
  override fun mayRegisterTests() = true

  /**
   * Executes the discovery function of the test container, and
   * adds any returned [TestCase] and [TestContainer] instances
   * to this descriptor.
   */
  fun discover(listener: EngineExecutionListener) {
    val testxs = container.discovery()
    val descriptors = testxs.map {
      when (it) {
        is TestContainer -> fromTestContainer(uniqueId, it)
        is TestCase -> TestCaseDescriptor.fromTestCase(uniqueId, it)
        else -> throw RuntimeException()
      }
    }
    descriptors.forEach {
      addChild(it)
      listener.dynamicTestRegistered(it)
    }
  }

  companion object {
    fun fromTestContainer(parentId: UniqueId, container: TestContainer): TestContainerDescriptor =
        TestContainerDescriptor(parentId.append("container", container.displayName), container)
  }
}

data class TestCaseDescriptor(val id: UniqueId,
                              val testCase: TestCase) : LeafDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = testCase.displayName
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(testCase.spec.javaClass))

  companion object {
    fun fromTestCase(parentId: UniqueId, tc: TestCase): TestCaseDescriptor =
        TestCaseDescriptor(parentId.append("test", tc.displayName), tc)
  }
}