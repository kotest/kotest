package com.sksamuel.kotest

import io.kotest.assertions.*
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(ExperimentalKotest::class)
class AnyTests : FunSpec({
   test("any succeeds as long as a single assertion succeeds") {
      any {
         1 shouldBe 2
         10 shouldBe 10
      }
   }

   test("any fails if all assertions fail") {
      val message = shouldFail {
         any {
            1 shouldBe 2
            2 shouldBe 3
         }
      }.message

      message shouldContain "The following 3 assertions failed"
      message shouldContain "expected:<2> but was:<1>"
      message shouldContain "expected:<3> but was:<2>"
      message shouldContain "Any expected at least one assertion to succeed but they all failed"
   }

   test("any should not destroy collected errors from previous assertions") {
      val expectedMessage = """expected:<"bar"> but was:<"foo">"""

      val beforeCount = errorCollector.errors().size
      var before: Throwable? = null

      var afterCount: Int? = null
      var after: Throwable? = null

      shouldFail {
         all {
            "foo" shouldBe "bar"
            before = errorCollector.errors().lastOrNull()

            any("foo") {
               this shouldBe "bar"
               this shouldBe "foo"
            }

            after = errorCollector.errors().lastOrNull()
            afterCount = errorCollector.errors().size
         }
      }.message shouldBe expectedMessage

      withClue("the last error in the collector before any should be the same as after") {
         before.shouldNotBeNull()
         before?.message shouldBe expectedMessage

         after.shouldNotBeNull()
         after?.message shouldBe expectedMessage

         before shouldBeSameInstanceAs after
      }

      withClue("the number of errors before the assertSoftly block with any should be one less than after") {
         beforeCount shouldBe afterCount?.minus(1)
      }
   }
})
