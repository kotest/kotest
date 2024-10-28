package io.kotest.permutations

import io.kotest.assertions.print.print
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.permutations.delegates.GenDelegate
import io.kotest.permutations.statistics.Classifications
import io.kotest.property.internal.Counter
import io.kotest.property.internal.ShrinkResult

internal suspend fun shrink(context: PermutationConfiguration, test: suspend Permutation.() -> Unit): List<ShrinkResult<Any?>> {

   // we need to switch each delegate to shrink mode so they lock in the current failed random values
   context.registry.delegates.forEach { it.setShrinking() }

   println("Will begin shrinking for the following failed values\n")
   context.registry.delegates.forEach { delegate ->
      println("Arg (${delegate.property()}) => ${delegate.sample().value.print().value}")
   }
   println()

   // the values after each of the args of the failed iteration have been shrunk (or same value if they could not be)
   return context.registry.delegates.map { delegate: io.kotest.permutations.delegates.GenDelegate<*> ->
      doShrinking(delegate, context, test)
   }
}

internal suspend fun <A> doShrinking(
   delegate: GenDelegate<A>,
   context: PermutationConfiguration,
   test: suspend Permutation.() -> Unit,
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
         test(Permutation(0, RandomSource.default(), Classifications()))
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
