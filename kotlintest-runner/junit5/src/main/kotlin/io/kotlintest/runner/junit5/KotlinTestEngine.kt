package io.kotlintest.runner.junit5

import io.kotlintest.AbstractSpec
import io.kotlintest.ProjectExtensions
import io.kotlintest.Spec
import io.kotlintest.TestCase
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.UriSelector
import org.reflections.util.ClasspathHelper

class KotlinTestEngine : TestEngine {

  companion object {
    const val EngineId = "io.kotlintest"
  }

  // the initial interceptor just invokes the intercept method
  // in the spec itself, which in turn invokes the chain.
  private val initialInterceptor = { spec: Spec, chain: () -> Unit ->
    spec.interceptSpec({ chain() })
  }

  override fun getId(): String = EngineId

  // private fun interceptorChain(spec: AbstractSpec) = createInterceptorChain(spec.specInterceptors, initialInterceptor)

  override fun execute(request: ExecutionRequest) {
    try {
      request.engineExecutionListener.executionStarted(request.rootTestDescriptor)
      ProjectExtensions.beforeAll()
      request.rootTestDescriptor.children.forEach { execute(it, request) }
    } catch (t: Throwable) {
      t.printStackTrace()
      throw t
    } finally {
      try {
        ProjectExtensions.afterAll()
      } finally {
        try {
          request.engineExecutionListener.executionFinished(request.rootTestDescriptor, TestExecutionResult.successful())
        } catch (t: Throwable) {
          t.printStackTrace()
          throw t
        }
      }
    }
  }

  private fun execute(descriptor: TestDescriptor, request: ExecutionRequest) {
    try {
      request.engineExecutionListener.executionStarted(descriptor)
      when (descriptor) {
        is TestContainerDescriptor -> {
          if (descriptor.container.isSpecRoot) {
            descriptor.discover(request.engineExecutionListener)
            val initialInterceptor = { next: () -> Unit -> descriptor.container.spec.interceptSpec(next) }
            val extensions = descriptor.container.spec.specExtensions() + ProjectExtensions.specExtensions()
            val chain = createSpecInterceptorChain(descriptor.container.spec, extensions, initialInterceptor)
            chain { descriptor.children.forEach { execute(it, request) } }
          } else {
            descriptor.discover(request.engineExecutionListener)
            descriptor.children.forEach { execute(it, request) }
          }
        }
        is TestCaseDescriptor -> {
          val runner = TestCaseRunner(request.engineExecutionListener)
          runner.runTest(actualDescriptor(descriptor))
        }
        else -> throw IllegalStateException("$descriptor is not supported")
      }
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (t: Throwable) {
      t.printStackTrace()
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.failed(t))
    }
  }

  private fun actualDescriptor(descriptor: TestCaseDescriptor): TestCaseDescriptor {
    return when (descriptor.testCase.spec.isInstancePerTest()) {
    // if we are using one instance per test then we need a descriptor
    // with a clean instance of the spec
      true -> {

        // we use the prototype spec to create another instance of the spec for this test
        val freshSpec = descriptor.testCase.spec.javaClass.newInstance() as AbstractSpec

        // we get the root scope again for this spec, and find our test case
        val freshTestCase = freshSpec.root().discovery().find {
          when (it) {
            is TestCase -> it.name() == descriptor.testCase.name()
            else -> false
          }
        } as TestCase

        // todo we need to re-run the spec interceptors here

        TestCaseDescriptor(descriptor.id, freshTestCase)
      }
    // if we are /not/ using one instance per test then we can just return the original descriptor
      false -> descriptor
    }
  }

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): TestDescriptor {
    // inside intellij when running a single test, we might be passed a class selector
    // which will be the classname of a spec implementation
    val classes = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

    val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
        request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
        request.getSelectorsByType(UriSelector::class.java).map { it.uri } +
        ClasspathHelper.forClassLoader().toList().map { it.toURI() }
    return TestDiscovery(TestDiscovery.DiscoveryRequest(uris, classes), uniqueId)
  }
}

