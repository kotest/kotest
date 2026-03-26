package io.kotest.datatest

import io.kotest.core.annotation.Issue
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class NameTest : FreeSpec() {
   init {
      "data tests should not strip trailing empty brackets" {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withSpecRefs(SpecRef.Reference(Issue5367::class))
            .execute()
         // if the package config isn't picked up, this test would not timeout
         collector.names shouldBe listOf("result should be ()", "result should be (1)")
         collector.result("result should be ()").shouldNotBeNull().isSuccess shouldBe true
         collector.result("result should be (1)").shouldNotBeNull().isSuccess shouldBe true
      }
   }

}

@Issue("https://github.com/kotest/kotest/issues/5367")
private class Issue5367 : ShouldSpec({
   class Data(val expected: IntArray) : io.kotest.engine.names.WithDataTestName {
      override fun dataTestName() =
         "result should be (${expected.joinToString()})"
   }
   withShoulds(sequence {
      yield(Data(intArrayOf()))
      yield(Data(intArrayOf(1)))
   }) {
   }
})
