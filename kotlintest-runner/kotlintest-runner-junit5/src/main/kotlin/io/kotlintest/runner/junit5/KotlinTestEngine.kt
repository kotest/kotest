package io.kotlintest.runner.junit5

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.description
import io.kotlintest.runner.jvm.IsolationTestEngineListener
import io.kotlintest.runner.jvm.SpecFilter
import io.kotlintest.runner.jvm.SynchronizedTestEngineListener
import io.kotlintest.runner.jvm.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.PostDiscoveryFilter
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass

class KotlinTestEngine : org.junit.platform.engine.TestEngine {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  companion object {
    const val EngineId = "kotlintest"
  }

  override fun getId(): String = EngineId

  override fun execute(request: ExecutionRequest) {
    logger.debug("JUnit execution request [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
    val root = request.rootTestDescriptor as KotlinTestEngineDescriptor
    val listener = SynchronizedTestEngineListener(IsolationTestEngineListener(JUnitTestRunnerListener(SynchronizedEngineExecutionListener(request.engineExecutionListener), root)))
    val runner = io.kotlintest.runner.jvm.TestEngine(root.classes, Project.parallelism(), listener)
    runner.execute()
  }

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): KotlinTestEngineDescriptor {
    logger.debug("configurationParameters=" + request.configurationParameters)
    logger.debug("uniqueId=$uniqueId")

    val postFilters = when (request) {
      is LauncherDiscoveryRequest -> {
        logger.debug(request.string())
        request.postDiscoveryFilters.toList()
      }
      else -> {
        logger.debug(request.string())
        emptyList()
      }
    }

    // a method selector is passed by intellij to run just a single method inside a test file
    // this happens for example, when trying to run a junit test alongside kotlintest tests,
    // and kotlintest will then run all other tests.
    // therefore, the presence of a MethodSelector means we must run no tests in KT.
    if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {

      val result = TestDiscovery.discover(discoveryRequest(request))

      // gradle passes through --tests some.Class using a PostDiscoveryFilter, specifically an
      // internal gradle class called ClassMethodNameFilter. That class makes all kinds of
      // assumptions around what is a test and what isn't, via the source so we must fool it.
      // this is liable to be buggy as well, and should be stripped out as soon as gradle
      // fix their bugs around junit 5 support
      class ClassMethodAdaptingFilter(val filter: PostDiscoveryFilter) : SpecFilter {
        override fun invoke(klass: KClass<out Spec>): Boolean {
          val id = uniqueId.appendSpec(klass.java.description())
          val descriptor = object : AbstractTestDescriptor(id, klass.java.description().name) {
            override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
            override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(klass.java))
          }
          val parent = KotlinTestEngineDescriptor(uniqueId, emptyList())
          parent.addChild(descriptor)
          return filter.apply(descriptor).included()
        }
      }

      val testFilters = postFilters.map { ClassMethodAdaptingFilter(it) }
      val classes = result.classes.filter { klass -> testFilters.isEmpty() || testFilters.any { it.invoke(klass) } }
      return KotlinTestEngineDescriptor(uniqueId, classes)

    } else {
      return KotlinTestEngineDescriptor(uniqueId, emptyList())
    }
  }

  class KotlinTestEngineDescriptor(id: UniqueId, val classes: List<KClass<out Spec>>) : EngineDescriptor(id, "KotlinTest") {
    override fun mayRegisterTests(): Boolean = true
  }
}