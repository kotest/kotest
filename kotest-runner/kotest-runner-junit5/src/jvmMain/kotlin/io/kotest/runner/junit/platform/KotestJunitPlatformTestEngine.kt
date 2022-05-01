package io.kotest.runner.junit.platform

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.framework.discovery.Discovery
import io.kotest.mpp.Logger
import io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilter
import io.kotest.runner.junit.platform.gradle.GradlePostDiscoveryFilterExtractor
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import java.util.*
import kotlin.reflect.KClass

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   private val logger = Logger(KotestJunitPlatformTestEngine::class)

   companion object {
      const val EngineId = "kotest"
   }

   override fun getId(): String = EngineId

   override fun getGroupId(): Optional<String> = Optional.of("io.kotest")

   override fun execute(request: ExecutionRequest) {
      logger.log {
         Pair(
            null,
            "ExecutionRequest[${request::class.java.name}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]"
         )
      }
      val root = request.rootTestDescriptor as KotestEngineDescriptor
      when (root.error) {
         null -> execute(request, root)
         else -> abortExecution(request, root.error)
      }
   }

   private fun abortExecution(request: ExecutionRequest, e: Throwable) {
      request.engineExecutionListener.executionStarted(request.rootTestDescriptor)
      request.engineExecutionListener.executionFinished(request.rootTestDescriptor, TestExecutionResult.failed(e))
   }

   private fun execute(request: ExecutionRequest, root: KotestEngineDescriptor) {

      val configuration = ProjectConfiguration()

      val listener = ThreadSafeTestEngineListener(
         PinnedSpecTestEngineListener(
            JUnitTestEngineListener(
               SynchronizedEngineExecutionListener(
                  request.engineExecutionListener
               ),
               root,
            )
         )
      )

      request.configurationParameters.get("kotest.extensions").orElseGet { "" }
         .split(',')
         .map { it.trim() }
         .filter { it.isNotBlank() }
         .map { Class.forName(it).newInstance() as Extension }
         .forEach { configuration.registry.add(it) }

      TestEngineLauncher(listener)
         .withConfiguration(configuration)
         .withExtensions(root.testFilters)
         .withClasses(root.classes)
         .launch()
   }

   /**
    * gradlew --tests rules:
    * Classname: adds classname selector and ClassMethodNameFilter post discovery filter
    * Classname.method: adds classname selector and ClassMethodNameFilter post discovery filter
    * org.Classname: doesn't seem to invoke the discover or execute methods.
    *
    * filter in gradle test block:
    * includeTestsMatching("*Test") - class selectors and ClassMethodNameFilter with pattern
    * includeTestsMatching("*Test") AND includeTestsMatching("org.gradle.internal.*") - class selectors and ClassMethodNameFilter with two patterns
    */
   override fun discover(
      request: EngineDiscoveryRequest,
      uniqueId: UniqueId,
   ): KotestEngineDescriptor {
      logger.log { Pair(null, "JUnit discovery request [uniqueId=$uniqueId]") }
      logger.log { Pair(null, request.string()) }

      // if we are excluded from the engines then we say goodnight according to junit rules
      val isKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!isKotest)
         return KotestEngineDescriptor(uniqueId, emptyList(), emptyList(), emptyList(), null)

      val classMethodFilterRegexes = GradlePostDiscoveryFilterExtractor.extract(request.postFilters())
      val gradleClassMethodTestFilter = GradleClassMethodRegexTestFilter(classMethodFilterRegexes)

      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // Some other engines run tests via uniqueId selectors
      // therefore, the presence of a MethodSelector or a UniqueIdSelector means we must run no tests in KT.
      // if we get a uniqueid with kotest as engine we throw because that should never happen
      val allSelectors = request.getSelectorsByType(DiscoverySelector::class.java)
      val containsUnsupported = allSelectors.any {
         if (it is UniqueIdSelector)
            if (it.uniqueId.engineId.get() == EngineId)
               throw RuntimeException("Kotest does not allow running tests via uniqueId")
            else true
         else
            it is MethodSelector
      }
      val descriptor = if (!containsUnsupported) {
         val discovery = Discovery(emptyList())
         val result = discovery.discover(request.toKotestDiscoveryRequest())
         KotestEngineDescriptor(
            uniqueId,
            result.specs,
            result.scripts,
            listOf(gradleClassMethodTestFilter),
            result.error
         )
      } else {
         KotestEngineDescriptor(uniqueId, emptyList(), emptyList(), emptyList(), null)
      }

      logger.log { Pair(null, "JUnit discovery completed [descriptor=$descriptor]") }
      return descriptor
   }
}

class KotestEngineDescriptor(
   id: UniqueId,
   val classes: List<KClass<out Spec>>,
   val scripts: List<KClass<*>>,
   val testFilters: List<TestFilter>,
   val error: Throwable?, // an error during discovery
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
