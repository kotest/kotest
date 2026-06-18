package com.sksamuel.kotest.assertions

import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.config.TestConfig
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Suppress("DEPRECATION")
class AssertSoftlyTests : FunSpec({
   test("assertSoftly should collect errors across multiple coroutine threads").config(TestConfig(invocations = 10)) {
      val threadIds = mutableSetOf<Long>()
      shouldThrowExactly<MultiAssertionError> {
         assertSoftly {
            withContext(newSingleThreadContext("thread1")) {
               threadIds.add(Thread.currentThread().id)
               "assertSoftly block begins on ${Thread.currentThread().name}, id ${Thread.currentThread().id}" shouldBe "collected failure"
            }
            withContext(newSingleThreadContext("thread2")) {
               threadIds.add(Thread.currentThread().id)
               "assertSoftly block begins on ${Thread.currentThread().name}, id ${Thread.currentThread().id}" shouldBe "collected failure"
            }
         }
      }
      threadIds shouldHaveSize 2
   }
   test("adds an Exception to non-empty collection of assertion failures") {
      val thrown = shouldThrowExactly<MultiAssertionError> {
         assertSoftly {
            "first assertion" shouldBe "First Assertion"
            0 shouldBe 1
         }
      }
      thrown.message.shouldContainInOrder(
         """expected:<First Assertion> but was:<first assertion>""",
         """expected:<1> but was:<0>""",
      )
   }
   test("adds an Exception to an empty collection of assertion failures") {
      val thrown = shouldThrowExactly<MultiAssertionError> {
         assertSoftly {
            0 shouldBe 1
            "first assertion" shouldBe "First Assertion"
         }
      }
      thrown.message.shouldContainInOrder(
         """expected:<1> but was:<0>""",
         """expected:<First Assertion> but was:<first assertion>"""
      )
   }
})
