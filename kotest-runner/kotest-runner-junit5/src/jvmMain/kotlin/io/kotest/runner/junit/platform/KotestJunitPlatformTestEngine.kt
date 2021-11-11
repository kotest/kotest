package io.kotest.runner.junit.platform

import io.kotest.common.KotestInternal
import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.framework.discovery.Discovery
import io.kotest.mpp.log
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.launcher.LauncherDiscoveryRequest
import java.util.Optional
import kotlin.reflect.KClass
//import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * A Kotest implementation of a Junit Platform [TestEngine].
 */
@KotestInternal
class KotestJunitPlatformTestEngine : TestEngine {

   companion object {
      const val EngineId = "kotest"
   }

   override fun getId(): String = EngineId

   override fun getGroupId(): Optional<String> = Optional.of("io.kotest")

   override fun execute(request: ExecutionRequest) {
      log { "JUnit ExecutionRequest[${request::class.java.name}] [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]" }
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

      val configuration = Configuration()

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
         .forEach { configuration.registry().add(it) }

      TestEngineLauncher(listener)
         .withClasses(root.classes)
         .withExtensions(root.testFilters)
         .withConfiguration(configuration)
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
      log { "JUnit discovery request [uniqueId=$uniqueId]" }
      log { request.string() }

      // if we are excluded from the engines then we say goodnight according to junit rules
      val isKotest = request.engineFilters().all { it.toPredicate().test(this) }
      if (!isKotest)
         return KotestEngineDescriptor(uniqueId, emptyList(), emptyList(), emptyList(), null)

      val testFilters = request.postFilters().map {
         PostDiscoveryFilterAdapter(it, uniqueId)
      }

      // a method selector is passed by intellij to run just a single method inside a test file
      // this happens for example, when trying to run a junit test alongside kotest tests,
      // and kotest will then run all other tests.
      // therefore, the presence of a MethodSelector means we must run no tests in KT.
      val descriptor = if (request.getSelectorsByType(MethodSelector::class.java).isEmpty()) {

         val discovery = Discovery(emptyList())
         val result = discovery.discover(request.toKotestDiscoveryRequest())
         val classes = result.specs.filter { spec ->
            testFilters.all { it.filter(spec.toDescriptor()) == TestFilterResult.Include }
         }
         KotestEngineDescriptor(uniqueId, classes, result.scripts, testFilters, result.error)
      } else {
         KotestEngineDescriptor(uniqueId, emptyList(), emptyList(), emptyList(), null)
      }

      log { "JUnit discovery completed [descriptor=$descriptor]" }
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
