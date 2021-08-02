package io.kotest.engine

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.Spec
import io.kotest.engine.extensions.EmptyTestSuiteExtension
import io.kotest.engine.extensions.EngineExtension
import io.kotest.engine.extensions.SpecStyleValidationExtension
import io.kotest.engine.extensions.TestDslStateExtensions
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.mpp.log
import kotlin.reflect.KClass

data class TestEngineConfig(
   val listener: TestEngineListener,
   val extensions: List<EngineExtension>,
   val configuration: Configuration,
) {

   companion object {
      fun default(): TestEngineConfig {

         val engineExtensions = listOfNotNull(
            TestDslStateExtensions,
            SpecStyleValidationExtension,
            if (configuration.failOnEmptyTestSuite) EmptyTestSuiteExtension else null,
         )

         return TestEngineConfig(
            listener = NoopTestEngineListener,
            extensions = engineExtensions,
            configuration = configuration,
         )
      }
   }

   fun withConfig(configuration: Configuration): TestEngineConfig {
      return TestEngineConfig(listener = listener, extensions = extensions, configuration = configuration)
   }
}

data class EngineResult(val errors: List<Throwable>)

/**
 * Contains the discovered specs that will be executed.
 *
 * On the platforms that lack reflective capability, the specs are pre-instantiated before
 * they are passed to the engine. On the JVM, the class definition is instead used.
 */
data class TestSuite(val specs: List<Spec>, val classes: List<KClass<out Spec>>)

/**
 * Multiplatform Kotest Test Engine.
 */
class TestEngine(val config: TestEngineConfig) {

   private val lifecycle = LifecycleEventManager()

   fun execute(suite: TestSuite) {
      require(suite.specs.isNotEmpty()) { "Cannot invoke the engine with no specs" }

      lifecycle.beforeProject(config.configuration.listeners().filterIsInstance<BeforeProjectListener>())

      val innerExecute: (TestSuite, TestEngineListener) -> EngineResult =
         { ts, tel ->

            // spec classes are ordered using SpecExecutionOrderExtension extensions
            val exts = configuration.extensions().filterIsInstance<SpecExecutionOrderExtension>().ifEmpty {
               listOf(DefaultSpecExecutionOrderExtension(configuration.specExecutionOrder))
            }

            log { "KotestEngine: Sorting specs using extensions $exts" }
            val ordered = exts.fold(ts.specs) { acc, op -> op.sortSpecs(acc) }
            execute(ordered, tel)
         }

      val execute = config.extensions.foldRight(innerExecute) { extension, next ->
         { ts, tel -> extension.intercept(ts, tel, next) }
      }

      execute(suite, config.listener)
   }

   private fun execute(specs: List<Spec>, listener: TestEngineListener): EngineResult {
      if (specs.isNotEmpty()) {
         val runner = SpecRunner()
         runner.execute(specs.first()) { execute(specs.drop(1), listener) }
      } else {
         lifecycle.afterProject(config.configuration.listeners().filterIsInstance<AfterProjectListener>())
      }
      return EngineResult(emptyList())
   }
}

expect class LifecycleEventManager() {
   fun beforeProject(listeners: List<BeforeProjectListener>)
   fun afterProject(listeners: List<AfterProjectListener>)
}

expect class SpecRunner() {

   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   fun execute(spec: Spec, onComplete: suspend () -> Unit)
}
