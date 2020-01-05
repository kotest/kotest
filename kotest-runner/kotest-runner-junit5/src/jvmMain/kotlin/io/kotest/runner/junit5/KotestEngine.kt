package io.kotest.runner.junit5

import io.kotest.core.Project
import io.kotest.core.specs.SpecContainer
import io.kotest.runner.jvm.IsolationTestEngineListener
import io.kotest.runner.jvm.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.slf4j.LoggerFactory

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestEngine : TestEngine {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   companion object {
      const val EngineId = "kotest"
      const val RootTestName = "Kotest"
   }

   override fun getId(): String = EngineId

   override fun execute(request: ExecutionRequest) {
      logger.debug("JUnit execution request [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
      val root = request.rootTestDescriptor as KotestEngineDescriptor
      val listener = IsolationTestEngineListener(
         JUnitTestEngineListener(
            SynchronizedEngineExecutionListener(request.engineExecutionListener),
            root
         )
      )
      val runner = io.kotest.runner.jvm.TestEngine(
         root.classes,
         emptyList(),
         Project.parallelism(),
         emptySet(),
         emptySet(),
         listener
      )
      runner.execute()
   }

   override fun discover(
      request: EngineDiscoveryRequest,
      uniqueId: UniqueId
   ): KotestEngineDescriptor {
      println("Test discovery")
      logger.trace("configurationParameters=" + request.configurationParameters)
      logger.trace("uniqueId=$uniqueId")
      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // therefore, the presence of a MethodSelector means we must run no tests in KT.
      return when {
         request.getSelectorsByType(MethodSelector::class.java).isEmpty() -> scan(request, uniqueId)
         else -> KotestEngineDescriptor(uniqueId, emptyList())
      }
   }

   /**
    * Performs test discovery by scanning the classpath.
    */
   private fun scan(
      request: EngineDiscoveryRequest,
      uniqueId: UniqueId
   ): KotestEngineDescriptor {

      val result = TestDiscovery.discover(request.toDiscoveryRequest())

      val postFilters = when (request) {
         is LauncherDiscoveryRequest -> {
            logger.trace(request.string())
            request.postDiscoveryFilters
         }
         else -> {
            logger.trace(request.string())
            emptyList()
         }
      }

      val classes = if (postFilters.isEmpty()) result.containers else {
         val testFilters = postFilters.map { ClassMethodAdaptingFilter(uniqueId, it) }
         result.containers.filter { container -> testFilters.any { it.invoke(container) } }
      }

      return KotestEngineDescriptor(uniqueId, classes)
   }

   class KotestEngineDescriptor(id: UniqueId, val classes: List<SpecContainer>) : EngineDescriptor(id, RootTestName) {
      override fun mayRegisterTests(): Boolean = true
   }
}
