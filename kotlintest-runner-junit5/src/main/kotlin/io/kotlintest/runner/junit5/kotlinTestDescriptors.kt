package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestScope
import org.junit.platform.commons.JUnitException
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import java.util.*

/**
 * A top level container [TestDescriptor] used to hold the root
 * container of each discovered [io.kotlintest.Spec].
 */
class RootTestDescriptor(val id: UniqueId) : BranchDescriptor() {
  override fun removeFromHierarchy() = throw JUnitException("Cannot remove from hierarchy for root")
  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = "Test Results"
  override fun getSource(): Optional<TestSource> = Optional.empty()
}

/**
 * A container level [TestDescriptor] that is used to contain
 * nested contexts, or [TestCase]'s themselves.
 */
open class TestContainerDescriptor(val id: UniqueId,
                                   val container: TestScope) : BranchDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = container.displayName
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(container.spec.javaClass))

  companion object {

    fun fromTestContainer(parentId: UniqueId, container: TestScope): TestContainerDescriptor {
      val desc = TestContainerDescriptor(parentId.append("container", container.displayName), container)
      container.childContainers().forEach {
        desc.addChild(fromTestContainer(desc.uniqueId, it))
      }
      container.testCases().forEach {
        desc.addChild(TestCaseDescriptor.fromTestCase(desc.uniqueId, it))
      }
      return desc
    }
  }
}

/**
 * A Test level descriptor that contains a single [TestCase].
 */
class TestCaseDescriptor(val id: UniqueId,
                         val testCase: TestCase) : LeafDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = testCase.displayName
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(testCase.spec.javaClass))

  companion object {
    fun fromTestCase(parentId: UniqueId, tc: TestCase): TestCaseDescriptor =
        TestCaseDescriptor(parentId.append("test", tc.displayName), tc)
  }
}