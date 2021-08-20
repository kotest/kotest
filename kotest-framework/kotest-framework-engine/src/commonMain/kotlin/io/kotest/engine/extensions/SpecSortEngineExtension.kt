package io.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.mpp.log

/**
 * An [EngineExtension] that sorts specs according to registered [SpecExecutionOrderExtension]s
 * or falling back to the [DefaultSpecExecutionOrderExtension].
 */
object SpecSortEngineExtension : EngineExtension {
   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      // spec classes are ordered using SpecExecutionOrderExtension extensions
      val exts = configuration.extensions().filterIsInstance<SpecExecutionOrderExtension>().ifEmpty {
         listOf(DefaultSpecExecutionOrderExtension(configuration.specExecutionOrder))
      }

      log { "SpecSortEngineExtension: Sorting specs using extensions $exts" }
      val specs = exts.fold(suite.specs) { acc, op -> op.sortSpecs(acc) }
      val classes = exts.fold(suite.classes) { acc, op -> op.sortClasses(acc) }
      return execute(suite.copy(specs = specs, classes = classes), listener)
   }
}
