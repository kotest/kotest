package io.kotlintest.runner.junit5

import createSpecInterceptorChain
import io.kotlintest.AbstractSpec
import io.kotlintest.Project
import io.kotlintest.SpecScope
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContainer
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.UriSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.reflections.util.ClasspathHelper
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class KotlinTestEngine : TestEngine {

  companion object {
    const val EngineId = "io.kotlintest"
  }

  override fun getId(): String = EngineId

  private val executor = Executors.newFixedThreadPool(Project.parallelism())

  override fun execute(request: ExecutionRequest) {
    println(request.configurationParameters)
    request.engineExecutionListener.executionStarted(request.rootTestDescriptor)
    try {
      Project.beforeAll()
      // each child of the root is a spec, which we execute in a thread pool so we
      // can parallelise spec execution.
      request.rootTestDescriptor.children.forEach {
        executor.submit {
          execute(it, request)
        }
      }
      executor.shutdown()
      executor.awaitTermination(1, TimeUnit.DAYS)
    } catch (t: Throwable) {
      t.printStackTrace()
      throw t
    } finally {
      Project.afterAll()
    }
    request.engineExecutionListener.executionFinished(request.rootTestDescriptor, TestExecutionResult.successful())
  }

  private fun execute(descriptor: TestDescriptor, request: ExecutionRequest) {
    when (descriptor) {
      is SpecTestDescriptor -> execute(descriptor, request)
      is TestContainerDescriptor -> execute(descriptor, request)
      is TestCaseDescriptor -> execute(descriptor, request)
      else -> throw IllegalStateException()
    }
  }

  private fun execute(descriptor: SpecTestDescriptor, request: ExecutionRequest) {
    // we will invoke the spec interceptors and listeners once as we enter the spec, and then
    // again for each fresh spec if we are using one instance per test
    request.engineExecutionListener.executionStarted(descriptor)
    runSpecInterception(descriptor.scope, {
      descriptor.children.forEach { execute(it, request) }
    })
    request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
  }

  private fun runSpecInterception(scope: SpecScope, afterInterception: () -> Unit) {
    val listeners = listOf(scope.spec) + scope.spec.listeners() + Project.listeners()
    listeners.forEach {
      try {
        it.beforeSpec(scope.description(), scope.spec())
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }

    val extensions: List<SpecExtension> =
        scope.spec.extensions().filterIsInstance<SpecExtension>() +
            Project.specExtensions()

    val context = SpecInterceptContext(scope.description, scope.spec)

    val chain = createSpecInterceptorChain(context, extensions) {
      afterInterception()
    }

    chain.invoke()

    listeners.reversed().forEach {
      try {
        it.afterSpec(scope.description(), scope.spec())
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }

  private fun execute(descriptor: TestContainerDescriptor, request: ExecutionRequest) {
    request.engineExecutionListener.executionStarted(descriptor)

    val context = AsynchronousTestContext(descriptor.container)
    descriptor.container.closure(context)

    // after the container has returned, we should add any nested scopes to the junit test plan
    context.scopes().forEach {
      val newDescriptor = when (it) {
        is TestContainer -> TestContainerDescriptor.fromTestContainer(descriptor.uniqueId, it)
        is TestCase -> TestCaseDescriptor.fromTestCase(descriptor.uniqueId, it)
        else -> throw IllegalArgumentException()
      }
      descriptor.addChild(newDescriptor)
      request.engineExecutionListener.dynamicTestRegistered(newDescriptor)
    }

    descriptor.children.forEach { execute(it, request) }
    request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
  }

  private fun execute(descriptor: TestCaseDescriptor, request: ExecutionRequest) {

    // we always start even if we then skip
    request.engineExecutionListener.executionStarted(descriptor)

    // if we are using one instance per test then we need to create a new spec,
    // and re-run the spec interceptors and listeners
    when (descriptor.testCase.spec.isInstancePerTest()) {
      true -> {

        // we use the prototype spec to create another instance of the spec for this test
        val freshSpec = descriptor.testCase.spec.javaClass.newInstance() as AbstractSpec

        // we can now fish out the fresh testcase that pertains to this test
        // it must be a scope directly under the spec root
        val freshTestCase = freshSpec.root().scopes.find { it.name() == descriptor.testCase.name() } as TestCase

        // now we can re-run interception, and then, straight into the test case
        runSpecInterception(freshSpec.root(), {
          runTest(TestCaseDescriptor(descriptor.id, freshTestCase), request.engineExecutionListener)
        })
      }
      false -> runTest(descriptor, request.engineExecutionListener)
    }
  }

  private fun runTest(descriptor: TestCaseDescriptor, listener: EngineExecutionListener) {

    val listeners = listOf(descriptor.testCase.spec) + descriptor.testCase.spec.listeners() + Project.listeners()
    listeners.forEach {
      try {
        it.beforeTest(descriptor.testCase.description())
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }

    fun complete(result: TestResult) {

      when (result.status) {
        TestStatus.Success -> listener.executionFinished(descriptor, TestExecutionResult.successful())
        TestStatus.Error -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
        TestStatus.Ignored -> listener.executionSkipped(descriptor, result.reason ?: "Test Ignored")
        TestStatus.Failure -> listener.executionFinished(descriptor, TestExecutionResult.failed(result.error))
      }

      listeners.reversed().forEach {
        try {
          it.afterTest(descriptor.testCase.description(), result)
        } catch (t: Throwable) {
          t.printStackTrace()
        }
      }
    }

    fun intercept(extensions: List<TestCaseExtension>,
                  config: TestCaseConfig,
                  complete: (TestResult) -> Unit) {
      when {
        extensions.isEmpty() -> {
          val result = TestCaseRunner.runTest(descriptor.testCase.copy(config = config))
          complete(result)
        }
        else -> {
          val context = TestCaseInterceptContext(
              descriptor.testCase.description,
              descriptor.testCase.spec,
              config)
          extensions.first().intercept(context, { conf, callback -> intercept(extensions.drop(1), conf, callback) }, { complete(it) })
        }
      }
    }

    val extensions: List<TestCaseExtension> = descriptor.testCase.config.extensions +
        descriptor.testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
        Project.testCaseExtensions()

    intercept(extensions, descriptor.testCase.config, { complete(it) })
  }

  private var result: EngineDescriptor? = null

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): EngineDescriptor {
    if (result == null) {
      // inside intellij when running a single test, we might be passed a class selector
      // which will be the classname of a spec implementation
      val classes = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

      val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
          request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
          request.getSelectorsByType(UriSelector::class.java).map { it.uri } +
          ClasspathHelper.forClassLoader().toList().map { it.toURI() }

      result = TestDiscovery(TestDiscovery.DiscoveryRequest(uris, classes), uniqueId)
    }
    return result!!
  }
}


