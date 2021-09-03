package io.kotest.engine

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineInterceptor
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
         return TestEngineConfig(
            listener = NoopTestEngineListener,
            interceptors = testEngineInterceptors(),
            configuration = configuration,
         )
      }
   }

   fun withConfig(configuration: Configuration): TestEngineConfig {
      return TestEngineConfig(listener = listener, interceptors = interceptors, configuration = configuration)
   }
}

expect fun testEngineInterceptors(): List<EngineInterceptor>

data class EngineResult(val errors: List<Throwable>) {
   companion object {
      val empty = EngineResult(emptyList())
   }
}

/**
 * Contains the discovered specs that will be executed.
 *
 * On the platforms that lack reflective capability, the specs are pre-instantiated before
 * they are passed to the engine. On the JVM, the class definition is instead used.
 */
data class TestSuite(val specs: List<Spec>, val classes: List<KClass<out Spec>>) {
   companion object {
      val empty = TestSuite(emptyList(), emptyList())
   }
}

/**
 * Multiplatform Kotest Test Engine.
 */
class TestEngine(val config: TestEngineConfig) {

   suspend fun execute(suite: TestSuite) {
      log { "TestEngine: Executing test suite with ${suite.specs.size} specs and ${suite.classes.size} classes" }
      require(suite.specs.isNotEmpty()) { "Cannot invoke the engine with no specs" }

      val innerExecute: suspend (TestSuite, TestEngineListener) -> EngineResult = { ts, tel -> execute(ts.specs, tel) }

      log { "TestEngine: ${config.interceptors.size} engine interceptors:" }
      config.interceptors.forEach {
         log { "TestEngine: ${it::class.simpleName}" }
      }

      val execute = config.interceptors.foldRight(innerExecute) { extension, next ->
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
