package io.kotest.engine

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalCoroutinesApi::class)
class DispatchersSharedCoroutineSchedulerTest : WordSpec({
   coroutineTestScope = true
   isolationMode = IsolationMode.InstancePerLeaf

   "outer test scope, delay 1_000 ms" should {
      val outerTestCoroutineScheduler = testCoroutineScheduler
      testCoroutineScheduler.currentTime shouldBe 0

      delay(1_000)

      testCoroutineScheduler.currentTime shouldBe 1_000

      "inner test scope, current time should be delayed by 1_000 ms" {
         testCoroutineScheduler.currentTime shouldBe 1_000
      }

      "inner test scope, test scheduler should be shared" {
         testCoroutineScheduler shouldBe outerTestCoroutineScheduler
      }
   }
})
