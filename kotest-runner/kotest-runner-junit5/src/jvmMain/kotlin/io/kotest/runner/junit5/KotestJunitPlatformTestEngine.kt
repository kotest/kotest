package io.kotest.runner.junit5

import io.kotest.assertions.log
import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import io.kotest.core.engine.IsolationTestEngineListener
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import kotlin.reflect.KClass

/**
 * A Kotest implementation of the Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   companion object {
      const val EngineId = "kotest"
   }

   override fun getId(): String = EngineId

   override fun execute(request: ExecutionRequest) {
      log("JUnit ExecutionRequest[${request::class.java.name}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
      val root = request.rootTestDescriptor as KotestEngineDescriptor
      val listener = IsolationTestEngineListener(
          JUnitTestEngineListener(
              SynchronizedEngineExecutionListener(request.engineExecutionListener),
              root
          )
      )
      val runner = KotestEngine(
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
      log("uniqueId=$uniqueId")
      log(request.string())

      val postFilters = request.postFilters()

      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // therefore, the presence of a MethodSelector means we must run no tests in KT.
      return if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {
         val result = TestDiscovery.discover(discoveryRequest(request))
         val testFilters = postFilters.map { ClassMethodAdaptingFilter(it, uniqueId) }
         val classes = result.specs.filter { klass -> testFilters.isEmpty() || testFilters.any { it.invoke(klass) } }
         KotestEngineDescriptor(uniqueId, classes)
      } else {
         KotestEngineDescriptor(uniqueId, emptyList())
      }
   }
}

class KotestEngineDescriptor(
   id: UniqueId,
   val classes: List<KClass<out Spec>>
) : EngineDescriptor(id, "Kotest") {
   override fun mayRegisterTests(): Boolean = true
}

fun EngineDiscoveryRequest.postFilters() = when (this) {
   is LauncherDiscoveryRequest -> postDiscoveryFilters.toList()
   else -> emptyList()
}
