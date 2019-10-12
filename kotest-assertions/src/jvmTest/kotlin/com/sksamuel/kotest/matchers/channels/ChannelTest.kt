package com.sksamuel.kotest.matchers.channels

import io.kotest.assertions.shouldFail
import io.kotest.matchers.channels.shouldBeClosed
import io.kotest.matchers.channels.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.specs.StringSpec
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

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
      val channel: Channel<Int> = Channel()
      launch {
        channel.send(1)
      }
      shouldFail {
        channel.shouldBeEmpty()
      }
      channel.receive() shouldBeExactly 1
    }
  }
}
