package io.kotest.matchers.channels

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

/**
 * Asserts that this [Channel] is closed
 *
 * Opposite of [Channel.shouldBeOpen]
 *
 */
@ExperimentalCoroutinesApi
fun <T> Channel<T>.shouldBeClosed() = this should beClosed()

@ExperimentalCoroutinesApi
fun <T> beClosed() = object : Matcher<Channel<T>> {
   override fun test(value: Channel<T>) = MatcherResult(
      value.isClosedForSend && value.isClosedForReceive,
      { "Channel should be closed" },
      { "Channel should not be closed" }
   )
}

/**
 * Asserts that this [Channel] is open
 *
 * Opposite of [Channel.shouldBeClosed]
 *
 */
@ExperimentalCoroutinesApi
fun <T> Channel<T>.shouldBeOpen() = this shouldNot beClosed()

/**
 * Asserts that this [Channel] is empty
 *
 */
@ExperimentalCoroutinesApi
fun <T> Channel<T>.shouldBeEmpty() = this should beEmpty()

@ExperimentalCoroutinesApi
fun <T> beEmpty() = object : Matcher<Channel<T>> {
   override fun test(value: Channel<T>) = MatcherResult(
      value.isEmpty,
      { "Channel should be empty" },
      { "Channel should not be empty" }
   )
}

/**
 * Asserts that this [Channel] should receive [n] elements, then is closed.
 *
 */
@ExperimentalCoroutinesApi
suspend fun <T> Channel<T>.shouldHaveSize(n: Int) {
   repeat(n) {
      this@shouldHaveSize.receive()
   }
   this@shouldHaveSize.shouldBeClosed()
}

/**
 * Asserts that this [Channel] should receive at least [n] elements
 *
 */
suspend fun <T> Channel<T>.shouldReceiveAtLeast(n: Int) {
   repeat(n) { this@shouldReceiveAtLeast.receive() }
}

/**
 * Asserts that this [Channel] should receive at most [n] elements, then close
 *
 */
@ExperimentalCoroutinesApi
suspend fun <T> Channel<T>.shouldReceiveAtMost(n: Int) {
   var count = 0
   for (value in this@shouldReceiveAtMost) {
      count++
      if (count == n) break
   }
   delay(100)
   this@shouldReceiveAtMost.shouldBeClosed()
}
