package io.kotlintest.runner.junit5

import createSpecInterceptorChain
import io.kotlintest.AbstractSpec
import io.kotlintest.Project
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class KotlinTestEngine : TestEngine {

  companion object {
    const val EngineId = "io.kotlintest"
  }

  override fun getId(): String = EngineId

  private val executor = Executors.newFixedThreadPool(Project.parallelism())

  override fun execute(request: ExecutionRequest) {
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
    try {
      request.engineExecutionListener.executionStarted(descriptor)

      when (descriptor) {
        is TestContainerDescriptor -> {


          val context = JUnit5TestContext(descriptor, request.engineExecutionListener, descriptor.container)
          descriptor.container.closure(context)

          // if this container is for the spec root and we're using a shared instance, then we can
          // invoke the spec interceptors here; otherwise the spec inteceptors will need to run each
          // time we create a fresh instance of the spec class
          if (descriptor.container.isSpecRoot && !descriptor.container.spec.isInstancePerTest()) {
            val initialInterceptor = { next: () -> Unit -> descriptor.container.spec.interceptSpec(next) }
            val extensions = descriptor.container.spec.specExtensions() + Project.specInterceptors()
            val chain = createSpecInterceptorChain(descriptor.container.spec, extensions, initialInterceptor)
            chain {
              descriptor.children.forEach { execute(it, request) }
            }

            val listeners = descriptor.container.spec.listeners() + Project.listeners()
            listeners.forEach { it.specStarted(descriptor.container.description(), descriptor.container.spec()) }

          } else {
            descriptor.children.forEach { execute(it, request) }
          }
        }
        is TestCaseDescriptor -> {

          val listeners = descriptor.testCase.spec.listeners() + Project.listeners()

          when (descriptor.testCase.spec.isInstancePerTest()) {
            true -> {
              // we use the prototype spec to create another instance of the spec for this test
              val freshSpec = descriptor.testCase.spec.javaClass.newInstance() as AbstractSpec

              // we can now execute each closure as we go down the tree, any created scopes
              // should not be added to junit as it already knows about them, but inside we
              // can store them so we can grab out the fresh closure
              val context = AccumulatingTestContext(descriptor.testCase)

              // and re-run the spec listeners
              val specListeners = freshSpec.listeners() + Project.listeners()
              specListeners.forEach { it.specStarted(freshSpec.root().description(), freshSpec) }

              freshSpec.root().closure(context)

              // we can now fish out the new scope that pertains to this test, it must be a test case
              val freshTestCase = context.scopes.find { it.name() == descriptor.testCase.name() } as TestCase

              // we need to re-run the spec inteceptors for this fresh instance now
              val initialInterceptor = { next: () -> Unit -> freshSpec.interceptSpec(next) }
              val extensions = freshSpec.specExtensions() + Project.specInterceptors()
              val chain = createSpecInterceptorChain(freshSpec, extensions, initialInterceptor)
              chain {
                val freshDescriptor = TestCaseDescriptor(descriptor.id, freshTestCase)
                val runner = TestCaseRunner(request.engineExecutionListener, listeners)
                runner.runTest(freshDescriptor)
              }
            }
            false -> {
              val runner = TestCaseRunner(request.engineExecutionListener, listeners)
              runner.runTest(descriptor)
            }
          }
        }
        else -> throw IllegalStateException("$descriptor is not supported")
      }
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
    } catch (t: Throwable) {
      t.printStackTrace()
      request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.failed(t))
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

    println("Test discovery uris = " + uris.joinToString(":"))
    println("classes = " + classes.joinToString(":"))

    return TestDiscovery(TestDiscovery.DiscoveryRequest(uris, classes), uniqueId)
  }
}

