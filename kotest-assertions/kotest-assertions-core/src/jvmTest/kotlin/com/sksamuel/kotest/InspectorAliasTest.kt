package com.sksamuel.kotest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.inspectors.shouldForAny
import io.kotest.inspectors.shouldForAtLeast
import io.kotest.inspectors.shouldForAtLeastOne
import io.kotest.inspectors.shouldForAtMost
import io.kotest.inspectors.shouldForAtMostOne
import io.kotest.inspectors.shouldForExactly
import io.kotest.inspectors.shouldForNone
import io.kotest.inspectors.shouldForOne
import io.kotest.inspectors.shouldForSome
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class InspectorAliasTest : FunSpec({

   val array = arrayOf(1, 2, 3)
   val list = listOf(1, 2, 3)
   val sequence = sequenceOf(1, 2, 3)

   context("forAll") {
      fun block(x: Int) = x shouldBeGreaterThan 0

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

      test("list") {
         list.shouldForAll(::block)
         shouldThrowAny {
            list.shouldForAll {
               it shouldBeLessThan 3
            }
         }
      }

      test("sequence") {
         sequence.shouldForAll(::block)
         shouldThrowAny {
            sequence.shouldForAll {
               it shouldBeLessThan 3
            }
         }
      }
   }

   context("forOne") {
      fun block(x: Int) = x shouldBe 2

      test("array") {
         array.shouldForOne(::block)
         shouldThrowAny {
            array.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("list") {
         list.shouldForOne(::block)
         shouldThrowAny {
            list.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence") {
         sequence.shouldForOne(::block)
         shouldThrowAny {
            sequence.shouldForOne {
               it shouldBeLessThan 1
            }
         }
      }
   }

   context("forExactly") {
      fun block(x: Int) = x shouldBeGreaterThan 1
      val n = 2

      test("array") {
         array.shouldForExactly(n, ::block)
         shouldThrowAny {
            array.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }

      test("list") {
         list.shouldForExactly(n, ::block)
         shouldThrowAny {
            list.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence") {
         sequence.shouldForExactly(n, ::block)
         shouldThrowAny {
            sequence.shouldForExactly(n) {
               it shouldBeLessThan 1
            }
         }
      }
   }

   context("forSome") {
      fun block(x: Int) = x shouldBeGreaterThan 2

      test("array") {
         array.shouldForSome(::block)
         shouldThrowAny {
            array.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }

      test("list") {
         list.shouldForSome(::block)
         shouldThrowAny {
            list.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence") {
         sequence.shouldForSome(::block)
         shouldThrowAny {
            sequence.shouldForSome {
               it shouldBeLessThan 1
            }
         }
      }
   }

   context("forAny") {
      fun block(x: Int) = x shouldBeGreaterThan 0

      test("array") {
         array.shouldForAny(::block)
         shouldThrowAny {
            array.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }

      test("list") {
         list.shouldForAny(::block)
         shouldThrowAny {
            list.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence") {
         sequence.shouldForAny(::block)
         shouldThrowAny {
            sequence.shouldForAny {
               it shouldBeLessThan 1
            }
         }
      }
   }

   context("forAtLeast") {
      fun block(x: Int) = x shouldBeGreaterThan 0
      val n = 3

      test("array") {
         array.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            array.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }

      test("list") {
         list.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            list.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }

      test("sequence") {
         sequence.shouldForAtLeast(n, ::block)
         shouldThrowAny {
            sequence.shouldForAtLeast(n) {
               it shouldBeLessThan 3
            }
         }
      }
   }

   context("forAtLeastOne") {
      fun block(x: Int) = x shouldBeGreaterThan 0

      test("array") {
         array.shouldForAtLeastOne(::block)
         shouldThrowAny {
            array.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("list") {
         list.shouldForAtLeastOne(::block)
         shouldThrowAny {
            list.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }

      test("sequence") {
         sequence.shouldForAtLeastOne(::block)
         shouldThrowAny {
            sequence.shouldForAtLeastOne {
               it shouldBeLessThan 1
            }
         }
      }
   }

   context("forAtMost") {
      fun block(x: Int) = x shouldBeGreaterThan 0

      test("array") {
         val arr = arrayOf(0, 0, 1)
         arr.shouldForAtMost(1, ::block)
         shouldThrowAny {
            arr.shouldForAtMost(1) {
               it shouldBeLessThan 3
            }
         }
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

      test("sequence") {
         sequence.shouldForAtMost(3, ::block)
         shouldThrowAny {
            sequence.shouldForAtMost(2) {
               it shouldBeLessThan 4
            }
         }
      }
   }

   context("forNone") {
      fun block(x: Int) = x shouldBeLessThan 1

      test("array") {
         array.shouldForNone(::block)
         shouldThrowAny {
            array.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }

      test("list") {
         list.shouldForNone(::block)
         shouldThrowAny {
            list.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }

      test("sequence") {
         sequence.shouldForNone(::block)
         shouldThrowAny {
            sequence.shouldForNone {
               it shouldBeLessThan 4
            }
         }
      }
   }

   context("forAtMostOne") {
      fun block(x: Int) = x shouldBe 1

      test("array") {
         array.shouldForAtMostOne(::block)
      }

      test("list") {
         list.shouldForAtMostOne(::block)
      }

      test("sequence") {
         sequence.shouldForAtMostOne(::block)
      }
   }
})
