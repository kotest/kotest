package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

data class EngineResult(val errors: List<Throwable>) {
   companion object {
      val empty = EngineResult(emptyList())
   }
}

/**
 * Contains the discovered specs that will be executed.
 *
 * All specs are wrapped in a [SpecRef].
 *
 * On platforms that lack reflective capability, such as nodeJS or native, the specs are
 * either preconstructed or constructed through a simple function. On the JVM, the [KClass]
 * instance is used to reflectively instantiate.
 */
data class TestSuite(val specs: List<SpecRef>) {
   companion object {
      operator fun invoke(classes: List<KClass<out Spec>>) = TestSuite(classes.map { ReflectiveSpecRef(it) })
      val empty = TestSuite(emptyList())
   }
}

data class TestEngineConfig(
   val listener: TestEngineListener,
   val interceptors: List<EngineInterceptor>,
   val configuration: Configuration,
   val testFilters: List<TestFilter>,
   val specFilters: List<SpecFilter>,
   val explicitTags: Tags?,
)

/**
 * Multiplatform Kotest Test Engine.
 */
class TestEngine(val config: TestEngineConfig) {

   init {
      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.explicitTags?.let { configuration.registerExtensions(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.testFilters)
   }

   private fun logInterceptors() {
      log { "TestEngine: ${config.interceptors.size} engine interceptors:" }
      config.interceptors.forEach {
         log { "\t\t${it::class.simpleName}" }
      }
   }

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineInterceptor]s.
    */
   suspend fun execute(suite: TestSuite): EngineResult {
      log { "TestEngine: Executing test suite with ${suite.specs.size} specs" }
      logInterceptors()

      val innerExecute: suspend (TestSuite, TestEngineListener) -> EngineResult = { ts, tel ->
         val scheduler = createTestSuiteScheduler()
         scheduler.schedule(ts, tel)
      }

      val execute = config.interceptors.foldRight(innerExecute) { extension, next ->
         { ts, tel -> extension.intercept(ts, tel, next) }
      }

      // we want to suspend the engine while we wait for all specs to complete
      return coroutineScope {
         execute(suite, config.listener)
      }
   }
}

