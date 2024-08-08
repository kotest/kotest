package io.kotest.property.state

import io.kotest.property.RandomSource

/**
 * A function that is applied against the current state to mutate and/or test.
 */
interface Action<T> {

   /**
    * A predicate which decides if this action should be applied.
    *
    * For example, if you were writing a stateful test for a stack, you would want
    * to skip 'pop' operations if the stack was empty.
    *
    * @return true if this action should be applied or false if the action should be skipped.
    */
   suspend fun precondition(state: T): Boolean

   /**
    * Invokes the next transition to the state.
    *
    * This function should contain your test assertions.
    */
   suspend fun run(state: T, rs: RandomSource)

   companion object {

      /**
       * Creates a new [Action] that is always applied (precondition is set to constant true).
       */
      operator fun <T> invoke(f: (T, RandomSource) -> Unit): Action<T> {
         return object : Action<T> {
            override suspend fun precondition(state: T): Boolean = true
            override suspend fun run(state: T, rs: RandomSource) = f(state, rs)
         }
      }
   }
}

suspend fun <T> checkState(input: T, vararg actions: Action<T>) {
   checkState(input, 1000, *actions)
}

suspend fun <T> checkState(input: T, iterations: Int, vararg actions: Action<T>) {
   checkState(input, iterations, RandomSource.default(), *actions)
}

suspend fun <T> checkState(input: T, iterations: Int, rs: RandomSource, vararg actions: Action<T>) {
   repeat(iterations) {
      val action = actions.random()
      if (action.precondition(input)) action.run(input, rs)
   }
}
