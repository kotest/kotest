package io.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.beEven
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.native.concurrent.TransferMode.SAFE
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.test.Test


class NativeThreadingTest {
   @Test
   fun testShouldBe() = threadedTest {
      1 shouldBe 1
   }

   @Test
   fun testShouldWithMatcher() = threadedTest {
      2 should beEven()
   }

   @Test
   fun testForAll() = threadedTest {
      forAll(
         row(4, 5),
         row(3, 6)
      ) { a, b ->
         a + b shouldBe 9
      }
   }

   @Test
   fun testSoftAssert() = threadedTest {
      assertSoftly {
         "a" shouldBe "a"
         "b" shouldBe "b"
      }
   }

   @Test
   fun testShouldThrow() = threadedTest {
      shouldThrow<IllegalArgumentException> {
         require(false)
      }
   }

   @Test
   fun testStackTraces() = threadedTest {
      shouldThrow<AssertionError> {
         forAll(
            row(4, 5),
            row(3, 6)
         ) { a, b ->
            a + b shouldBe 0
         }
      }
   }

   // https://jakewharton.com/litmus-testing-kotlins-many-memory-models/
   private fun threadedTest(body: () -> Unit) {
      // Run once on the main thread
      body()

      // Run again on a background thread
      body.freeze()
      val worker = Worker.start()
      val future = worker.execute(SAFE, { body }) {
         runCatching(it)
      }
      future.result.getOrThrow()
   }
}
