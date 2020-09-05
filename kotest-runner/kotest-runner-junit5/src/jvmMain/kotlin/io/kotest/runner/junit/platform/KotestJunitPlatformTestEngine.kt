package io.kotest.runner.junit.platform

import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.extensions.EnabledConditionSpecDiscoveryExtension
import io.kotest.engine.extensions.IgnoredSpecDiscoveryExtension
import io.kotest.engine.extensions.TagsExcludedDiscoveryExtension
import io.kotest.engine.listener.IsolationTestEngineListener
import io.kotest.engine.listener.SynchronizedTestEngineListener
import io.kotest.framework.discovery.Discovery
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import java.util.Optional
import kotlin.reflect.KClass

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   companion object {
      const val EngineId = "kotest"
   }

   init {
      ConfigManager.init()
   }

   override fun getId(): String = EngineId

   override fun getGroupId(): Optional<String> = Optional.of("io.kotest")

   override fun execute(request: ExecutionRequest) = runBlocking {
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
      KotestEngineLauncher()
         .withListener(listener)
         .withSpecs(root.classes)
         .withDumpConfig(true)
         .withFilters(root.testFilters)
         .launch()
   }

   /**
    * gradlew --tests rules:
    * Classname: adds classname selector and ClassMethodNameFilter post discovery filter
    * Classname.method: adds classname selector and ClassMethodNameFilter post discovery filter
    * org.Clsasname: doesn't seem to invoke the discover or execute methods.
    *
    * filter in gradle test block:
    * includeTestsMatching("*Test") - class selectors and ClassMethodNameFilter with pattern
    * includeTestsMatching("*Test") AND includeTestsMatching("org.gradle.internal.*") - class selectors and ClassMethodNameFilter with two patterns
    */
   override fun discover(
      request: EngineDiscoveryRequest,
      uniqueId: UniqueId,
   ): KotestEngineDescriptor {
      log("uniqueId=$uniqueId")
      log(request.string())

      // if we are excluded from the engines then we say goodnight according to junit rules
      val isKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!isKotest)
         return KotestEngineDescriptor(uniqueId, emptyList(), emptyList())

      val testFilters = request.postFilters().map {
         PostDiscoveryFilterAdapter(it, uniqueId)
      }

      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // therefore, the presence of a MethodSelector means we must run no tests in KT.
      return if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {
         val extensions = listOf(
            IgnoredSpecDiscoveryExtension,
            EnabledConditionSpecDiscoveryExtension,
            TagsExcludedDiscoveryExtension,
         ) + configuration.extensions().filterIsInstance<DiscoveryExtension>()
         val discovery = Discovery(extensions)
         val result = discovery.discover(request.toKotestDiscoveryRequest())
         val classes =
            result.specs.filter { spec -> testFilters.all { it.filter(spec.toDescription()) == TestFilterResult.Include } }
         KotestEngineDescriptor(uniqueId, classes, testFilters)
      } else {
         KotestEngineDescriptor(uniqueId, emptyList(), emptyList())
      }
   }
}

class KotestEngineDescriptor(
   id: UniqueId,
   val classes: List<KClass<out Spec>>,
   val testFilters: List<TestFilter>,
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
