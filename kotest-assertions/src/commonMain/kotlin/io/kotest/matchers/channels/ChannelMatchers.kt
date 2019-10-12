package io.kotest.matchers.channels

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import kotlinx.coroutines.channels.Channel

fun <T> beClosed() = object : Matcher<Channel<T>> {
  override fun test(value: Channel<T>) = MatcherResult(
    value.isClosedForSend && value.isClosedForReceive,
    { "Channel should be closed" },
    { "Channel should not be closed" }
  )
}

/**
 * Asserts that this [Channel] is closed
 *
 * Opposite of [Channel.shouldBeOpen]
 *
 */
fun <T> Channel<T>.shouldBeClosed() = this should beClosed()

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

