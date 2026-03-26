package io.kotest.engine

@ConsistentCopyVisibility
data class EngineResult internal constructor(
   val errors: List<Throwable>, // these are errors at the project level, not test failures
) {

   constructor() : this(emptyList())

   fun addError(error: Throwable): EngineResult = addErrors(listOf(error))

   fun addErrors(errors: List<Throwable>): EngineResult {
      return copy(errors = this.errors + errors)
   }
}
