package io.kotest.engine

import io.kotest.engine.extensions.ExtensionException

data class EngineResult(
   val errors: List<Throwable>, // these are errors during engine processing, not test failures
   val testFailures: Boolean,
) {

   constructor(errors: List<Throwable>) : this(errors, false)

   companion object {
      val empty = EngineResult(emptyList(), false)
   }

   fun addError(t: Throwable): EngineResult {
      return copy(errors = this.errors + t)
   }

   fun addErrors(errors: List<ExtensionException.AfterProjectException>): EngineResult {
      return copy(errors = this.errors + errors)
   }

   fun withTestFailures(): EngineResult {
      return copy(testFailures = true)
   }
}
