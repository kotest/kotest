package io.kotlintest.runner.junit5

import io.kotlintest.SpecScope
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestRunnerListener
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import java.util.*

class JUnitTestRunnerListener(val listener: EngineExecutionListener, val root: EngineDescriptor) : TestRunnerListener {

  private val stack = Stack<TestDescriptor>().apply { push(root) }

  override fun executionStarted(scope: TestScope) {
    val descriptor = createDescriptor(scope)
    stack.peek().addChild(descriptor)
    stack.push(descriptor)
    listener.dynamicTestRegistered(descriptor)
    listener.executionStarted(descriptor)
  }

  private fun createDescriptor(scope: TestScope): TestDescriptor {
    val (id, source, type) = when (scope) {
      is TestCase -> {
        val id = stack.peek().uniqueId.append("test", scope.name())
        val source = ClassSource.from(scope.spec.javaClass)
        Triple(id, source, TestDescriptor.Type.TEST)
      }
      is TestContainer -> {
        val id = stack.peek().uniqueId.append("container", scope.name())
        val source = ClassSource.from(scope.sourceClass.java)
        Triple(id, source, TestDescriptor.Type.CONTAINER)
      }
      is SpecScope -> {
        val id = stack.peek().uniqueId.append("spec", scope.name())
        val source = ClassSource.from(scope.javaClass)
        Triple(id, source, TestDescriptor.Type.CONTAINER)
      }
      else -> throw IllegalStateException()
    }
    return object : AbstractTestDescriptor(id, scope.name(), source) {
      override fun getType(): TestDescriptor.Type = type
      override fun mayRegisterTests() = true
    }
  }

  override fun executionFinished(scope: TestScope, result: TestResult) {
    val descriptor = stack.pop()
    when (result.status) {
      TestStatus.Success -> listener.executionFinished(descriptor, TestExecutionResult.successful())
      TestStatus.Error -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
      TestStatus.Ignored -> listener.executionSkipped(descriptor, result.reason ?: "Test Ignored")
      TestStatus.Failure -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
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