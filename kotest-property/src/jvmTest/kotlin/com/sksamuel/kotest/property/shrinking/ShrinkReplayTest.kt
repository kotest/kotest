package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import io.kotest.property.internal.ReplayShrinkPathException
import io.kotest.property.internal.doReplay
import io.kotest.property.internal.doShrinking

/**
 * Tests for shrink replay via [PropTestConfig.shrinkPaths] for issue
 * [#3076](https://github.com/kotest/kotest/issues/3076).
 *
 * Once a property test has failed, the failure message includes the recorded shrink path (from the
 * Part 1 PR). Re-running with that path in [PropTestConfig.shrinkPaths] jumps directly to the
 * shrunk value instead of repeating the full search.
 *
 * Replay is strict by design: a stale or otherwise unusable path raises [ReplayShrinkPathException]
 * rather than silently falling back, so users are never misled about which value reproduced the
 * failure. The exception is caught by `handleException` and the user sees a normal property-test
 * failure with a "replay was skipped" note.
 */
class ShrinkReplayTest : FunSpec({

   test("doReplay follows the recorded path and returns the value at its end") {
      val leaf0 = RTree({ "leaf0" })
      val leaf1 = RTree({ "leaf1" })
      val grand = RTree({ "grand" }, lazy { listOf(leaf0, leaf1) })
      val child = RTree({ "child" }, lazy { listOf(grand) })
      val tree = RTree({ "root" }, lazy {
         listOf(RTree({ "skip" }), child)
      })

      var invocations = 0
      val result = doReplay(tree, listOf(1, 0, 1)) { _: String ->
         invocations++
         throw AssertionError("replay")
      }

      // Path 1->0->1: root.children[1]=child -> child.children[0]=grand -> grand.children[1]=leaf1
      result.shrink shouldBe "leaf1"
      result.path shouldBe listOf(1, 0, 1)
      // Replay must only invoke the test once on the final node, never at intermediate levels.
      invocations shouldBe 1
   }

   test("doReplay throws when an index is out of bounds (stale path)") {
      val tree = RTree({ 10 }, lazy { listOf(RTree({ 5 })) })
      val ex = shouldThrow<ReplayShrinkPathException> {
         doReplay(tree, listOf(0, 99)) { /* never reached */ }
      }
      ex.message shouldContain "out of range"
      ex.message shouldContain "step 1"
      ex.message shouldContain "rerun without shrinkPaths"
   }

   test("doReplay throws when a negative index is supplied") {
      val tree = RTree({ 10 }, lazy { listOf(RTree({ 5 })) })
      val ex = shouldThrow<ReplayShrinkPathException> {
         doReplay(tree, listOf(-1)) { /* never reached */ }
      }
      ex.message shouldContain "out of range"
   }

   test("doReplay throws when the final value no longer reproduces the failure (test passes)") {
      val tree = RTree({ 10 }, lazy { listOf(RTree({ 5 })) })
      val ex = shouldThrow<ReplayShrinkPathException> {
         doReplay(tree, listOf(0)) { /* always passes */ }
      }
      ex.message shouldContain "did not reproduce"
      ex.message shouldContain "test passed"
      ex.message shouldContain "5"
   }

   test("doReplay throws when the final value is rejected by assume() (assumptions changed)") {
      val tree = RTree({ 10 }, lazy { listOf(RTree({ 5 })) })
      val ex = shouldThrow<ReplayShrinkPathException> {
         doReplay(tree, listOf(0)) { _: Int ->
            io.kotest.property.assume(false)
         }
      }
      ex.message shouldContain "skipped by assume()"
   }

   test("doShrinking dispatches to doReplay when replayPath is given (no full search)") {
      var searches = 0
      val tree = RTree({ 10 }, lazy {
         listOf(RTree({ 7 }), RTree({ 3 }), RTree({ 1 }))
      })
      // Without replay the search would visit children in order. With replay path [2] it must
      // jump straight to children[2] (= 1) and never touch the others.
      val result = doShrinking(tree, ShrinkingMode.Unbounded, replayPath = listOf(2)) { n: Int ->
         searches++
         throw AssertionError("$n always fails in this test")
      }
      result.shrink shouldBe 1
      result.path shouldBe listOf(2)
      searches shouldBe 1
   }

   test("end-to-end: feeding Eval index + Shrink paths back via PropTestConfig replays to the same shrunk value (proven via ShrinkingMode.Off)") {
      val seed = 1234L

      val originalErr = shouldThrow<AssertionError> {
         forAll(
            PropTestConfig(seed = seed, shrinkingMode = ShrinkingMode.Bounded(50)),
            Arb.int()
         ) { n -> n >= 0 }
      }
      val originalMsg = originalErr.message!!
      val evalIndex = Regex("Eval index: (\\d+)").find(originalMsg)?.groupValues?.get(1)?.toInt()
      val shrinkPathsText = Regex("Shrink paths: (\\[\\[[^\\n]*\\]\\])").find(originalMsg)?.groupValues?.get(1)
      val originalArg0 = Regex("Arg 0: ([^\\n]+)").find(originalMsg)?.groupValues?.get(1)

      evalIndex shouldNotBe null
      shrinkPathsText shouldNotBe null
      originalArg0 shouldNotBe null

      val parsedShrinkPaths = parseShrinkPaths(shrinkPathsText!!)

      // ShrinkingMode.Off + shrinkPaths: if the implementation weren't honoring the recorded path
      // and instead doing a search, Off would mean we'd see the initial (unshrunk) value. The fact
      // that we still get the same shrunk Arg 0 proves the path was followed, not searched.
      val replayErr = shouldThrow<AssertionError> {
         forAll(
            PropTestConfig(
               seed = seed,
               skipTo = evalIndex!!,
               shrinkPaths = parsedShrinkPaths,
               shrinkingMode = ShrinkingMode.Off,
            ),
            Arb.int()
         ) { n -> n >= 0 }
      }
      val replayArg0 = Regex("Arg 0: ([^\\n]+)").find(replayErr.message!!)?.groupValues?.get(1)
      replayArg0 shouldBe originalArg0
   }

   test("a malformed shrinkPaths (size != property arity) surfaces as a regular property failure, not a leaked exception") {
      // 2-arg property but only 1 path entry → arity mismatch. requireShrinkPathsArity throws
      // ReplayShrinkPathException, which handleException catches and reformats.
      val replayErr = shouldThrow<AssertionError> {
         forAll(
            PropTestConfig(seed = 1234L, shrinkPaths = listOf(listOf(0)), shrinkingMode = ShrinkingMode.Off),
            Arb.int(),
            Arb.int(),
         ) { a, _ -> a >= 0 }
      }
      val msg = replayErr.message!!
      msg shouldContain "Property failed"
      msg shouldContain "Repeat this test by using seed 1234"
      msg shouldContain "Eval index:"
   }

   test("a stale shrinkPaths surfaces as a regular property failure with a replay-skipped note") {
      // Seed a real failure first to capture a valid path, then corrupt it.
      val baseErr = shouldThrow<AssertionError> {
         forAll(PropTestConfig(seed = 1234L), Arb.int()) { n -> n >= 0 }
      }
      val basePathsText = Regex("Shrink paths: (\\[\\[[^\\n]*\\]\\])").find(baseErr.message!!)?.groupValues?.get(1)!!
      val basePaths = parseShrinkPaths(basePathsText)
      val stalePaths = listOf(basePaths.first() + 9999) // tack on an out-of-bounds index

      val replayErr = shouldThrow<AssertionError> {
         forAll(
            PropTestConfig(seed = 1234L, shrinkPaths = stalePaths, shrinkingMode = ShrinkingMode.Off),
            Arb.int()
         ) { n -> n >= 0 }
      }
      val msg = replayErr.message!!
      msg shouldContain "Property failed"
      msg shouldContain "Repeat this test by using seed 1234"
      msg shouldContain "Eval index:"
      // The replay-skipped note goes to stdout, not the assertion message — the assertion stays a
      // regular property failure rather than a raw ReplayShrinkPathException.
   }

   test("with maxFailure > 0, the shrinker is not invoked for allowed failures (only on the failure that crosses the threshold)") {
      // Pre-#3076, shrinkfn() ran only inside handleException's throw branches, so it executed
      // exactly once — when failure #(maxFailure+1) crossed the threshold. The replay refactor
      // briefly moved shrinkfn() outside the throw branches, which would have run it on every
      // allowed failure. This pins the original lazy behavior.
      var invocations = 0
      val maxFailure = 5
      val shrinkBound = 50

      shouldThrow<AssertionError> {
         forAll(
            PropTestConfig(
               seed = 1234L,
               maxFailure = maxFailure,
               shrinkingMode = ShrinkingMode.Bounded(shrinkBound),
            ),
            Arb.int(),
         ) { _ ->
            invocations++
            false // always fail
         }
      }

      // Lazy shrink (correct): ~(maxFailure+1) initial calls + shrink probes for the final
      // failure ≤ shrinkBound. Eager shrink (regression): + shrinkBound per allowed failure.
      // The bound below catches the regression cleanly while leaving comfortable headroom for
      // the correct behavior.
      invocations shouldBeLessThan (maxFailure + 1 + 2 * shrinkBound)
   }

   test("shrinkPaths config field round-trips through PropTestConfig copy") {
      val base = PropTestConfig()
      val with = base.copy(shrinkPaths = listOf(listOf(1, 2, 3)))
      with.shrinkPaths shouldBe listOf(listOf(1, 2, 3))
      base.shrinkPaths shouldBe null
   }

   test("the failure message contains Shrink paths and Eval index lines") {
      val err = shouldThrow<AssertionError> {
         forAll(PropTestConfig(seed = 1234L), Arb.int()) { n -> n >= 0 }
      }
      val msg = err.message!!
      msg shouldContain "Shrink paths:"
      msg shouldContain "Eval index:"
      msg shouldContain "Repeat this test by using seed 1234"
   }
})

/**
 * Parses the `Shrink paths: ...` value emitted in failure messages back into a `List<List<Int>>`.
 * Accepts strings like `[[0]]`, `[[0, 1], [2]]`, `[[]]`, or `[]`.
 */
private fun parseShrinkPaths(text: String): List<List<Int>> {
   val outer = text.trim().removeSurrounding("[", "]")
   if (outer.isBlank()) return emptyList()
   val result = mutableListOf<List<Int>>()
   var depth = 0
   val current = StringBuilder()
   for (c in outer) {
      when {
         c == '[' -> { depth++; current.append(c) }
         c == ']' -> { depth--; current.append(c) }
         c == ',' && depth == 0 -> { result.add(parseIntList(current.toString())); current.clear() }
         else -> current.append(c)
      }
   }
   if (current.toString().isNotBlank()) result.add(parseIntList(current.toString()))
   return result
}

private fun parseIntList(text: String): List<Int> {
   val inner = text.trim().removeSurrounding("[", "]").trim()
   if (inner.isBlank()) return emptyList()
   return inner.split(",").map { it.trim().toInt() }
}
