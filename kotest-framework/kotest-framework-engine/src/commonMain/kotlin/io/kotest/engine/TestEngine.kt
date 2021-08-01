package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.extensions.EmptyTestSuiteExtension
import io.kotest.engine.extensions.EngineExtension
import io.kotest.engine.extensions.SpecStyleValidationExtension
import io.kotest.engine.extensions.TestDslStateExtensions
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.sort
import io.kotest.mpp.log
import kotlin.reflect.KClass

data class TestEngineConfig(
   val listener: TestEngineListener,
   val extensions: List<EngineExtension>,
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
         )
      }
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

class TestEngine(val config: TestEngineConfig) {

   fun execute(suite: TestSuite) {
      require(suite.specs.isNotEmpty()) { "Cannot invoke the engine with no specs" }

      val innerExecute: (TestSuite, TestEngineListener) -> EngineResult =
         { ts, tel ->
            log { "TestEngine: Sorting specs by ${configuration.specExecutionOrder}" }
            val ordered = ts.specs.sort(configuration.specExecutionOrder)
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
      }
      return EngineResult(emptyList())
   }
}

expect class SpecRunner() {

   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   fun execute(spec: Spec, onComplete: suspend () -> Unit)
}
