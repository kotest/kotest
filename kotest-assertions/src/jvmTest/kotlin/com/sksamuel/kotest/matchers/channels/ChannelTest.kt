package com.sksamuel.kotest.matchers.channels

import io.kotest.assertions.shouldFail
import io.kotest.matchers.channels.*
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

class ChannelTest : StringSpec() {
  init {
    "shouldBeClosed should not fail on closed channels" {
      val channel: Channel<Int> = Channel()
      channel.close()
      channel.shouldBeClosed()
    }
    "shouldBeClosed should fail on open channels" {
      val channel: Channel<Int> = Channel()
      shouldFail {
        channel.shouldBeClosed()
      }
    }
    "shouldBeEmpty should not fail on empty channels" {
      val channel: Channel<Int> = Channel()
      channel.shouldBeEmpty()
    }
    "shouldBeEmpty should not fail on channels that have received all elements" {
      val channel: Channel<Int> = Channel()
      launch {
        channel.send(1)
      }
      channel.receive() shouldBeExactly 1
      channel.shouldBeEmpty()
    }
     "shouldBeEmpty should fail on non-empty channels" {
        val channel: Channel<Int> = Channel(1)
        channel.send(1)
        shouldFail {
           channel.shouldBeEmpty()
        }
        channel.receive() shouldBe 1
     }
     "shouldReceiveWithin should not fail on non-empty channels" {
        val channel: Channel<Int> = Channel(1)
        channel.send(1)
        channel.shouldReceiveWithin(Duration.ofSeconds(1))
     }
    "shouldReceiveWithin should not fail when elements are received later" {
      val channel: Channel<Int> = Channel()
      launch {
        delay(1000)
        channel.send(1)
      }
      channel.shouldReceiveWithin(Duration.ofSeconds(3))
    }
    "shouldReceiveWithin should fail when no elements are sent" {
      val channel: Channel<Int> = Channel()
      shouldFail {
        channel.shouldReceiveWithin(Duration.ofSeconds(1))
      }
    }
    "shouldReceiveNoElementsWithin should not fail when no elements are sent" {
      val channel: Channel<Int> = Channel()
      channel.shouldReceiveNoElementsWithin(Duration.ofSeconds(1))
    }
    "shouldReceiveNoElementsWithin should fail when elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        channel.send(1)
      }
      shouldFail {
        channel.shouldReceiveNoElementsWithin(Duration.ofSeconds(1))
      }
    }
    "!shouldHaveSize should not fail when n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(10) { channel.send(1) }
        channel.close()
      }
      channel.shouldHaveSize(10)
    }
    "shouldHaveSize should fail when greater than n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(12) { channel.send(1) }
        channel.close()
      }
      shouldFail {
        channel.shouldHaveSize(10)
      }
      repeat(2) { channel.receive() }
    }
    "shouldReceiveAtLeast should not fail when n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(10) { channel.send(1) }
        channel.close()
      }
      channel.shouldReceiveAtLeast(10)
    }
    "shouldReceiveAtLeast should not fail when greater than n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(12) { channel.send(1) }
        channel.close()
      }
      channel.shouldReceiveAtLeast(10)
      repeat(2) { channel.receive() }
    }
    "shouldReceiveAtMost should not fail when n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(10) { channel.send(1) }
        channel.close()
      }
      channel.shouldReceiveAtMost(10)
    }
    "shouldReceiveAtMost should not fail when less than n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(9) { channel.send(1) }
        channel.close()
      }
      channel.shouldReceiveAtMost(10)
    }
    "shouldReceiveAtMost should fail when more than n elements are sent" {
      val channel: Channel<Int> = Channel()
      launch {
        repeat(11) { channel.send(1) }
        channel.close()
      }
      shouldFail {
        channel.shouldReceiveAtMost(10)
      }
      channel.receive()
    }
  }
}
