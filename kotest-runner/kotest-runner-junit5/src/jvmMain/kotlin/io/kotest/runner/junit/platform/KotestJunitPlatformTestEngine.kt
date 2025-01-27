package io.kotest.runner.junit.platform

import io.kotest.core.Logger
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.ProjectConfigLoader
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
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
import kotlin.reflect.KClass

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

      val config = ProjectConfigLoader.detect()

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
         .addExtensions(root.testFilters)
         .addExtensions(root.extensions)
         .withClasses(root.classes)
         .withProjectConfig(config)
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

      // if we are excluded from the engines then we do not run discovery
      val includeKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!includeKotest)
         return createEmptyEngineDescriptor(uniqueId)

      val descriptor = if (shouldRunTests(request)) {

         val result: List<KClass<out Spec>> = classesFromSelectors(request)

         val extensions = request.configurationParameters.get("kotest.extensions").orElseGet { "" }
            .split(',')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { Class.forName(it).getDeclaredConstructor().newInstance() as Extension }

         val classMethodFilterRegexes = GradlePostDiscoveryFilterExtractor.extract(request.postFilters())
         val gradleClassMethodTestFilter = GradleClassMethodRegexTestFilter(classMethodFilterRegexes)

         createEngineDescriptor(
            uniqueId,
            result,
            gradleClassMethodTestFilter,
            extensions,
         )
      } else {
         createEmptyEngineDescriptor(uniqueId)
      }

      logger.log { "JUnit discovery completed [descriptor=$descriptor]" }
      logger.log { "Final specs [${descriptor.classes.joinToString(", ")}]" }
      return descriptor
   }

   private fun classesFromSelectors(request: EngineDiscoveryRequest): List<KClass<out Spec>> {
      // first filter down to spec instances only, then load the full class
      val selectors = request.getSelectorsByType(ClassSelector::class.java)
      return selectors
         .asSequence()
         .map { Class.forName(it.className, false, this::class.java.classLoader) }
         .filter(isSpecSubclass)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out Spec>>()
         .filterNot(isAbstract)
         .toList()
   }

   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }

   /**
    * A [MethodSelector] is passed by intellij to run just a single method inside a test file.
    * This happens when trying to run Junit tests from intellij because they are method based.
    * Kotest does not use method selectors, so if we have one, then we know its the junit plugin
    * and not kotest, so we should skip running the engine.
    */
   private fun shouldRunTests(request: EngineDiscoveryRequest): Boolean {

      if (request.getSelectorsByType(MethodSelector::class.java).isEmpty() &&
         request.getSelectorsByType(UniqueIdSelector::class.java).isEmpty()
      ) {
         logger.log { "No MethodSelector or UniqueIdSelector specified" }
         return true
      }

      return false
   }
}

