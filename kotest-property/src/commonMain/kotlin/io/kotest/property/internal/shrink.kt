package io.kotest.property.internal

import io.kotest.assertions.print.print
import io.kotest.common.stacktrace.stacktraces
import io.kotest.engine.IterationSkippedException
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.isEmpty

/**
 * Accepts a value of type A and a function that varies in type A (fixed in any other types) and attempts
 * to shrink the value to find the smallest failing case.
 *
 * For each step in the shrinker, we test all the values. If they all pass then the shrinking ends.
 * Otherwise, the next batch is taken and the shrinks continue.
 *
 * Once all values from a shrink step pass, we return the previous value as the "smallest" failing case
 * along with the reason for the failure.
 *
 * If [replayPath] is provided, the search is skipped and the recorded path is followed directly to
 * the shrunk value via [doReplay]. This lets a user re-run a previously discovered failure without
 * paying for the full shrink search — see issue
 * [#3076](https://github.com/kotest/kotest/issues/3076).
 */
@JvmOverloads
suspend fun <A> doShrinking(
   initial: RTree<A>,
   mode: ShrinkingMode,
   replayPath: List<Int>? = null,
   test: suspend (A) -> Unit
): ShrinkResult<A> {

   if (replayPath != null) return doReplay(initial, replayPath, test)

   if (initial.isEmpty()) return ShrinkResult(initial.value(), initial.value(), null)

   val counter = Counter()
   val tested = mutableSetOf<A>()
   val sb = StringBuilder()
   sb.append("Attempting to shrink arg ${initial.value().print().value}\n")

   val stepResult = doStep(initial, mode, tested, counter, test, sb)
   result(sb, stepResult, counter.count)

   return if (stepResult == null) {
      ShrinkResult(initial.value(), initial.value(), null)
   } else {
      ShrinkResult(initial.value(), stepResult.failed, stepResult.cause, stepResult.path)
   }
}

class Counter {
   var count = 0
   fun inc() = count++
}

/**
 * The result of shrinking a failed arg.
 * If no shrinking took place, shrink should be set to the same as the initial.
 * Each arg that was part of the failed test will have a [ShrinkResult] associated with it.
 *
 * [path] records the sequence of raw child indices (into [RTree.children]) traversed during the
 * shrinking search. An empty path means no shrinking actually took place. Recording the path
 * makes it possible for a follow-up to replay a known failure directly — see issue
 * [#3076](https://github.com/kotest/kotest/issues/3076).
 */
data class ShrinkResult<out A> @JvmOverloads constructor(
   val initial: A,
   val shrink: A,
   val cause: Throwable?,
   val path: List<Int> = emptyList(),
)

data class StepResult<A> @JvmOverloads constructor(
   val failed: A,
   val cause: Throwable,
   val path: List<Int> = emptyList(),
)

/**
 * Performs shrinking on the given RTree. Recurses into the tree for failing cases.
 * Returns the last candidate to fail as a [StepResult] or null if the initial passes.
 */
suspend fun <A> doStep(
   tree: RTree<A>,
   mode: ShrinkingMode,
   tested: MutableSet<A>,
   counter: Counter,
   test: suspend (A) -> Unit,
   sb: StringBuilder
): StepResult<A>? {

   // if no more shrinking return null (if we've hit the bounds)
   if (!mode.isShrinking(counter.count)) return null
   val candidates = tree.children.value

   // We iterate over raw indices (not the dedup-filtered sequence) so that the recorded
   // [StepResult.path] uses positions in [RTree.children] directly. That keeps any future
   // replay simple: walk `children[path[i]]` at each level without having to reconstruct
   // the `tested` state we built up during the search.
   for ((rawIdx, a) in candidates.withIndex()) {
      val candidate = a.value()
      // shrinkers might generate duplicate candidates so we must filter them out to avoid infinite loops or slow shrinking
      if (!tested.add(candidate)) continue
      counter.inc()
      try {
         test(candidate)
         if (PropertyTesting.shouldPrintShrinkSteps)
            sb.append("Shrink #${counter.count}: ${candidate.print().value} pass\n")
      } catch (_: IterationSkippedException) {
         // A skipped iteration (from assume()) is not a failure — the shrunk value does not
         // satisfy the precondition so it is not a valid counterexample. Treat it as a pass.
         if (PropertyTesting.shouldPrintShrinkSteps)
            sb.append("Shrink #${counter.count}: ${candidate.print().value} skip\n")
      } catch (t: Throwable) {
         if (PropertyTesting.shouldPrintShrinkSteps)
            sb.append("Shrink #${counter.count}: ${candidate.print().value} fail\n")
         // this result failed, so we'll recurse in to find further failures otherwise return this candidate
         val deeperResult = doStep(a, mode, tested, counter, test, sb)
         return if (deeperResult != null) {
            deeperResult.copy(path = listOf(rawIdx) + deeperResult.path)
         } else {
            StepResult(candidate, t, listOf(rawIdx))
         }
      }
   }

   return null
}

