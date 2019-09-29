package io.kotest.runner.junit5

import io.kotest.Project
import io.kotest.Spec
import io.kotest.description
import io.kotest.runner.jvm.IsolationTestEngineListener
import io.kotest.runner.jvm.SpecFilter
import io.kotest.runner.jvm.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.PostDiscoveryFilter
import org.slf4j.LoggerFactory
import java.util.Optional
import kotlin.reflect.KClass

/**
 * An implementation of Kotest that runs as a JUnit Platform [TestEngine].
 */
class KotestEngine : TestEngine {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  companion object {
    const val EngineId = "kotest"
  }

  override fun getId(): String = EngineId

  override fun execute(request: ExecutionRequest) {
    logger.trace("JUnit execution request [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
    val root = request.rootTestDescriptor as KotestEngineDescriptor
    val listener = IsolationTestEngineListener(JUnitTestRunnerListener(SynchronizedEngineExecutionListener(request.engineExecutionListener), root))
    val runner = io.kotest.runner.jvm.TestEngine(root.classes,
      emptyList(),
      Project.parallelism(),
      emptySet(),
      emptySet(),
      listener)
    runner.execute()
  }

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): KotestEngineDescriptor {
    logger.trace("configurationParameters=" + request.configurationParameters)
    logger.trace("uniqueId=$uniqueId")

    val postFilters = when (request) {
      is LauncherDiscoveryRequest -> {
        logger.trace(request.string())
        request.postDiscoveryFilters.toList()
      }
      else -> {
        logger.trace(request.string())
        emptyList()
      }
    }

    // a method selector is passed by intellij to run just a single method inside a test file
    // this happens for example, when trying to run a junit test alongside kotest tests,
    // and kotest will then run all other tests.
    // therefore, the presence of a MethodSelector means we must run no tests in KT.
    if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {

      val result = TestDiscovery.discover(discoveryRequest(request))

      // gradles uses a post discovery filter called [ClassMethodNameFilter] when a user runs gradle
      // with either `-- tests someClass` or by adding a test filter section to their gradle build.
      // This filter class makes all kinds of assumptions around what is a test and what isn't,
      // so we must fool it by creating a dummy test descriptor.
      // This is liable to be buggy, and should be stripped out as soon as gradle
      // fix their bugs around junit 5 support, if ever.
      class ClassMethodAdaptingFilter(val filter: PostDiscoveryFilter) : SpecFilter {
        override fun invoke(klass: KClass<out Spec>): Boolean {
          val id = uniqueId.appendSpec(klass.java.description())
          val descriptor = object : AbstractTestDescriptor(id, klass.java.description().name) {
            override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
            override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(klass.java))
          }
          val parent = KotestEngineDescriptor(uniqueId, emptyList())
          parent.addChild(descriptor)
          return filter.apply(descriptor).included()
        }
      }

      val testFilters = postFilters.map { ClassMethodAdaptingFilter(it) }
      val classes = result.classes.filter { klass -> testFilters.isEmpty() || testFilters.any { it.invoke(klass) } }
      return KotestEngineDescriptor(uniqueId, classes)

    } else {
      return KotestEngineDescriptor(uniqueId, emptyList())
    }
  }

  class KotestEngineDescriptor(id: UniqueId, val classes: List<KClass<out Spec>>) : EngineDescriptor(id, "Kotest") {
    override fun mayRegisterTests(): Boolean = true
  }
}
