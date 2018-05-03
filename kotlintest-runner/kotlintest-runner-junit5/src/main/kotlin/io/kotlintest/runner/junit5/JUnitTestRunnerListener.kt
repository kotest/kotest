package io.kotlintest.runner.junit5

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.runner.jvm.TestSet
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Notifies JUnit Platform of test statuses via a [EngineExecutionListener].
 *
 * JUnit platform supports out of order notification of tests, in that sibling
 * tests can be executing in parallel and updating JUnit out of order.
 *
 * However the gradle test task in intellij gets confused by this and mixes up the results.
 */
class JUnitTestRunnerListener(val listener: EngineExecutionListener, val root: EngineDescriptor) : TestEngineListener {

  data class ResultState(val scope: TestScope, val result: TestResult)

  // contains a mapping of a description to a junit TestDescription
  private val descriptors = ConcurrentHashMap<Description, TestDescriptor>()

  // contains a set of all the tests we have notified as started
  // this is to avoid started the same test twice when we have nested scopes with multiple invocations
  private val started = ConcurrentHashMap.newKeySet<Description>()

  private val results = ConcurrentHashMap.newKeySet<ResultState>()

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    listener.executionStarted(root)
  }

  override fun engineFinished(t: Throwable?) {
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    listener.executionFinished(root, result)
  }

  override fun prepareSpec(spec: Spec) {
    synchronized(this) {
      val descriptor = createDescriptor(spec)
      listener.executionStarted(descriptor)
    }
  }

  override fun completeSpec(spec: Spec, t: Throwable?) {
    // when a spec is completed, we need to complete all the test scopes that we have for that spec
    // squashing all the results for each individual scope as there may be several
    results.filter { it.scope.spec.javaClass == spec.javaClass }
        .sortedBy { it.scope.description.depth() }
        .reversed()
        .forEach {
          val descriptor = descriptors[it.scope.description] ?: getOrCreateDescriptor(it.scope)
          // find an error by priority
          val result = findResult(it.scope.description)
          println("COMPLETING ${it.scope.description} with $result")
          when (result.status) {
            TestStatus.Success -> listener.executionFinished(descriptor, TestExecutionResult.successful())
            TestStatus.Error -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
            TestStatus.Ignored -> listener.executionSkipped(descriptor, result.reason ?: "Test Ignored")
            TestStatus.Failure -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
          }
        }

    // now we can complete the spec
    val descriptor = descriptors[spec.description()]
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    println("ENDING SPEC ${spec.description()} with $result")
    listener.executionFinished(descriptor, result)
  }

  override fun prepareScope(scope: TestScope) {}

  override fun completeScope(scope: TestScope, result: TestResult) {
    // we don't immediately finish a test, we just store the result until we have completed the spec
    // this allows us to handle multiple invocations of the same test scope, deferring the notification
    // to junit until all invocations have completed
    results.add(ResultState(scope, result))
  }

  override fun prepareTestSet(set: TestSet) {
    // we only "start" a test once, the first time a TestSet is seen for the scope, because
    // at that point we know the test cannot be skipped. This is required because JUnit requires
    // that we do not "start" a test that is later marked as skipped.
    synchronized(this) {
      if (!started.contains(set.scope.description)) {
        try {
          started.add(set.scope.description)
          val descriptor = createDescriptor(set.scope)
          listener.executionStarted(descriptor)
        } catch (t: Throwable) {
          t.printStackTrace()
        }
      }
    }
  }

  override fun testRun(set: TestSet, k: Int) {}
  override fun completeTestSet(set: TestSet, result: TestResult) {}

  private fun getOrCreateDescriptor(scope: TestScope): TestDescriptor =
      descriptors.getOrPut(scope.description, { createDescriptor(scope) })

  // returns the most important result for a given scope
  // by searching all the results stored for that scope and child scopes
  private fun findResult(description: Description): TestResult {

    fun findByStatus(status: TestStatus): TestResult? = results
        .filter { it.scope.description == description || description.isAncestorOf(it.scope.description) }
        .filter { it.result.status == status }
        .map { it.result }
        .firstOrNull()

    var result = findByStatus(TestStatus.Error)
    if (result == null)
      result = findByStatus(TestStatus.Failure)
    if (result == null)
      result = findByStatus(TestStatus.Success)
    if (result == null)
      result = findByStatus(TestStatus.Ignored)
    return result!!
  }

  private fun createDescriptor(scope: TestScope): TestDescriptor {

    val parentDescription = scope.description.parent() ?: throw RuntimeException("All test scopes must have a parent")
    val parent = descriptors[parentDescription]!!
    val id = parent.uniqueId.append("test", scope.name)
    val source = MethodSource.from(scope.spec.javaClass.canonicalName, scope.description.fullName())

    val descriptor = object : AbstractTestDescriptor(id, scope.name, source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER_AND_TEST
      override fun mayRegisterTests(): Boolean = true
    }

    descriptors[scope.description] = descriptor

    // we need to synchronize because we don't want to allow multiple specs adding
    // to the root container at the same time
    synchronized(this) {
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
    }

    return descriptor
  }

  private fun createDescriptor(spec: Spec): TestDescriptor {

    val id = root.uniqueId.append("spec", spec.name())
    val source = ClassSource.from(spec.javaClass)

    val descriptor = object : AbstractTestDescriptor(id, spec.name(), source) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
      override fun mayRegisterTests(): Boolean = true
    }

    descriptors[spec.description()] = descriptor

    // we need to synchronize because we don't want to allow multiple specs adding
    // to the root container at the same time
    synchronized(this) {
      root.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
    }

    return descriptor
  }
}