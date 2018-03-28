package io.kotlintest.runner.junit5

import io.kotlintest.Spec
import io.kotlintest.SpecScope
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import java.util.*

/**
 * A [TestDescriptor] used as the top level container in a [Spec].
 */
data class SpecTestDescriptor(val id: UniqueId,
                              val scope: SpecScope) : AbstractTestDescriptor(id, scope.name()) {

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(scope.spec.javaClass))

  companion object {
    fun fromSpecScope(parentId: UniqueId, spec: SpecScope): SpecTestDescriptor =
        SpecTestDescriptor(parentId.append("spec", spec.name()), spec)
  }
}

/**
 * A nested container at the top level in a [Spec] or inside another [TestContainer].
 */
data class TestContainerDescriptor(val id: UniqueId,
                                   val container: TestContainer) : AbstractTestDescriptor(id, container.name()) {

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(container.spec.javaClass))
  override fun mayRegisterTests() = true

  companion object {
    fun fromTestContainer(parentId: UniqueId, container: TestContainer): TestContainerDescriptor =
        TestContainerDescriptor(parentId.append("container", container.name()), container)
  }
}

data class TestCaseDescriptor(val id: UniqueId,
                              val testCase: TestCase) : AbstractTestDescriptor(id, testCase.name()) {

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST

  override fun getSource(): Optional<TestSource> {
    val source = ClassSource.from(testCase.spec.javaClass, FilePosition.from(Math.max(1, testCase.line)))
    return Optional.of(source)
  }

  companion object {
    fun fromTestCase(parentId: UniqueId, tc: TestCase): TestCaseDescriptor =
        TestCaseDescriptor(parentId.append("test", tc.name()), tc)
  }
}