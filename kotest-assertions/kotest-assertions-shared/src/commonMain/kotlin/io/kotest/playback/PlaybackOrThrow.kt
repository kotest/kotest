package io.kotest.playback

class PlaybackResults<T>(
   source: Sequence<Result<T>>
) {
   private val iterator = source.iterator()

   fun next(): T = iterator.next().getOrThrow()
}

/**
 * Provides a fake function that returns elements or throw exceptions from a sequence.
 *
 * Example:
 *
 *          val fakeFunction = sequenceOf(
 *             Result.success("yes"),
 *             Result.failure(RuntimeException("bad request")),
 *             Result.success("no")
 *          ).toFunction()
 *          fakeFunction.next() shouldBe "yes"
 *          shouldThrow<RuntimeException> { fakeFunction.next() }
 *          fakeFunction.next() shouldBe "no"
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
 *       "play back elements or throw exceptions, correctly plugged in" {
 *          val fakeFunction = sequenceOf(
 *             Result.success("yes"),
 *             Result.failure(RuntimeException("bad request")),
 *             Result.success("no")
 *          ).toFunction()
 *          val decisionEngine = DecisionEngine { fakeFunction.next() }
 *          decisionEngine.answer("what") shouldBe "yes"
 *          shouldThrow<RuntimeException> { decisionEngine.answer("when") }
 *          decisionEngine.answer("where") shouldBe "no"
 *       }
 *
 */

fun<T> Sequence<Result<T>>.toFunction(): PlaybackResults<T> = PlaybackResults(this)
