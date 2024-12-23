package io.kotest.playback

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PlaybackElementsTest: StringSpec() {
   init {
      "play back elements" {
         val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
         fakeFunction.next() shouldBe "yes"
         fakeFunction.next() shouldBe "no"
         fakeFunction.next() shouldBe "maybe"
      }
      "play back elements, correctly plugged in" {
         val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
         val decisionEngine = DecisionEngine { fakeFunction.next() }
         decisionEngine.answer("what") shouldBe "yes"
         decisionEngine.answer("when") shouldBe "no"
         decisionEngine.answer("where") shouldBe "maybe"
      }
   }

   private fun interface HasAnswer<Q, T> {
      fun answer(question: Q): T
   }

   private class DecisionEngine(
      private val hasAnswers: HasAnswer<String, String>
   ) {
      fun answer(question: String): String {
         return hasAnswers.answer(question)
      }
   }
}
