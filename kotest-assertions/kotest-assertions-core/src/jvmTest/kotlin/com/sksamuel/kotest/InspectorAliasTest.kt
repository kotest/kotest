package com.sksamuel.kotest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.*
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class InspectorAliasTest : FunSpec({

   val array = arrayOf(1, 2, 3)
   val list = listOf(1, 2, 3)
   val sequence = sequenceOf(1, 2, 3)

   suspend fun dummySuspendFunction(x: Int) = x

   context("forAll") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 0

      test("array") {
         array.shouldForAll {
            it shouldBeLessThan 4
         }
         shouldThrowAny {
            array.shouldForAll {
               it shouldBeLessThan 3
            }
         }
      }

      test("array with suspend function") {
         array.shouldForAll { suspendBlock(it) }
      }

      test("list") {
         list.shouldForAll(::block)
         shouldThrowAny {
            list.shouldForAll {
               it shouldBeLessThan 3
            }
         }
      }

      test("list with suspend function") {
         list.shouldForAll { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAll(::block)
         shouldThrowAny {
            sequence.shouldForAll {
               it shouldBeLessThan 3
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForAll { suspendBlock(it) }
      }
   }

   context("forOne") {
      fun block(x: Int) = x shouldBe 2
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBe 2

      test("array") {
         array.shouldForOne(::block)
         shouldThrowAny {
            array.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("array with suspend function") {
         array.shouldForOne { suspendBlock(it) }
      }

      test("list") {
         list.shouldForOne(::block)
         shouldThrowAny {
            list.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("list with suspend function") {
         list.shouldForOne { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForOne(::block)
         shouldThrowAny {
            sequence.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForOne { suspendBlock(it) }
      }
   }

   context("forExactly") {
      fun block(x: Int) = x shouldBeGreaterThan 1
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 1
      val n = 2

      test("array") {
         array.shouldForExactly(n, ::block)
         shouldThrowAny {
            array.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }

      test("array with suspend function") {
         array.shouldForExactly(n) { suspendBlock(it) }
      }

      test("list") {
         list.shouldForExactly(n, ::block)
         shouldThrowAny {
            list.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }

      test("list with suspend function") {
         list.shouldForExactly(n) { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForExactly(n, ::block)
         shouldThrowAny {
            sequence.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForExactly(n) { suspendBlock(it) }
      }
   }

   context("forSome") {
      fun block(x: Int) = x shouldBeGreaterThan 2
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 2

      test("array") {
         array.shouldForSome(::block)
         shouldThrowAny {
            array.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }

      test("array with suspend function") {
         array.shouldForSome { suspendBlock(it) }
      }

      test("list") {
         list.shouldForSome(::block)
         shouldThrowAny {
            list.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }

      test("list with suspend function") {
         list.shouldForSome { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForSome(::block)
         shouldThrowAny {
            sequence.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForSome { suspendBlock(it) }
      }
   }

   context("forAny") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 0

      test("array") {
         array.shouldForAny(::block)
         shouldThrowAny {
            array.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }

      test("array with suspend function") {
         array.shouldForAny { suspendBlock(it) }
      }

      test("list") {
         list.shouldForAny(::block)
         shouldThrowAny {
            list.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }

      test("list with suspend function") {
         list.shouldForAny { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAny(::block)
         shouldThrowAny {
            sequence.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForAny { suspendBlock(it) }
      }
   }

   context("forAtLeast") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 0
      val n = 3

      test("array") {
         array.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            array.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }

      test("array with suspend function") {
         array.shouldForAtLeast(n) { suspendBlock(it) }
      }

      test("list") {
         list.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            list.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }

      test("list with suspend function") {
         list.shouldForAtLeast(n) { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            sequence.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForAtLeast(n) { suspendBlock(it) }
      }
   }

   context("forAtLeastOne") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 0

      test("array") {
         array.shouldForAtLeastOne(::block)
         shouldThrowAny {
            array.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("array with suspend function") {
         array.shouldForAtLeastOne { suspendBlock(it) }
      }

      test("list") {
         list.shouldForAtLeastOne(::block)
         shouldThrowAny {
            list.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("list with suspend function") {
         list.shouldForAtLeastOne { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAtLeastOne(::block)
         shouldThrowAny {
            sequence.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForAtLeastOne { suspendBlock(it) }
      }
   }

   context("forAtMost") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeGreaterThan 0

      test("array") {
         val arr = arrayOf(0, 0, 1)
         arr.shouldForAtMost(1, ::block)
         shouldThrowAny {
            arr.shouldForAtMost(1) {
               it shouldBeLessThan 3
            }
         }
      }

      test("array with suspend function") {
         val arr = arrayOf(0, 0, 1)
         arr.shouldForAtMost(1) { suspendBlock(it) }
      }

      test("list") {
         val l = listOf(0, 1, 1)
         l.shouldForAtMost(2, ::block)
         shouldThrowAny {
            l.shouldForAtMost(2) {
               it shouldBeLessThan 3
            }
         }
      }

      test("list with suspend function") {
         val l = listOf(0, 1, 1)
         l.shouldForAtMost(2) { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAtMost(3, ::block)
         shouldThrowAny {
            sequence.shouldForAtMost(2) {
               it shouldBeLessThan 4
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForAtMost(3) { suspendBlock(it) }
      }
   }

   context("forNone") {
      fun block(x: Int) = x shouldBeLessThan 1
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBeLessThan 1

      test("array") {
         array.shouldForNone(::block)
         shouldThrowAny {
            array.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }

      test("array with suspend function") {
         array.shouldForNone { suspendBlock(it) }
      }

      test("list") {
         list.shouldForNone(::block)
         shouldThrowAny {
            list.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }

      test("list with suspend function") {
         list.shouldForNone { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForNone(::block)
         shouldThrowAny {
            sequence.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }

      test("sequence with suspend function") {
         sequence.shouldForNone { suspendBlock(it) }
      }
   }

   context("forAtMostOne") {
      fun block(x: Int) = x shouldBe 1
      suspend fun suspendBlock(x: Int) = dummySuspendFunction(x) shouldBe 1

      test("array") {
         array.shouldForAtMostOne(::block)
      }

      test("array with suspend function") {
         array.shouldForAtMostOne { suspendBlock(it) }
      }

      test("list") {
         list.shouldForAtMostOne(::block)
      }

      test("list with suspend function") {
         list.shouldForAtMostOne { suspendBlock(it) }
      }

      test("sequence") {
         sequence.shouldForAtMostOne(::block)
      }

      test("sequence with suspend function") {
         sequence.shouldForAtMostOne { suspendBlock(it) }
      }
   }
})
