package io.kotlintest.runner.junit5

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestEngineListener
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

class JUnitTestRunnerListener(val listener: EngineExecutionListener, val root: EngineDescriptor) : TestEngineListener {

  private val descriptors = ConcurrentHashMap<Description, TestDescriptor>()
  private val results = ConcurrentHashMap<Description, TestResult>()

  override fun executionStarted() {
    listener.executionStarted(root)
  }

  override fun executionFinished(t: Throwable?) {
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    listener.executionFinished(root, result)
  }

  override fun executionStarted(spec: Spec) {
    val descriptor = createDescriptor(spec)
    try {
      listener.executionStarted(descriptor)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  override fun executionFinished(spec: Spec, t: Throwable?) {
    val descriptor = descriptors[spec.description()]
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    listener.executionFinished(descriptor, result)
  }

  override fun executionStarted(scope: TestScope) {
    val descriptor = createDescriptor(scope)
    try {
      listener.executionStarted(descriptor)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  override fun executionFinished(scope: TestScope, result: TestResult) {

    fun storeResult(description: Description, result: TestResult) {
      results[description] = result
      val parent = description.parent()
      if (parent != null)
        storeResult(parent, result)
    }

    // if we have a failed result, then for all parents we need to store this, so we
    // can fail the parents later as they complete
    when (result.status) {
      TestStatus.Failure -> storeResult(scope.description, result)
      TestStatus.Error -> storeResult(scope.description, result)
      else -> {
      }
    }

    // check the stored list of results which could have been set by the child of this test case
    // if the child test did store a result (failed or aborted) then we need to use it here as well
    // in order to 'propagate' up the failures
    var resultp = results[scope.description]
    if (resultp == null)
      resultp = result

    val descriptor = descriptors[scope.description]
    when (descriptor) {
      null -> System.exit(-108)
      else -> when (resultp.status) {
        TestStatus.Success -> listener.executionFinished(descriptor, TestExecutionResult.successful())
        TestStatus.Error -> listener.executionFinished(descriptor, TestExecutionResult.failed(resultp.error))
        TestStatus.Ignored -> listener.executionSkipped(descriptor, result.reason ?: "Test Ignored")
        TestStatus.Failure -> listener.executionFinished(descriptor, TestExecutionResult.failed(resultp.error))
      }
    }
  }

  private fun createDescriptor(scope: TestScope): TestDescriptor {
    val parentDescription = scope.description.parent()
    val parent = if (parentDescription == null) root else descriptors[parentDescription]!!
    val id = parent.uniqueId.append("test", scope.name)
    val f = File(scope.spec.javaClass.protectionDomain.codeSource.location.path)
    val source = FileSource.from(f, FilePosition.from(max(1, scope.line)))
    val descriptor = object : AbstractTestDescriptor(id, scope.name, source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER_AND_TEST
    }
    descriptors[scope.description] = descriptor
    parent.addChild(descriptor)
    listener.dynamicTestRegistered(descriptor)
    return descriptor
  }

  private fun createDescriptor(spec: Spec): TestDescriptor {
    val parentDescription = spec.description().parent()
    val parent = if (parentDescription == null) root else descriptors[parentDescription]!!
    val id = parent.uniqueId.append("spec", spec.name())
    val source = ClassSource.from(spec.javaClass)
    val descriptor = object : AbstractTestDescriptor(id, spec.name(), source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
      override fun mayRegisterTests(): Boolean = true
    }
    descriptors[spec.description()] = descriptor
    parent.addChild(descriptor)
    listener.dynamicTestRegistered(descriptor)
    return descriptor
  }
}