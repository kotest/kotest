package io.kotlintest.runner.jvm

import createSpecInterceptorChain
import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestScope
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TestRunner(val classes: List<KClass<out Spec>>, val listener: TestRunnerListener) {

  // we execute each spec inside a thread pool so we can parallelise spec execution.
  private val specExecutor = Executors.newFixedThreadPool(Project.parallelism())

  fun execute() {
    try {
      Project.beforeAll()
      listener.executionStarted()
      classes.forEach {
        // we need to instantiate the spec outside of the executor
        // so the error will be caught and shutdown the executor
        val spec = createSpecInstance(it)
        specExecutor.submit {
          executeSpec(spec)
        }
      }
      specExecutor.shutdown()
      specExecutor.awaitTermination(1, TimeUnit.DAYS)
      listener.executionFinished(null)
    } catch (t: Throwable) {
      t.printStackTrace()
      // if we pick up an error in a spec that isn't caught as part of a test case (usually
      // inside the init method of a spec) then we immediately terminate the test run
      listener.executionFinished(t)
      specExecutor.shutdownNow()
      specExecutor.awaitTermination(1, TimeUnit.DAYS)
      throw t
    } finally {
      try {
        Project.afterAll()
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }

  private fun executeSpec(spec: Spec) {
    try {
      listener.executionStarted(spec)
      // we will invoke the spec interceptors and listeners once as we enter the spec, and then
      // again for each fresh spec if we are using one instance per test
      runSpecInterception(spec, {
        // creating the spec instance will have invoked the init block, resulting
        // in the top level test cases being available on the spec class
        spec.testCases().forEach { executeTestCase(it) }
      })
      listener.executionFinished(spec, null)
    } catch (t: Throwable) {
      listener.executionFinished(spec, t)
    }
  }

  private fun runSpecInterception(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach { it.beforeSpec(spec.description(), spec) }

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.description(), spec)
    val chain = createSpecInterceptorChain(context, extensions) {
      afterInterception()
    }

    chain.invoke()

    listeners.reversed().forEach { it.afterSpec(spec.description(), spec) }
  }

  fun executeTestCase(testScope: TestScope) {
    // if we are using one instance per test then we need to create a new spec,
    // re-run the spec interceptors and listeners, and then locate the fresh closure instance
    when (testScope.spec.isInstancePerTest()) {
      true -> {

        val freshSpec = createSpecInstance(testScope.spec::class)

        // after the spec is instantiated, the root scopes will be available
        val freshTestCase = freshSpec.testCases().find { it.name == testScope.description.name }!!
        // now we can re-run interception, and then, straight into the test case
        runSpecInterception(freshSpec, {
          runTestCaseInstance(freshTestCase)
        })
      }
      false -> runTestCaseInstance(testScope)
    }
  }

  private fun intercept(testScope: TestScope,
                        extensions: List<TestCaseExtension>,
                        config: TestCaseConfig,
                        onComplete: (TestResult) -> Unit) {

    when {
      extensions.isEmpty() -> {
        val result = if (config.enabled && Project.tags().isActive(config.tags)) {
          testCaseInvocations(testScope, config)
        } else {
          TestResult.Ignored
        }
        onComplete(result)
      }
      else -> {
        val context = TestCaseInterceptContext(testScope.description, testScope.spec, config)
        extensions.first().intercept(context, { conf, callback -> intercept(testScope, extensions.drop(1), conf, callback) }, { onComplete(it) })
      }
    }
  }

  private fun runTestCaseInstance(testScope: TestScope) {
    try {

      listener.executionStarted(testScope)
      val listeners = listOf(testScope.spec) + testScope.spec.listeners() + Project.listeners()

      val extensions = testScope.config.extensions +
          testScope.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      listeners.forEach { it.beforeTest(testScope.description) }

      intercept(testScope, extensions, testScope.config, { result ->
        listeners.reversed().forEach { it.afterTest(testScope.description, result) }
        listener.executionFinished(testScope, result)
      })

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.executionFinished(testScope, TestResult.error(t))
    }
  }

  private fun testCaseInvocations(testScope: TestScope, config: TestCaseConfig): TestResult {
    // each test runs inside its own execution service, so we can easily support multiple threads
    val executor = Executors.newFixedThreadPool(config.threads)
    val metadata = ConcurrentHashMap<String, Any?>()
    val errors = mutableListOf<Throwable>()

    for (j in 1..config.invocations) {
      executor.execute {
        val context = object : TestContext() {
          override fun description(): Description = testScope.description
          override fun registerTestScope(scope: TestScope) {
            executeTestCase(scope)
          }
        }
        try {
          testScope.test(context)
          context.blockUntilReady()
          val error = context.error()
          if (error != null)
            errors.add(error)
        } catch (t: Throwable) {
          errors.add(t)
        } finally {
          metadata.putAll(context.metaData())
        }
      }
    }

    executor.shutdown()
    val terminated = executor.awaitTermination(config.timeout.seconds, TimeUnit.SECONDS)
    return buildTestResult(terminated, config.timeout, errors.toList(), metadata)
  }

  private fun buildTestResult(terminated: Boolean, timeout: Duration, errors: List<Throwable>, metadata: Map<String, Any?>): TestResult {
    return if (!terminated) {
      TestResult(TestStatus.Error, TestTimedOutException(timeout.seconds, TimeUnit.SECONDS), null, metadata)
    } else {
      val first = errors.firstOrNull()
      when (first) {
        null -> TestResult(TestStatus.Success, null, null, metadata)
        is AssertionError -> TestResult(TestStatus.Failure, first, null, metadata)
        else -> TestResult(TestStatus.Error, first, null, metadata)
      }
    }
  }
}