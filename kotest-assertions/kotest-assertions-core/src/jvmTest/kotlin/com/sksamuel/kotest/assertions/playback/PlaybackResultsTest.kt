package com.sksamuel.kotest.assertions.playback

import io.kotest.assertions.playback.toFunction
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PlaybackResultsTest: StringSpec() {
   init {
      "return or throw" {
         val fakeFunction = sequenceOf(
            Result.success("yes"),
            Result.failure(RuntimeException("bad request")),
            Result.success("no")
         ).toFunction()
         fakeFunction.next() shouldBe "yes"
         shouldThrow<RuntimeException> { fakeFunction.next() }
         fakeFunction.next() shouldBe "no"
      }

      "plug in correctly" {
         val fakeFunction = sequenceOf(
            Result.success("yes"),
            Result.failure(RuntimeException("bad request")),
            Result.success("no")
         ).toFunction()
         val decisionEngine = DecisionEngine { fakeFunction.next() }
         decisionEngine.answer("what") shouldBe "yes"
         shouldThrow<RuntimeException> { decisionEngine.answer("when") }
         decisionEngine.answer("where") shouldBe "no"
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
