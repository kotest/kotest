package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.assume
import io.kotest.property.internal.doShrinking

/**
 * Tests for shrink-path recording on [io.kotest.property.internal.ShrinkResult] — the first half of
 * the work for [#3076](https://github.com/kotest/kotest/issues/3076). The recorded path is a
 * sequence of raw child indices into [RTree.children]; replay infrastructure that consumes it lands
 * in a follow-up PR.
 */
class ShrinkPathRecordingTest : FunSpec({

   test("the failing child's raw index is recorded when only one child fails") {
      // ROOT(10) -> [ child0=0 pass, child1=3 fail, child2=7 pass ]
      val tree = RTree({ 10 }, lazy {
         listOf(RTree({ 0 }), RTree({ 3 }), RTree({ 7 }))
      })
      val result = doShrinking(tree, ShrinkingMode.Unbounded) { n: Int ->
         if (n == 3) throw AssertionError("3 fails")
      }
      result.shrink shouldBe 3
      result.path shouldBe listOf(1)
   }

   test("path accumulates as shrinking recurses into a failing child") {
      // ROOT(10) -> [ child0=5 fail -> [ grand0=2 pass, grand1=4 fail ], child1=8 pass ]
      val grand0 = RTree({ 2 })
      val grand1 = RTree({ 4 })
      val child0 = RTree({ 5 }, lazy { listOf(grand0, grand1) })
      val child1 = RTree({ 8 })
      val tree = RTree({ 10 }, lazy { listOf(child0, child1) })

      val result = doShrinking(tree, ShrinkingMode.Unbounded) { n: Int ->
         if (n == 5 || n == 4) throw AssertionError("$n fails")
      }
      result.shrink shouldBe 4
      result.path shouldBe listOf(0, 1)
   }

   test("path is empty when no child fails") {
      val tree = RTree({ 10 }, lazy {
         listOf(RTree({ 0 }), RTree({ 3 }), RTree({ 7 }))
      })
      val result = doShrinking(tree, ShrinkingMode.Unbounded) { /* always passes */ }
      result.shrink shouldBe 10
      result.path shouldBe emptyList()
   }

   test("path is empty when the tree has no children") {
      val tree = RTree<Int>({ 10 }, lazy { emptyList() })
      val result = doShrinking(tree, ShrinkingMode.Unbounded) { throw AssertionError("never runs") }
      result.path shouldBe emptyList()
   }

   test("IterationSkippedException is not a failure, so it does not contribute to the path") {
      // ROOT(10) -> [ child0=2 skipped-by-assume, child1=5 fail ]
      val tree = RTree({ 10 }, lazy {
         listOf(RTree({ 2 }), RTree({ 5 }))
      })
      val result = doShrinking(tree, ShrinkingMode.Unbounded) { n: Int ->
         assume(n >= 4)
         if (n == 5) throw AssertionError("5 fails")
      }
      result.shrink shouldBe 5
      result.path shouldBe listOf(1)
   }

   test("duplicate child values filtered by the tested set do not shift the recorded raw indices") {
      // ROOT -> [ a (pass), a (dup, filtered), b (fail) ] — b's raw index is still 2 even though
      // the dedup-filtered sequence would have placed it at index 1. Raw indices keep replay
      // simple: a future doReplay() can use `children[path[i]]` without reconstructing tested state.
      val tree = RTree({ "root" }, lazy {
         listOf(RTree({ "a" }), RTree({ "a" }), RTree({ "b" }))
      })
      val result = doShrinking(tree, ShrinkingMode.Unbounded) { s: String ->
         if (s == "b") throw AssertionError("b fails")
      }
      result.shrink shouldBe "b"
      result.path shouldBe listOf(2)
   }

   test("StepResult path is composed into a multi-step path on deep recursion") {
      // 3 levels: ROOT -> child[2] fails -> grand[0] fails -> greatGrand[1] fails (leaf)
      val greatGrand0 = RTree({ "leaf0" })
      val greatGrand1 = RTree({ "leaf1" })
      val grand = RTree({ "grand" }, lazy { listOf(greatGrand0, greatGrand1) })
      val child = RTree({ "child" }, lazy { listOf(grand) })
      val tree = RTree({ "root" }, lazy {
         listOf(RTree({ "ok-a" }), RTree({ "ok-b" }), child)
      })

      val result = doShrinking(tree, ShrinkingMode.Unbounded) { s: String ->
         if (s in listOf("child", "grand", "leaf1")) throw AssertionError("$s fails")
      }
      result.shrink shouldBe "leaf1"
      result.path shouldBe listOf(2, 0, 1)
   }
})
