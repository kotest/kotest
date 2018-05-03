package io.kotlintest.runner.jvm

import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [TestEngine] lifecycle.
 */
interface TestEngineListener {

  /**
   * Is invoked when the [TestEngine] is starting execution.
   *
   * @param classes the [Spec] classes that will be used by the [TestEngine].
   */
  fun engineStarted(classes: List<KClass<out Spec>>)

  /**
   * Is invoked when the [TestEngine] has finished execution.
   *
   * If an unrecoverable error was detected during execution then it will be passed
   * as the parameter to the engine.
   */
  fun engineFinished(t: Throwable?)

  /**
   * Is invoked once per [Spec] when the [TestEngine] is preparing
   * to submit the spec for execution to a [SpecExecutor].
   */
  fun prepareSpec(spec: Spec)

  /**
   * Is invoked once per [Spec] to indicate that all [TestScope] instances
   * of the spec have returned and the [SpecExecutor] has completed.
   */
  fun completeSpec(spec: Spec, t: Throwable?)

  /**
   * Executed each time a [TestScope] has been entered from a parent scope.
   *
   * If a parent scope has been configured with multiple invocations, then this
   * function will be executed once per parent invocation.
   */
  fun prepareScope(scope: TestScope)

  /**
   * Executed each time a [TestScope] has completed.
   * This function will always be executed even if the scope is skipped.
   * The result passed in here will be after test scope interception.
   *
   * If a parent scope has been configured with multiple invocations, then this
   * function will be executed once per parent invocation.
   */
  fun completeScope(scope: TestScope, result: TestResult)

  /**
   * Invoked each time a new [TestSet] is being prepared for execution.
   * This function will only be invoked if a test scope is actually being executed, and not skipped.
   */
  fun prepareTestSet(set: TestSet)

  /**
   * Invoked for each run of a [TestSet].
   *
   * If a scope is configured with invocations = k, then this function
   * will be invoked k times.
   *
   * @param k indicates which position in the [TestSet] this execution is.
   */
  fun testRun(set: TestSet, k: Int)

  /**
   * Invoked when all the runs of a [TestSet] have completed.
   * This function will only be invoked if a test scope is actually being executed.
   * The result passed in here is the result directly from the test run, before any interception.
   */
  fun completeTestSet(set: TestSet, result: TestResult)

}