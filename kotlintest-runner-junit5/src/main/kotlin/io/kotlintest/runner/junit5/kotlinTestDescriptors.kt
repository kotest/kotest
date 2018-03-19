package io.kotlintest.runner.junit5

import io.kotlintest.Spec
import io.kotlintest.TestCase
import org.junit.platform.commons.JUnitException
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import java.util.*

/**
 * A top level container [TestDescriptor] used to hold
 * all discovered specs.
 */
class RootTestDescriptor(val id: UniqueId,
                         val name: String) : BranchDescriptor() {
  override fun removeFromHierarchy() = throw JUnitException("Cannot remove from hierarchy for root")
  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = name
  override fun getSource(): Optional<TestSource> = Optional.empty()
}

/**
 * A container level [TestDescriptor] that is used to contain
 * nested contexts, or [TestCase]'s themselves.
 */
open class TestContainerDescriptor(val id: UniqueId,
                                   val name: String,
                                   val spec: Spec) : BranchDescriptor() {
  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = name
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(spec.javaClass))

  companion object {
    fun fromSpec(spec: Spec): TestContainerDescriptor =
        TestContainerDescriptor(UniqueId.parse("wibble_" + System.currentTimeMillis()), spec.javaClass.simpleName, spec)
  }
}

/**
 * A Test level descriptor that contains a single [TestCase].
 */
class TestCaseDescriptor(val id: UniqueId,
                         val testCase: TestCase,
                         val name: String) : LeafDescriptor() {

  override fun getUniqueId(): UniqueId = id
  override fun getDisplayName(): String = testCase.displayName
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(testCase.spec.javaClass))
}