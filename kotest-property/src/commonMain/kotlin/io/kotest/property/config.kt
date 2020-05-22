package io.kotest.property

import io.kotest.mpp.sysprop

/**
 * Global object for containing settings for property testing.
 */
object PropertyTesting {
   var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", "false") == "true"
   var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", "true") == "true"
   var defaultIterationCount: Int = sysprop("kotest.proptest.default.iteration.count", "1000").toInt()
}

data class PropTest(
   val seed: Long? = null,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000),
   val iterations: Int? = null,
   val listeners: MutableList<PropTestListener> = mutableListOf()
)

fun PropTest.toPropTestConfig() =
   PropTestConfig(
      seed = seed,
      minSuccess = minSuccess,
      maxFailure = maxFailure,
      iterations = iterations,
      shrinkingMode = shrinkingMode,
      listeners = listeners
   )

data class PropTestConfig(
   val seed: Long? = null,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000),
   val iterations: Int? = null,
   val listeners: MutableList<PropTestListener> = mutableListOf()
) {
   fun beforeTest(before: suspend () -> Unit) {
      listeners.add(object : PropTestListener {
         override suspend fun beforeTest() {
            before()
            super.beforeTest()
         }
      })
   }
   fun afterTest(after: suspend () -> Unit) {
      listeners.add(object : PropTestListener {
         override suspend fun afterTest() {
            after()
            super.afterTest()
         }
      })
   }
}

interface PropTestListener {
   suspend fun beforeTest(): Unit = Unit
   suspend fun afterTest(): Unit = Unit
}
