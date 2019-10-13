package io.kotest.matchers.channels

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration

/**
 * Asserts that this [Channel] is closed
 *
 * Opposite of [Channel.shouldBeOpen]
 *
 */
fun <T> Channel<T>.shouldBeClosed() = this should beClosed()

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
fun <T> Channel<T>.shouldBeOpen() = this shouldNot beClosed()

/**
 * Asserts that this [Channel] is empty
 *
 */
fun <T> Channel<T>.shouldBeEmpty() = this should beEmpty()

fun <T> beEmpty() = object : Matcher<Channel<T>> {
  override fun test(value: Channel<T>) = MatcherResult(
    value.isEmpty,
    { "Channel should be empty" },
    { "Channel should not be empty" }
  )
}

/**
 * Asserts that this [Channel] should receive within [duration]
 *
 */
fun <T> Channel<T>.shouldReceiveWithin(duration: Duration) = this should receiveWithin(duration)

fun <T> receiveWithin(duration: Duration) = object : Matcher<Channel<T>> {
  override fun test(value: Channel<T>) = MatcherResult(
    runBlocking {
      withTimeoutOrNull(duration.toMillis()) {
        value.receive()
      } != null
    },
    { "Channel should receive within ${duration.toMillis()}ms" },
    { "Channel should not receive within ${duration.toMillis()}ms" }
  )
}

/**
 * Asserts that this [Channel] should not receive within [duration]
 *
 */
fun <T> Channel<T>.shouldReceiveNoElementsWithin(duration: Duration) = this shouldNot receiveWithin(duration)


/**
 * Asserts that this [Channel] should receive [n] elements, then close
 *
 */
fun <T> Channel<T>.shouldHaveSize(n: Int) = runBlocking {
  repeat(n) { this@shouldHaveSize.receive() }
  this@shouldHaveSize.shouldBeClosed()
}

/**
 * Asserts that this [Channel] should receive at least [n] elements
 *
 */
fun <T> Channel<T>.shouldReceiveAtLeast(n: Int) = runBlocking {
  repeat(n) { this@shouldReceiveAtLeast.receive() }
}

/**
 * Asserts that this [Channel] should receive at most [n] elements, then close
 *
 */
fun <T> Channel<T>.shouldReceiveAtMost(n: Int) = runBlocking {
  var count = 0
  for (value in this@shouldReceiveAtMost) {
    count++
    if(count>n) this@shouldReceiveAtMost.shouldBeClosed()
  }
}