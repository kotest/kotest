package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.Spec
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TestEngine(val classes: List<KClass<out Spec>>, val listener: TestEngineListener) {

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
        val executor = when {
          spec.isInstancePerTest() -> SingleInstanceSpecExecutor(listener)
          else -> SharedInstanceSpecExecutor(listener)
        }
        specExecutor.submit {
          executor.execute(spec)
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

//  fun executeTestCase(testScope: TestScope) {
//    // if we are using one instance per test then we need to create a new spec,
//    // re-run the spec interceptors and listeners, and then locate the fresh closure instance
//    when (testScope.spec.isInstancePerTest()) {
//      true -> {
//
//        val freshSpec = createSpecInstance(testScope.spec::class)
//
//        // after the spec is instantiated, the root scopes will be available
//        val freshTestCase = freshSpec.testCases().find { it.name == testScope.description.name }!!
//        // now we can re-run interception, and then, straight into the test case
//        runSpecInterception(freshSpec, {
//          runTestCaseInstance(freshTestCase)
//        })
//      }
//      false -> runTestCaseInstance(testScope)
//    }
//  }
}