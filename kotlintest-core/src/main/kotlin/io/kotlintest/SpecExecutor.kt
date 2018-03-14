package io.kotlintest

import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult

interface SpecExecutor {
  fun execute(specDescriptor: SpecDescriptor, listener: EngineExecutionListener)
}

// a basic implementation that supports spec interceptors, traverses the specs, and for each detected test invokes 'run'
abstract class AbstractSpecExecutor : SpecExecutor {

  protected abstract fun run(testCase: TestCaseDescriptor, listener: EngineExecutionListener)

  // the initial interceptor just invokes the intercept method in the spec itself, which in
  // turn invokes the chain.
  private val initialInterceptor = { spec: Spec, chain: () -> Unit ->
    spec.interceptSpec({ chain() })
  }

  private fun interceptorChain(spec: Spec) = createInterceptorChain(spec.specInterceptors, initialInterceptor)

  private fun run(descriptor: TestDescriptor, listener: EngineExecutionListener) {
    try {
      listener.executionStarted(descriptor)
      descriptor.children.forEach {
        when (it) {
          is ContainerTestDescriptor -> run(it, listener)
          is TestCaseDescriptor -> run(it, listener)
        }
      }
      listener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (throwable: Throwable) {
      listener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
    }
  }

  override fun execute(specDescriptor: SpecDescriptor, listener: EngineExecutionListener) {
    try {
      interceptorChain(specDescriptor.spec).invoke(specDescriptor.spec, {
        run(specDescriptor, listener)
      })
    } catch (throwable: Throwable) {
      // an exception here means the entire spec failed
      listener.executionFinished(specDescriptor, TestExecutionResult.failed(throwable))
    }
  }
}

object SharedSpecExecutor : AbstractSpecExecutor() {
  override fun run(testCase: TestCaseDescriptor, listener: EngineExecutionListener) {
    val runner = TestRunner(listener)
    runner.runTest(testCase.spec, testCase)
  }
}

object OneInstanceSpecExecutor : AbstractSpecExecutor() {
  override fun run(testCase: TestCaseDescriptor, listener: EngineExecutionListener) {

    // we use the prototype to create another instance of the spec for this test
    val actualSpec = testCase.spec.javaClass.newInstance()
    // we then need to get the equivalent test case from the new instance,
    // so we can be sure the fields properly match up
    val actualTestCase = actualSpec.specDescriptor.findByUniqueId(testCase.uniqueId).get() as TestCaseDescriptor

    val runner = TestRunner(listener)
    runner.runTest(actualSpec, actualTestCase)
  }
}