package io.kotlintest.runner.junit5

import io.kotlintest.AbstractSpec
import io.kotlintest.Project
import io.kotlintest.Spec
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.PackageSelector
import java.lang.reflect.Modifier

class KotlinTestEngine : TestEngine {

  // the initial interceptor just invokes the intercept method
  // in the spec itself, which in turn invokes the chain.
  private val initialInterceptor = { spec: Spec, chain: () -> Unit ->
    spec.interceptSpec({ chain() })
  }

  override fun getId(): String = "io.kotlintest"

  private fun interceptorChain(spec: AbstractSpec) = createInterceptorChain(spec.specInterceptors, initialInterceptor)

  override fun execute(request: ExecutionRequest) {
    Project.beforeAll()
    request.rootTestDescriptor.children.forEach {
      when (it) {
        is TestContainerDescriptor -> execute(it, request.engineExecutionListener)
        else -> throw IllegalStateException("All children of the root test descriptor must be instances of ContainerTestDescriptor; was $it")
      }
    }
    Project.afterAll()
  }

  private fun execute(descriptor: TestContainerDescriptor, listener: EngineExecutionListener) {
    try {
      listener.executionStarted(descriptor)
      descriptor.children.forEach {
        when (it) {
          is TestContainerDescriptor -> execute(it, listener)
          is TestCaseDescriptor -> execute(it, listener)
          else -> throw IllegalStateException("$it is not supported")
        }
      }
      listener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (throwable: Throwable) {
      listener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
    }
  }

  private fun execute(descriptor: TestCaseDescriptor, listener: EngineExecutionListener) {
    try {
      listener.executionStarted(descriptor)
      val runner = TestCaseRunner(listener)
      runner.runTest(actualDescriptor(descriptor))
      listener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (throwable: Throwable) {
      listener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
    }
  }

  private fun actualDescriptor(descriptor: TestCaseDescriptor): TestCaseDescriptor {
    return when (descriptor.testCase.spec.isInstancePerTest()) {
    // if we are using one instance per test then we need a descriptor
    // with a clean instance of the spec
      true -> {
        // we use the prototype spec to create another instance of the spec for this test
        val freshSpec = descriptor.testCase.spec.javaClass.newInstance() as AbstractSpec

        // we can then create a new test descriptor for this spec
        val container = TestContainerDescriptor.fromSpec(freshSpec)

        // and then we can get the test case out of that new container
        container.findByUniqueId(descriptor.uniqueId).orElseThrow { IllegalStateException("Test case cannot be found in spec clone") } as TestCaseDescriptor
      }
    // if we are /not/ using one instance per test then we can just return the original descriptor
      false -> descriptor
    }
  }

  override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {

    val isSpec: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) && !Modifier.isAbstract(it.modifiers) }

    val specs: List<Class<Spec>> = discoveryRequest.getSelectorsByType(PackageSelector::class.java).flatMap {
      ReflectionUtils.findAllClassesInPackage(it.packageName, isSpec, { true }).map {
        it as Class<Spec>
      }
    }

    val root = RootTestDescriptor(uniqueId, "KotlinTest")
    specs.forEach {
      val spec: Spec = it.newInstance()
      root.addChild(TestContainerDescriptor.fromSpec(spec))
    }
    return root
  }
}

