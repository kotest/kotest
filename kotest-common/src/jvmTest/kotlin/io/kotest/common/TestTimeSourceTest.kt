package io.kotest.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

class TestTimeSourceTest : FunSpec({

   coroutineTestScope = true

   test("using virtual time with test dispatcher") {
      testTimeSource() shouldNotBe TimeSource.Monotonic
   }

   context("using real time on other dispatchers") {
      test("withContext") {
         testTimeSource() shouldNotBe TimeSource.Monotonic
         withContext(Dispatchers.IO) {
            testTimeSource() shouldBe TimeSource.Monotonic
         }
      }

      test("launch") {
         coroutineScope {
            testTimeSource() shouldNotBe TimeSource.Monotonic
            launch(Dispatchers.IO) {
               testTimeSource() shouldBe TimeSource.Monotonic
            }
         }
      }
   }
})
