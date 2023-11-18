package io.kotest.runner.junit.platform

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.detectAbstractProjectConfigsJVM
import io.kotest.engine.config.loadProjectConfigFromClassnameJVM
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.mpp.Logger
import io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilter
import io.kotest.runner.junit.platform.gradle.GradlePostDiscoveryFilterExtractor
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.EngineFilter
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.PostDiscoveryFilter
import java.util.*
import kotlin.reflect.KClass

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   private val logger = Logger(KotestJunitPlatformTestEngine::class)

   companion object {
      internal const val EngineId = "kotest"
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

      val listener = ThreadSafeTestEngineListener(
         PinnedSpecTestEngineListener(
            JUnitTestEngineListener(
               SynchronizedEngineExecutionListener(
                  request.engineExecutionListener
               ),
               root,
               root.formatter
            )
         )
      )

      TestEngineLauncher(listener)
         .withInitializedConfiguration(root.configuration)
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

      val configuration = ConfigManager.initialize(ProjectConfiguration()) {
         detectAbstractProjectConfigsJVM() +
            listOfNotNull(loadProjectConfigFromClassnameJVM())
      }

      // if we are excluded from the engines then we say goodnight according to junit rules
      val isKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!isKotest)
         return KotestEngineDescriptor(uniqueId, configuration, emptyList(), emptyList(), emptyList(), null)

      val discoveryRequest = request.toKotestDiscoveryRequest(uniqueId)

      val descriptor = if (shouldRunTests(discoveryRequest, request)) {
         val discovery = Discovery(emptyList(), configuration)
         val result = discovery.discover(discoveryRequest)

         if (result.specs.isNotEmpty()) {
            request.configurationParameters.get("kotest.extensions").orElseGet { "" }
               .split(',')
               .map { it.trim() }
               .filter { it.isNotBlank() }
               .map { Class.forName(it).getDeclaredConstructor().newInstance() as Extension }
               .forEach { configuration.registry.add(it) }
         }

         val classMethodFilterRegexes = GradlePostDiscoveryFilterExtractor.extract(request.postFilters())
         val gradleClassMethodTestFilter = GradleClassMethodRegexTestFilter(classMethodFilterRegexes)

         KotestEngineDescriptor(
            uniqueId,
            configuration,
            result.specs,
            result.scripts,
            listOf(gradleClassMethodTestFilter),
            result.error
         )
      } else {
         KotestEngineDescriptor(uniqueId, configuration, emptyList(), emptyList(), emptyList(), null)
      }

      logger.log { Pair(null, "JUnit discovery completed [descriptor=$descriptor]") }
      logger.log { Pair(null, "Final specs [${descriptor.classes.joinToString(", ")}]") }
      return descriptor
   }

   // a method selector is passed by intellij to run just a single method inside a test file
   // this happens for example, when trying to run a junit test alongside kotest tests,
   // and kotest will then run all other tests.
   // therefore, no detected selectors and the presence of a MethodSelector or UniqueIdSelector means we must run no tests in KT.
   private fun shouldRunTests(discoveryRequest: DiscoveryRequest, request: EngineDiscoveryRequest): Boolean {

      if (discoveryRequest.selectors.isNotEmpty()) {
         logger.log { "selectors are non-empty" }
         return true
      }

      if (request.getSelectorsByType(MethodSelector::class.java).isEmpty() && request.getSelectorsByType(UniqueIdSelector::class.java).isEmpty()) {
         logger.log { "No method selector and no unique id specified" }
         return true
      }

      return false
   }
}

class KotestEngineDescriptor(
   id: UniqueId,
   internal val configuration: ProjectConfiguration,
   classes: List<KClass<out Spec>>,
   val scripts: List<KClass<*>>,
   val testFilters: List<TestFilter>,
   val error: Throwable?, // an error during discovery
) : EngineDescriptor(id, "Kotest") {

   private val logger = Logger(KotestEngineDescriptor::class)

   internal val formatter: FallbackDisplayNameFormatter by lazy {
      getFallbackDisplayNameFormatter(configuration.registry, configuration)
   }

   internal val classes: List<KClass<out Spec>>
      get() = children.map {
         @Suppress("UNCHECKED_CAST") // we only add Spec classes as children
         (it.source.get() as ClassSource).javaClass.kotlin as KClass<out Spec>
      }

   init {
      logger.log { "Adding ${children.size} as children of the descriptor" }
      classes.forEach {
         addChild(getSpecDescriptor(this, it.toDescriptor(), formatter.format(it)))
      }
   }

   // Only reports dynamic children (see ExtensionExceptionExtractor) if there are any test classes to run
   override fun mayRegisterTests(): Boolean = children.isNotEmpty()
}

fun EngineDiscoveryRequest.engineFilters(): List<EngineFilter> = when (this) {
   is LauncherDiscoveryRequest -> engineFilters.toList()
   else -> emptyList()
}

fun EngineDiscoveryRequest.postFilters(): List<PostDiscoveryFilter> = when (this) {
   is LauncherDiscoveryRequest -> postDiscoveryFilters.toList()
   else -> emptyList()
}
