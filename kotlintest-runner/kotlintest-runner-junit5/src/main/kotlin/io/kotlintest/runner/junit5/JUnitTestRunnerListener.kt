package io.kotlintest.runner.junit5

import io.kotlintest.Description
import io.kotlintest.TestScope
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestRunnerListener
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import java.util.concurrent.ConcurrentHashMap

class JUnitTestRunnerListener(val listener: EngineExecutionListener, val root: EngineDescriptor) : TestRunnerListener {

  private val descriptors = ConcurrentHashMap<Description, TestDescriptor>()

  override fun executionStarted(scope: TestScope) {
    val descriptor = createDescriptor(scope)
    try {
      listener.executionStarted(descriptor)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  private fun createDescriptor(scope: TestScope): TestDescriptor {
    val parentDescription = scope.description().parent()
    val parent = if (parentDescription == null) root else descriptors[parentDescription]!!
    val descriptor = when (scope) {
      is TestCase -> {
        val id = parent.uniqueId.append("test", scope.name())
        val source = MethodSource.from(scope.spec.javaClass.name, scope.description.fullName())
        object : AbstractTestDescriptor(id, scope.description.fullName(), source) {
          override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
        }
      }
      is TestContainer -> {
        val id = parent.uniqueId.append("container", scope.name())
        val source = ClassSource.from(scope.sourceClass.java)
        object : AbstractTestDescriptor(id, scope.description.fullName(), source) {
          override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
          override fun mayRegisterTests(): Boolean = true
        }
      }
      else -> throw IllegalStateException()
    }
    descriptors[scope.description()] = descriptor
    parent.addChild(descriptor)
    listener.dynamicTestRegistered(descriptor)
    return descriptor
  }

  override fun executionFinished(scope: TestScope, result: TestResult) {
    val descriptor = descriptors[scope.description()]
    when (descriptor) {
      null -> System.exit(-8)
      else -> when (result.status) {
        TestStatus.Success -> listener.executionFinished(descriptor, TestExecutionResult.successful())
        TestStatus.Error -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
        TestStatus.Ignored -> listener.executionSkipped(descriptor, result.reason ?: "Test Ignored")
        TestStatus.Failure -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
      }
    }
  }

  override fun executionStarted() {
    listener.executionStarted(root)
  }

  override fun executionFinished(t: Throwable?) {
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    listener.executionFinished(root, result)
  }
}