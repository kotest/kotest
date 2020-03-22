package io.kotest.runner.junit.platform

import io.kotest.mpp.log
import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import io.kotest.core.engine.IsolationTestEngineListener
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.SynchronizedTestEngineListener
import io.kotest.core.engine.discovery.Discovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import java.util.*
import kotlin.reflect.KClass

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   companion object {
      const val EngineId = "kotest"
   }

   override fun getId(): String =
      EngineId

   override fun getGroupId(): Optional<String> = Optional.of("io.kotest")

   override fun execute(request: ExecutionRequest) {
      log("JUnit ExecutionRequest[${request::class.java.name}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
      val root = request.rootTestDescriptor as KotestEngineDescriptor
      val listener = SynchronizedTestEngineListener(
         IsolationTestEngineListener(
            JUnitTestEngineListener(
               SynchronizedEngineExecutionListener(request.engineExecutionListener),
               root
            )
         )
      )
      val runner = KotestEngine(
         root.classes,
         emptyList(),
         Project.parallelism(),
         null,
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

      // if we are excluded from the engines then we say goodnight
      val isKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!isKotest)
         return KotestEngineDescriptor(uniqueId, emptyList())

      val postFilters = request.postFilters()
      val specFilters = postFilters.map {
         ClassMethodAdaptingFilter(
            it,
            uniqueId
         )
      }

      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // therefore, the presence of a MethodSelector means we must run no tests in KT.
      return if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {
         val result = Discovery.discover(createDiscoveryRequest(request))
         val classes = result.specs.filter { klass -> specFilters.isEmpty() || specFilters.any { it.invoke(klass) } }
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

fun EngineDiscoveryRequest.engineFilters() = when (this) {
   is LauncherDiscoveryRequest -> engineFilters.toList()
   else -> emptyList()
}

fun EngineDiscoveryRequest.postFilters() = when (this) {
   is LauncherDiscoveryRequest -> postDiscoveryFilters.toList()
   else -> emptyList()
}
