package io.kotlintest.runner.jvm

import createSpecInterceptorChain
import io.kotlintest.Project
import io.kotlintest.TestScope
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContainer
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TestRunner(val classes: List<KClass<out Spec>>, val listener: TestRunnerListener) {

  // we execute each spec inside a thread pool so we can parallelise spec execution.
  private val executor = Executors.newFixedThreadPool(Project.parallelism())

  fun execute() {
    try {
      Project.beforeAll()
      listener.executionStarted()
      classes.forEach {
        executor.submit {
          executeSpec(it)
        }
      }
      executor.shutdown()
      executor.awaitTermination(1, TimeUnit.DAYS)
      listener.executionFinished(null)
    } catch (t: Throwable) {
      t.printStackTrace()
      listener.executionFinished(t)
      throw t
    } finally {
      try {
        Project.afterAll()
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }

  internal fun execute(scope: TestScope) {
    when (scope) {
      is TestContainer -> execute(scope)
      is TestCase -> execute(scope)
    }
  }

  private fun executeSpec(kclass: KClass<out Spec>) {

    // we need to instantiate the spec and then notify listeners that a new
    // spec container has been added to the execution
    val spec = createSpecInstance(kclass)

    try {
      // we will invoke the spec interceptors and listeners once as we enter the spec, and then
      // again for each fresh spec if we are using one instance per test
      listener.executionStarted(spec.root())

      runSpecInterception(spec, {
        // creating the spec instance will have invoked `init` on the class setting up
        // the nested scopes. These scopes will now be available on the test context
        val context = AsynchronousTestContext(spec.root())
        spec.root().closure(context)
        context.scopes().forEach { execute(it) }
      })
      listener.executionFinished(spec.root(), TestResult.Success)
    } catch (t: Throwable) {
      listener.executionFinished(spec.root(), TestResult.error(t))
    }
  }

  private fun runSpecInterception(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach {
      try {
        it.beforeSpec(spec.root().description(), spec)
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.root().description, spec)
    val chain = createSpecInterceptorChain(context, extensions) {
      afterInterception()
    }

    chain.invoke()

    listeners.reversed().forEach {
      try {
        it.afterSpec(spec.root().description(), spec)
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }

  private fun execute(container: TestContainer) {
    try {
      listener.executionStarted(container)

      val context = AsynchronousTestContext(container)
      container.closure(context)

      // after the container has been executed, we may have new scopes discovered which should now
      // be executed in turn
      context.scopes().forEach { execute(it) }

      listener.executionFinished(container, TestResult.Success)
    } catch (t: Throwable) {
      listener.executionFinished(container, TestResult.error(t))
    }
  }

  private fun execute(testCase: TestCase) {
    listener.executionStarted(testCase)

    // if we are using one instance per test then we need to create a new spec,
    // re-run the spec interceptors and listeners, and then execute any nested
    // scopes down to the test case
    when (testCase.spec.isInstancePerTest()) {
      true -> {

        val freshSpec = createSpecInstance(testCase.spec::class)

        testCase.description.parents.drop(1)

        // we can now fish out the fresh testcase that pertains to this test
        // it must be a scope directly under the spec root
        val context = AsynchronousTestContext(freshSpec.root())
        freshSpec.root().closure(context)
        val freshTestCase = context.scopes().find { it.name() == testCase.name() } as TestCase

        // now we can re-run interception, and then, straight into the test case
        runSpecInterception(freshSpec, {
          runTest(freshTestCase)
        })
      }
      false -> runTest(testCase)
    }
  }

  private fun runTest(testCase: TestCase) {

    val listeners = listOf(testCase.spec) + testCase.spec.listeners() + Project.listeners()

    val extensions = testCase.config.extensions +
        testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
        Project.testCaseExtensions()

    try {
      listeners.forEach { it.beforeTest(testCase.description()) }
      intercept(testCase, extensions, testCase.config, { result ->
        listeners.reversed().forEach { it.afterTest(testCase.description(), result) }
        listener.executionFinished(testCase, result)
      })
    } catch (t: Throwable) {
      t.printStackTrace()
      listener.executionFinished(testCase, TestResult.error(t))
    }
  }

  private fun intercept(testCase: TestCase,
                        extensions: List<TestCaseExtension>,
                        config: TestCaseConfig,
                        complete: (TestResult) -> Unit) {
    when {
      extensions.isEmpty() -> {
        val result = TestCaseRunner.runTest(testCase.copy(config = config))
        complete(result)
      }
      else -> {
        val context = TestCaseInterceptContext(testCase.description, testCase.spec, config)
        extensions.first().intercept(context, { conf, callback -> intercept(testCase, extensions.drop(1), conf, callback) }, { complete(it) })
      }
    }
  }
}