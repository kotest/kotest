package io.kotlintest.runner.junit5

import io.kotlintest.AbstractSpec
import io.kotlintest.Project
import io.kotlintest.Spec
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId

class KotlinTestEngine : TestEngine {

  // the initial interceptor just invokes the intercept method
  // in the spec itself, which in turn invokes the chain.
  private val initialInterceptor = { spec: Spec, chain: () -> Unit ->
    spec.interceptSpec({ chain() })
  }

  override fun getId(): String = javaClass.canonicalName

  private fun interceptorChain(spec: AbstractSpec) = createInterceptorChain(spec.specInterceptors, initialInterceptor)

  override fun execute(request: ExecutionRequest) {
    Project.beforeAll()
    request.rootTestDescriptor.children.forEach {
      when (it) {
        is TestContainerDescriptor -> execute(it, request)
        else -> throw IllegalStateException("All children of the root test descriptor must be instances of ContainerTestDescriptor; was $it")
      }
    }
    Project.afterAll()
  }

  private fun execute(descriptor: TestContainerDescriptor, request: ExecutionRequest) {
    try {
      request.engineExecutionListener.executionStarted(descriptor)
      descriptor.children.forEach {
        when (it) {
          is TestContainerDescriptor -> execute(it, request)
          is TestCaseDescriptor -> execute(it, request)
          else -> throw IllegalStateException("$it is not supported")
        }
      }
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (throwable: Throwable) {
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
    }
  }

  private fun execute(descriptor: TestCaseDescriptor, request: ExecutionRequest) {
    try {
      request.engineExecutionListener.executionStarted(descriptor)
      val runner = TestCaseRunner(request.engineExecutionListener)
      runner.runTest(actualDescriptor(descriptor, request))
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (throwable: Throwable) {
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
    }
  }

  private fun actualDescriptor(descriptor: TestCaseDescriptor, request: ExecutionRequest): TestCaseDescriptor {
    return when (descriptor.testCase.spec.isInstancePerTest()) {
    // if we are using one instance per test then we need a descriptor
    // with a clean instance of the spec
      true -> {

        // we use the prototype spec to create another instance of the spec for this test
        val freshSpec = descriptor.testCase.spec.javaClass.newInstance() as AbstractSpec

        // we then create a new spec-level container descriptor for this spec
        // the id should be the same as for the existing spec
        val container = TestContainerDescriptor.fromTestContainer(request.rootTestDescriptor.uniqueId, freshSpec.root())

        // todo we need to re-run the spec interceptors here

        // and then we can get the test case out of that new container
        container.findByUniqueId(descriptor.uniqueId)
            .orElseThrow {
              IllegalStateException("Test case with id ${descriptor.id} cannot be found in spec container clone $container")
            } as TestCaseDescriptor
      }
    // if we are /not/ using one instance per test then we can just return the original descriptor
      false -> descriptor
    }
  }

  override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor =
      SpecDiscovery(discoveryRequest, uniqueId)
}

