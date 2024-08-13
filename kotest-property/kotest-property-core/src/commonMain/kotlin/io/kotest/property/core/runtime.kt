package io.kotest.property.core

import io.kotest.assertions.print.print
import io.kotest.property.AssumptionFailedException
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.internal.Counter
import io.kotest.property.internal.ShrinkResult
import io.kotest.property.internal.throwPropertyTestAssertionError

internal suspend fun executePropTest(context: PermutationContext, test: suspend PermutationContext.() -> Unit) {

   if (context.seed != null && context.failOnSeed)
      error("A seed is specified on this property test and failOnSeed is true")

   val constraints = context.constraints
      ?: context.iterations?.let { Constraints.iterations(it) }
      ?: context.duration?.let { Constraints.duration(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   var k = 0
   while (constraints.evaluate(Iteration(k))) {
      try {
         context.container.reset()
         context.beforePermutation()
         test(context)
         context.afterPermutation()
      } catch (e: AssumptionFailedException) {
         // we don't mark failed assumptions as errors
      } catch (e: Throwable) {
         // we track any throwables and try to shrink them
         val shrinks = shrink(context, test)
         throwPropertyTestAssertionError(shrinks, AssertionError(e), k, context.rs.value.seed)
      }
      k++
   }
}

internal suspend fun shrink(context: PermutationContext, test: suspend PermutationContext.() -> Unit): List<ShrinkResult<Any?>> {

   // we need to switch each delegate to shrink mode so they lock in the current failed random values
   context.container.delegates.forEach { it.setShrinking() }

   println("Will begin shrinking for the following failed values\n")
   context.container.delegates.forEach { delegate ->
      println("Arg (${delegate.property()}) => ${delegate.sample().value.print().value}")
   }
   println()

   // the values after each of the args of the failed iteration have been shrunk (or same value if they could not be)
   return context.container.delegates.map { delegate ->
      doShrinking(delegate, context, test)
   }
}

internal suspend fun <A> doShrinking(
   delegate: GenDelegate<A>,
   context: PermutationContext,
   test: suspend PermutationContext.() -> Unit,
): ShrinkResult<A> {

   // if no more shrinking return null (if we've hit the bounds)
   // todo if (!mode.isShrinking(counter.count)) return null

   val counter = Counter()
   val initial = delegate.sample()
   var result = ShrinkResult(initial.value, delegate.sample().value, null)

   val sb = StringBuilder()
   sb.append("Attempting to shrink arg (${delegate.property()})\n")

   while (delegate.hasNextCandidate()) {
      val candidate = delegate.candidate().value()
      try {
         test(context)
         if (context.shouldPrintShrinkSteps)
            sb.append("Shrink #${counter.count}: ${candidate.print().value} pass\n")
      } catch (t: Throwable) {
         if (context.shouldPrintShrinkSteps)
            sb.append("Shrink #${counter.count}: ${candidate.print().value} fail\n")
         // this result failed, so we'll set the result to be this candidate for now
         // and replace the candidate list with this candidate's children
         result = ShrinkResult(initial.value, candidate, t)
         delegate.nextCandidates()
      }
      counter.inc()
   }

   if (context.shouldPrintShrinkSteps)
      printResult(sb, result, counter.count)

   println(sb)
   return result
}

// todo print out each step if enabled
private fun <A> printResult(sb: StringBuilder, result: ShrinkResult<A>, count: Int) {
   if (count == 0 || result.initial == result.shrink) {
      sb.append("Arg was not shunk\n")
   } else {
      sb.append("Shrink result (after $count shrinks) => ${result.shrink.print().value}\n\n")
//      when (val location = stacktraces.throwableLocation(result.cause, 4)) {
//         null -> sb.append("Caused by ${result.cause}\n")
//         else -> {
//            sb.append("Caused by ${result.cause} at\n")
//            location.forEach { sb.append("\t$it\n") }
//         }
//      }
   }
}

/**
 * Returns true if we should continue shrinking given the shrinks performed so far.
 */
private fun ShrinkingMode.isShrinking(iterations: Int): Boolean = when (this) {
   ShrinkingMode.Off -> false
   ShrinkingMode.Unbounded -> true
   is ShrinkingMode.Bounded -> iterations < bound
}
