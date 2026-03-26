package io.kotest.runner.junit.platform

import io.kotest.common.reflection.instantiations
import io.kotest.core.Logger
import io.kotest.core.extensions.Extension
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.KotestPropertiesLoader
import io.kotest.engine.config.ProjectConfigLoader
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.runner.junit.platform.discovery.Discovery
import io.kotest.runner.junit.platform.gradle.ClassMethodNameFilterAdapter
import kotlinx.coroutines.runBlocking
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import java.util.Optional
import kotlin.reflect.KClass

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
class KotestJunitPlatformTestEngine : TestEngine {

   private val logger = Logger<KotestJunitPlatformTestEngine>()

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

      // this is a hack - junit needs access to project config to load the formatter, but at this stage, the config is not quite ready
      // specifically; the /kotest.properties file hasn't been loaded as the engine has not yet been initialized,
      // so we'll force the loading here as well. Ideally, this would all be taken care of in the engine and junit shouldn't need to know
      // anything about the internals. We will need to think of a better way to handle this in the future to clean this up, perhaps by changing
      // this initialize code out of engine interceptors and into the engine constructor itself
      KotestPropertiesLoader.loadAndApplySystemPropsFile()

      // we need to load this here as well so we can configure the formatter
      // todo update display name formatter to be a builder that accepts config, so we can push the config part to runtime and remove the dependency here entirely, then project config loader can go internal
      val config = ProjectConfigLoader.load(root.specs.map { it.fqn }.toSet())

      val listener = ThreadSafeTestEngineListener(
         PinnedSpecTestEngineListener(
            JUnitTestEngineListener(
               listener = SynchronizedEngineExecutionListener(
                  request.engineExecutionListener
               ),
               root = root,
               formatter = DisplayNameFormatting(config)
            )
         )
      )

      runBlocking {
         // the result is ignored as the junit runner will add engine errors as "dummy specs"
         TestEngineLauncher()
            .withListener(listener)
            .addExtensions(root.extensions)
            .withSpecRefs(root.specs)
            .execute()
      }
   }

   /**
    * This method is invoked by build systems that support JUnit Platform, to return a [TestDescriptor] that
    * contains any matching tests given the [EngineDiscoveryRequest].
    *
    * The [EngineDiscoveryRequest] contains a list of [DiscoverySelector]s which provide information
    * on the tests that have been discovered by the build system. The tests may or may not be relevant
    * to Kotest, so our job is to filter them down to only those that are relevant.
    *
    * For example, when running a single test using --tests, Gradle will add a [ClassSelector] and a
    * [ClassMethodNameFilter] post-discovery filter.
    *
    * When executing the test task without any particular --tests filter, Gradle will include multiple
    * [ClassSelector]s, one for each test class, with no post-discovery filters.
    *
    * Finally, another example is re-running a failed test, Gradle will provide a [UniqueIdSelector]
    * that contains the unique id of the test that failed.
    *
    * gradlew --tests rules:
    * Classname: adds classname selector and ClassMethodNameFilter post-discovery filter
    * Classname.method: adds classname selector and ClassMethodNameFilter post-discovery filter
    * org.Classname: doesn't seem to invoke the discover or execute methods.
    *
    * filter in Gradle test block:
    * includeTestsMatching("*Test") - class selectors and ClassMethodNameFilter with pattern
    * includeTestsMatching("*Test") AND includeTestsMatching("org.gradle.internal.*") - class selectors and ClassMethodNameFilter with two patterns
    */
   override fun discover(
      request: EngineDiscoveryRequest,
      uniqueId: UniqueId,
   ): KotestEngineDescriptor {

      logger.log { "JUnit discovery request [uniqueId=$uniqueId]" }
      logger.log { "JUnit discovery request [configurationParameters=${request.configurationParameters}]" }
      logger.log { "JUnit discovery request [engineFilters=${request.engineFilters()}]" }
      logger.log { "JUnit discovery request [postFilters=${request.postFilters()}]" }
      logger.log { "JUnit discovery request [classSelectors=${request.getSelectorsByType(ClassSelector::class.java)}]" }
      logger.log { "JUnit discovery request [methodSelectors=${request.getSelectorsByType(MethodSelector::class.java)}]" }
      logger.log { "JUnit discovery request [uniqueIdSelectors=${request.getSelectorsByType(UniqueIdSelector::class.java)}]" }

      if (!isEngineIncluded(request) || !shouldRunTests(request))
         return EngineDescriptorBuilder.builder(uniqueId).build()

      val result = Discovery.discover(uniqueId, request)

      // this is a hack - junit needs access to project config to load the formatter, but at this stage, the config is not quite ready
      // specifically; the /kotest.properties file hasn't been loaded as the engine has not yet been initialized,
      // so we'll force the loading here as well. Ideally, this would all be taken care of in the engine and junit shouldn't need to know
      // anything about the internals. We will need to think of a better way to handle this in the future to clean this up, perhaps by changing
      // this initialize code out of engine interceptors and into the engine constructor itself
      KotestPropertiesLoader.loadAndApplySystemPropsFile()

      // we need to load this here as well so we can configure the formatter
      // todo update display name formatter to be a builder that accepts config, so we can push the config part to runtime and remove the dependency here entirely, then project config loader can go internal
      val config = ProjectConfigLoader.load(result.specs.map { it.fqn }.toSet())

      val formatting = DisplayNameFormatting(config)

      val engine = EngineDescriptorBuilder.builder(uniqueId)
         .withSpecs(result.specs)
         .withExtensions(configurationParameterExtensions(request) + ClassMethodNameFilterAdapter.adapt(request))
         .withFormatter(formatting)
         .build()

      logger.log { "Final discovery [${engine.specs.joinToString(", ")}]" }
      return engine
   }

   /**
    * Creates [Extension]s from configuration parameters
    */
   @Suppress("UNCHECKED_CAST")
   private fun configurationParameterExtensions(request: EngineDiscoveryRequest): List<Extension> {
      return request.configurationParameters.get("kotest.extensions").orElse("")
         .split(',')
         .map { it.trim() }
         .filter { it.isNotBlank() }
         .map { instantiations.newInstanceNoArgConstructorOrObjectInstance(Class.forName(it).kotlin as KClass<Extension>) }
   }

   /**
    * Returns true if there are selectors compatible with Kotest.
    * Kotest supports [ClasspathRootSelector]s, [ClassSelector]s, and [UniqueIdSelector]s.
    *
    * Note: [MethodSelector]s may be present alongside class/classpath selectors (e.g., from AGP 9+
    * which pre-discovers `@Test` methods and passes them as method selectors). We intentionally
    * ignore method selectors here — [Discovery] already ignores them — and only check for the
    * selector types Kotest actually processes.
    */
   private fun shouldRunTests(request: EngineDiscoveryRequest): Boolean {
      return request.getSelectorsByType(ClassSelector::class.java).isNotEmpty() ||
         request.getSelectorsByType(UniqueIdSelector::class.java).isNotEmpty() ||
         request.getSelectorsByType(ClasspathRootSelector::class.java).isNotEmpty()
   }

   /**
    * If any engine filter excludes Kotest, then we do not run discovery.
    * In other words, all filters must pass for a given engine to be included.
    * Usually, these filters are not used, but they can be used to exclude engines.
    */
   private fun isEngineIncluded(request: EngineDiscoveryRequest): Boolean {
      return request.engineFilters().all { it.toPredicate().test(this) }
   }
}
