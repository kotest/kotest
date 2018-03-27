package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import org.junit.platform.commons.JUnitException
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import java.util.*

/**
 * The top level [TestDescriptor] which is used to
 * hold the root containers of each [io.kotlintest.Spec].
 */
data class RootTestDescriptor(val id: UniqueId) : BranchDescriptor() {
  override fun removeFromHierarchy() = throw JUnitException("Cannot remove from hierarchy for root")
  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = "Kotlin Test"
  override fun getSource(): Optional<TestSource> = Optional.empty()
  override fun mayRegisterTests() = true
}

data class TestContainerDescriptor(val id: UniqueId,
                                   val container: TestContainer) : BranchDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = container.name()
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(container.spec.javaClass))
  override fun mayRegisterTests() = true

  companion object {
    fun fromTestContainer(parentId: UniqueId, container: TestContainer): TestContainerDescriptor =
        TestContainerDescriptor(parentId.append("container", container.name()), container)
  }
}

data class TestCaseDescriptor(val id: UniqueId,
                              val testCase: TestCase) : LeafDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = testCase.name()
  override fun getSource(): Optional<TestSource> {
    val source = ClassSource.from(testCase.spec.javaClass, FilePosition.from(Math.max(1, testCase.line)))
    return Optional.of(source)
  }

  companion object {
    fun fromTestCase(parentId: UniqueId, tc: TestCase): TestCaseDescriptor =
        TestCaseDescriptor(parentId.append("test", tc.name()), tc)
  }
}