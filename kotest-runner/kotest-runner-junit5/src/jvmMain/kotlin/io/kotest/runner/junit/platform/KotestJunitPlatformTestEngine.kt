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
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import java.util.Optional
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
         "ExecutionRequest[${request::class.java.name}@${request.hashCode()}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]"
      }
      logger.log { "Root request children ${request.rootTestDescriptor.children.joinToString(" ,") { it.uniqueId.toString() }}" }
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

      logger.log { "Executing request with listener ${request::class.java.name}:${request.engineExecutionListener}" }

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
      logger.log { "JUnit discovery request [uniqueId=$uniqueId]" }
      logger.log { request.string() }

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

      logger.log { "JUnit discovery completed [descriptor=$descriptor]" }
      logger.log { "Final specs [${descriptor.classes.joinToString(", ")}]" }
      return descriptor
   }

   // a method selector is passed by intellij to run just a single method inside a test file
   // this happens for example, when trying to run a junit test alongside kotest tests,
   // and kotest will then run all other tests.
   // therefore, no detected selectors and the presence of a MethodSelector or UniqueIdSelector means we must run no tests in KT.
   private fun shouldRunTests(discoveryRequest: DiscoveryRequest, request: EngineDiscoveryRequest): Boolean {

      if (discoveryRequest.selectors.isNotEmpty()) {
         logger.log { "DiscoverySelectors are non-empty" }
         return true
      }

      if (request.getSelectorsByType(MethodSelector::class.java).isEmpty() &&
         request.getSelectorsByType(UniqueIdSelector::class.java).isEmpty()
      ) {
         logger.log { "No MethodSelector or UniqueIdSelector specified" }
         return true
      }

      return false
   }
}

class KotestEngineDescriptor(
   id: UniqueId,
   internal val configuration: ProjectConfiguration,
   val classes: List<KClass<out Spec>>,
   val scripts: List<KClass<*>>,
   val testFilters: List<TestFilter>,
   val error: Throwable?, // an error during discovery
) : EngineDescriptor(id, "Kotest") {

   private val logger = Logger(KotestEngineDescriptor::class)

   internal val formatter: FallbackDisplayNameFormatter by lazy {
      getFallbackDisplayNameFormatter(configuration.registry, configuration)
   }

   init {
      logger.log { "Adding ${classes.size} children to the root descriptor ${KotestEngineDescriptor::class}@${this.hashCode()}" }
      classes.forEach {
         addChild(getSpecDescriptor(this, it.toDescriptor(), formatter.format(it)))
      }
   }

   // Only reports dynamic children (see ExtensionExceptionExtractor) if there are any test classes to run
   override fun mayRegisterTests(): Boolean = classes.isNotEmpty()
}
