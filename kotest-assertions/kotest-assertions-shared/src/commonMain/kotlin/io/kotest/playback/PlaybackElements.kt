package io.kotest.playback


class PlaybackElements<T>(
   source: Sequence<T>
) {
   private val iterator = source.iterator()

   fun next(): T = iterator.next()
}

/**
 * Provides a fake function that returns elements from a sequence.
 *
 * Example:
 *
 *  val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
 *  fakeFunction.next() shouldBe "yes"
 *  fakeFunction.next() shouldBe "no"
 *  fakeFunction.next() shouldBe "maybe"
 *
 * It should be used in tests to replace a real dependency with a fake one, like this:
 *
 *    private fun interface HasAnswer<Q, T> {
 *       fun answer(question: Q): T
 *    }
 *
 *    private class DecisionEngine(
 *       private val hasAnswers: HasAnswer<String, String>
 *    ) {
 *       fun answer(question: String): String {
 *          return hasAnswers.answer(question)
 *       }
 *    }
 *
 *       "play back elements, correctly plugged in" {
 *          val fakeFunction = sequenceOf("yes", "no", "maybe").toFunction()
 *          val decisionEngine = DecisionEngine { fakeFunction.next() }
 *          decisionEngine.answer("what") shouldBe "yes"
 *          decisionEngine.answer("when") shouldBe "no"
 *          decisionEngine.answer("where") shouldBe "maybe"
 *       }
 *
 */
fun<T> Sequence<T>.toFunction(): PlaybackElements<T> = PlaybackElements(this)
