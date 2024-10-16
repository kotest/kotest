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

   "a" should {
      testCoroutineScheduler.currentTime shouldBe 0

      delay(1_000)

      testCoroutineScheduler.currentTime shouldBe 1_000

      "b" {
         testCoroutineScheduler.currentTime shouldBe 1_000
      }
   }
})
