package io.kotest.runner.junit.platform

import io.kotest.core.Logger
import io.kotest.core.extensions.Extension
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.ProjectConfigLoader
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.runner.junit.platform.discovery.Discovery
import io.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilter
import io.kotest.runner.junit.platform.gradle.GradlePostDiscoveryFilterExtractor
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import java.util.Optional

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   private val logger = Logger(KotestJunitPlatformTestEngine::class)

   companion object {
      const val ENGINE_ID = "kotest"
      const val ENGINE_NAME = "Kotest"
      const val GROUP_ID = "io.kotest"
   }

   override fun getId(): String = ENGINE_ID
   override fun getGroupId(): Optional<String> = Optional.of(GROUP_ID)

   override fun execute(request: ExecutionRequest) {
      logger.log {
         "ExecutionRequest[${request::class.java.name}@${request.hashCode()}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]"
      }
      logger.log { "Root request children ${request.rootTestDescriptor.children.joinToString(" ,") { it.uniqueId.toString() }}" }
      val root = request.rootTestDescriptor as KotestEngineDescriptor
      execute(request, root)
   }

   private fun execute(request: ExecutionRequest, root: KotestEngineDescriptor) {

      logger.log { "Executing request with listener ${request::class.java.name}:${request.engineExecutionListener}" }

      // we need to load this here as well so we can configure the formatter
      // todo update display name formatter to be a builder that accepts config, so we can push the config part to runtime and remove the dependency here entirely, then project config loader can go internal
      val config = ProjectConfigLoader.load()

      val listener = ThreadSafeTestEngineListener(
         PinnedSpecTestEngineListener(
            JUnitTestEngineListener(
               listener = SynchronizedEngineExecutionListener(
                  request.engineExecutionListener
               ),
               root = root,
               formatter = FallbackDisplayNameFormatter.default(config)
            )
         )
      )

      TestEngineLauncher(listener)
         .addExtensions(root.extensions)
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

      if (!isEngineIncluded(request) || !shouldRunTests(request))
         return createEmptyEngineDescriptor(uniqueId)

      val result = Discovery.discover(uniqueId, request)

      val descriptor = createEngineDescriptor(
         uniqueId,
         result.specs,
         configurationParameterExtensions(request) + listOfNotNull(gradleTestFilterExtension(request)),
      )

      logger.log { "JUnit discovery completed [descriptor=$descriptor]" }
      logger.log { "Final specs [${descriptor.classes.joinToString(", ")}]" }
      return descriptor
   }

   /**
    * Creates [Extension]s from configuration parameters
    */
   private fun configurationParameterExtensions(request: EngineDiscoveryRequest): List<Extension> {
      return request.configurationParameters.get("kotest.extensions").orElseGet { "" }
         .split(',')
         .map { it.trim() }
         .filter { it.isNotBlank() }
         .map { Class.forName(it).getDeclaredConstructor().newInstance() as Extension }
   }

   /**
    * Returns a [DescriptorFilter] created from the --tests parameter in gradle, which it exposes
    * as an instance of [org.junit.platform.launcher.PostDiscoveryFilter].
    */
   private fun gradleTestFilterExtension(request: EngineDiscoveryRequest): DescriptorFilter {
      val classMethodFilterRegexes = GradlePostDiscoveryFilterExtractor.extract(request.postFilters())
      return GradleClassMethodRegexTestFilter(classMethodFilterRegexes)
   }

   /**
    * Returns true if there are selectors compatible with Kotest.
    * Kotest supports [ClassSelector]s and [UniqueIdSelector]s.
    *
    * A [MethodSelector] is passed by intellij to run just a single method inside a test file.
    * Kotest will never use method selectors, so if we have one, then we know it is something
    * other than kotest that is trying to run the tests, and we should skip running the engine.
    */
   private fun shouldRunTests(request: EngineDiscoveryRequest): Boolean {
      if (request.getSelectorsByType(MethodSelector::class.java).isNotEmpty()) return false
      return request.getSelectorsByType(ClassSelector::class.java).isNotEmpty() ||
         request.getSelectorsByType(UniqueIdSelector::class.java).isNotEmpty()
   }

   /**
    * If we are excluded from the engines then we do not run discovery
    */
   private fun isEngineIncluded(request: EngineDiscoveryRequest): Boolean {
      return request.engineFilters().all { it.toPredicate().test(this) }
   }
}