/**
 * Replays a previously-recorded shrink path on the given [RTree] without running the full shrink
 * search. Walks [path] by indexing into [RTree.children] at each level, then runs [test] once on
 * the final node's value to obtain the failure.
 *
 * Replay is strict by design: if the path can no longer be followed (an index is out of bounds
 * because the [io.kotest.property.Shrinker] changed since the path was recorded), or if the final
 * value no longer reproduces the failure, this throws [ReplayShrinkPathException] rather than
 * silently falling back. Silent fallback would otherwise label a non-failing value as if it were
 * the shrunk counterexample, defeating the point of replay.
 *
 * The thrown exception is caught by [handleException] and surfaced as a normal property-test
 * failure with a "replay was skipped" note, so users can just rerun without `shrinkPaths` to
 * perform a full shrink.
 */
internal suspend fun <A> doReplay(
   initial: RTree<A>,
   path: List<Int>,
   test: suspend (A) -> Unit
): ShrinkResult<A> {
   var current = initial
   for ((step, idx) in path.withIndex()) {
      val children = current.children.value
      if (idx < 0 || idx >= children.size) {
         throw ReplayShrinkPathException(
            "Replay shrink path $path is invalid at step $step: index $idx is out of range for " +
               "children of size ${children.size}. The Shrinker for this type may have changed " +
               "since the path was recorded; rerun without shrinkPaths to perform a full shrink."
         )
      }
      current = children[idx]
   }

   val finalValue = current.value()
   val cause: Throwable? = try {
      test(finalValue)
      null
   } catch (e: IterationSkippedException) {
      throw ReplayShrinkPathException(
         "Replay shrink path $path did not reproduce a failure: the test was skipped by assume() " +
            "on the replayed value ${finalValue.print().value}. The property's assumptions may have " +
            "changed since the path was recorded; rerun without shrinkPaths to perform a full shrink.",
         e
      )
   } catch (t: Throwable) {
      t
   }
   if (cause == null) {
      throw ReplayShrinkPathException(
         "Replay shrink path $path did not reproduce a failure: the test passed on the replayed " +
            "value ${finalValue.print().value}. The Shrinker or the property under test may have " +
            "changed since the path was recorded; rerun without shrinkPaths to perform a full shrink."
      )
   }
   return ShrinkResult(initial.value(), finalValue, cause, path)
}

/**
 * Thrown by [doReplay] when a [io.kotest.property.PropTestConfig.shrinkPaths] entry can no longer
 * be followed to a genuine failure — either an index is out of range for the current tree, or the
 * final replayed value no longer reproduces the original failure.
 *
 * Kept `internal` so the type does not enter the public ABI. It is caught by [handleException]
 * during the shrink phase: a `Note: shrink replay was skipped — ...` line is printed, and the
 * property test still fails through the normal failure path so users see seed, eval index, and
 * the original cause.
 */
internal class ReplayShrinkPathException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Returns true if we should continue shrinking given the count.
 */
private fun ShrinkingMode.isShrinking(count: Int): Boolean = when (this) {
   ShrinkingMode.Off -> false
   ShrinkingMode.Unbounded -> true
   is ShrinkingMode.Bounded -> count < bound
}

private fun <A> result(sb: StringBuilder, result: StepResult<A>?, count: Int) {
   if (count == 0 || result == null) {
      sb.append("Arg was not shrunk\n")
   } else {
      sb.append("Shrink result (after $count shrinks) => ${result.failed.print().value}\n\n")
      when (val location = stacktraces.throwableLocation(result.cause, 4)) {
         null -> sb.append("Caused by ${result.cause}\n")
         else -> {
            sb.append("Caused by ${result.cause} at\n")
            location.forEach { sb.append("\t$it\n") }
         }
      }
   }
   if (PropertyTesting.shouldPrintShrinkSteps) {
      println(sb)
   }
}

/**
 * The contextual shrinking is  only used for output purposes for the time being.
 * Contextual arb shrinking is theoretically possible. A strategy that may work is to treat shrinks
 * as a standard Vector of arbitrary dimension. However, this will need to be discussed and addressed separately.
 */
internal suspend fun PropertyContext.doContextualShrinking(
   mode: ShrinkingMode,
   test: suspend PropertyContext.() -> Unit
): List<ShrinkResult<*>> = generatedSamples().map {
   ShrinkResult(it.value, it.value, null)
}

