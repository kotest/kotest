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
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Notifies JUnit Platform of test statuses via a [EngineExecutionListener].
 *
 * JUnit platform supports out of order notification of tests, in that sibling
 * tests can be executing in parallel and updating JUnit out of order. However the gradle test
 * task gets confused if we are executing two or more tests directly under the root at once.
 * Therefore we must queue up notifications until each spec is completed.
 *
 * Gradle test run observations:
 *
 * Top level descriptors must have a source attached or the execution will fail with a parent attached exception.
 * Type.CONTAINER_TEST doesn't seem to work as a top level descriptor, it will hang
 * leaf tests do not need to be completed but they will be marked as uncomplete in intellij.
 * Dynamic test can be called after or before addChild.
 * A Type.TEST can be a child of a Type.TEST.
 * Intermediate Type.CONTAINER seem to be ignored in output.
 * Intermediate containers can have same class source as parent.
 * Type.TEST as top level seems to hang.
 * A TEST doesn't seem to be able to have the same source as a parent, or hang.
 * A TEST seems to hang if it has a ClassSource.
 * MethodSource seems to be ok with a TEST.
 * Container test names seem to be taken from a Source.
 * Nested tests are outputted as siblings.
 * Can complete executions out of order.
 * Child failures will fail parent CONTAINER.
 * Sibling containers can start and finish in parallel.
 *
 * Intellij runner observations:
 *
 * Intermediate Type.CONTAINERs are shown.
 * Intermediate Type.TESTs are shown.
 * A Type.TEST can be a child of a Type.TEST
 * MethodSource seems to be ok with a TEST.
 * Container test names seem to be taken from the name property.
 * Nested tests are outputted as nested.
 * Child failures will not fail containing TEST.
 * child failures will fail a containing CONTAINER.
 * Call addChild _before_ registering test otherwise will appear in the display out of order.
 * Must start tests after their parent or they can go missing.
 * Sibling containers can start and finish in parallel.
 */
class JUnitTestRunnerListener(val listener: EngineExecutionListener, val root: EngineDescriptor) : TestEngineListener {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  data class ResultState(val scope: TestScope, val result: TestResult)

  // contains a mapping of a description to a junit TestDescription
  private val descriptors = ConcurrentHashMap<Description, TestDescriptor>()

  // contains a set of all the tests we have notified as started
  // this is to avoid started the same test twice when we have nested scopes with multiple invocations
  private val started = ConcurrentHashMap.newKeySet<Description>()

  private val results = ConcurrentHashMap.newKeySet<ResultState>()

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    logger.debug("Engine started; classes=[$classes]")
    listener.executionStarted(root)

//    val a = object : AbstractTestDescriptor(root.uniqueId.append("container", "a"), "a", ClassSource.from("com.sksamuel.Wobble")) {
//      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
//    }
//
//    val b = object : AbstractTestDescriptor(root.uniqueId.append("container", "b"), "b") {
//      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
//    }
//
//    val c = object : AbstractTestDescriptor(a.uniqueId.append("test", "ccfff"), "ccsdfef", MethodSource.from("com.sksamuel.WWWW", "fo")) {
//      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
//    }
//
//    val d = object : AbstractTestDescriptor(a.uniqueId.append("test", "d"), "d") {
//      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
//    }
//
//    val e = object : AbstractTestDescriptor(a.uniqueId.append("test", "e"), "e") {
//      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
//    }
//
//    root.addChild(a)
//    listener.dynamicTestRegistered(a)
//
//    root.addChild(b)
//    listener.dynamicTestRegistered(b)
//
//    a.addChild(c)
//    listener.dynamicTestRegistered(c)
//
//    b.addChild(d)
//    listener.dynamicTestRegistered(d)
//
//    c.addChild(e)
//    listener.dynamicTestRegistered(e)
//
//    listener.executionStarted(a)
//    listener.executionStarted(b)
//    listener.executionStarted(c)
//    listener.executionStarted(d)
//    listener.executionStarted(e)
//    listener.executionFinished(e, TestExecutionResult.successful())
//    listener.executionFinished(d, TestExecutionResult.successful())
//    listener.executionFinished(c, TestExecutionResult.successful())
//    listener.executionFinished(a, TestExecutionResult.successful())
//    listener.executionFinished(b, TestExecutionResult.failed(RuntimeException()))
  }

  override fun engineFinished(t: Throwable?) {
    logger.debug("Engine finished; throwable=[$t]")
    val result = if (t == null) TestExecutionResult.successful() else TestExecutionResult.failed(t)
    listener.executionFinished(root, result)
  }

  override fun prepareSpec(spec: Spec) {
    logger.debug("prepareSpec [$spec]")
    val descriptor = createSpecDescriptor(spec)
    listener.executionStarted(descriptor)
  }

  override fun completeSpec(spec: Spec, t: Throwable?) {
    logger.debug("completeSpec [$spec]")

    // we wait until the spec is completed before completing all child scopes, because we need
    // to wait until all possible invocations of each scope have completed.
    val descriptions = started.filter { spec.description().isAncestorOf(it) }
    logger.debug("spec contains ${descriptions.size} child descriptions")

    // for each description we can grab the best result and use that
    descriptions.sortedBy { it.depth() }
        .reversed()
        .forEach {
          val descriptor = descriptors[it] ?: getOrCreateDescriptor(it)
          // find an error by priority
          val result = findResult(it) ?: throw RuntimeException("Every description must have a result")
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
    // we only "start" a test once, the first time a TestSet is seen for a scope, because
    // at that point we know the test cannot be skipped. This is required because JUnit requires
    // that we do not "start" a test that is later marked as skipped.
    synchronized(this) {
      if (!started.contains(set.scope.description)) {
        started.add(set.scope.description)
        val descriptor = createScopeDescriptor(set.scope.description)
        listener.executionStarted(descriptor)
      }
    }
  }

  override fun testRun(set: TestSet, k: Int) {}
  override fun completeTestSet(set: TestSet, result: TestResult) {}

  private fun getOrCreateDescriptor(description: Description): TestDescriptor =
      descriptors.getOrPut(description, { createScopeDescriptor(description) })

  // returns the most important result for a given scope
  // by searching all the results stored for that scope and child scopes
  private fun findResult(description: Description): TestResult? {

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
    return result
  }

  private fun createScopeDescriptor(description: Description): TestDescriptor {

    val parentDescription = description.parent() ?: throw RuntimeException("All test scopes must have a parent")
    val parent = descriptors[parentDescription]!!
    val id = parent.uniqueId.append("test", description.name)

    val descriptor = object : AbstractTestDescriptor(id, description.name) {
      override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
      override fun mayRegisterTests(): Boolean = true
    }

    descriptors[description] = descriptor

    synchronized(this) {
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
    }

    return descriptor
  }

  private fun createSpecDescriptor(spec: Spec): TestDescriptor {

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