package io.kotest.engine

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.SpecStyleValidationInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log
import kotlin.reflect.KClass

data class TestEngineConfig(
   val listener: TestEngineListener,
   val interceptors: List<EngineInterceptor>,
   val configuration: Configuration,
) {

   companion object {
      fun default(): TestEngineConfig {

         val interceptors = listOfNotNull(
            TestDslStateInterceptor,
            SpecStyleValidationInterceptor,
            SpecSortEngineInterceptor,
            ProjectListenerEngineInterceptor(configuration.extensions()),
            if (configuration.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
         )

         return TestEngineConfig(
            listener = NoopTestEngineListener,
            interceptors = interceptors,
            configuration = configuration,
         )
      }
   }

   fun withConfig(configuration: Configuration): TestEngineConfig {
      return TestEngineConfig(listener = listener, interceptors = interceptors, configuration = configuration)
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

   suspend fun execute(suite: TestSuite) {
      log { "TestEngine: Executing test suite with ${suite.specs.size} specs and ${suite.classes.size} classes" }
      require(suite.specs.isNotEmpty()) { "Cannot invoke the engine with no specs" }

      val innerExecute: suspend (TestSuite, TestEngineListener) -> EngineResult = { ts, tel -> execute(ts.specs, tel) }

      val extensions = config.interceptors
      log { "TestEngine: ${extensions.size} engine extensions:" }
      extensions.forEach {
         log { "TestEngine: ${it::class.simpleName}" }
      }

      val execute = extensions.foldRight(innerExecute) { extension, next ->
         { ts, tel -> extension.intercept(ts, tel, next) }
      }

      execute(suite, config.listener)
   }

   private fun execute(specs: List<Spec>, listener: TestEngineListener): EngineResult {
      log { "TestEngine: Executing ${specs.size} specs" }
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
