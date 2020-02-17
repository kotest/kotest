package io.kotest.matchers.concurrent

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun Thread.shouldBeBlocked() = this should beBlocked()
fun Thread.shouldNotBeBlocked() = this shouldNot beBlocked()
fun beBlocked() = object : Matcher<Thread> {
  override fun test(value: Thread) = MatcherResult(
      value.state == Thread.State.BLOCKED,
      "Thread ${value.id} should be blocked",
      "Thread ${value.id} should not be blocked"
  )
}

fun Thread.shouldBeTerminated() = this should beTerminated()
fun Thread.shouldNotBeTerminated() = this shouldNot beTerminated()
fun beTerminated() = object : Matcher<Thread> {
  override fun test(value: Thread) = MatcherResult(
      value.state == Thread.State.TERMINATED,
      "Thread ${value.id} should be terminated",
      "Thread ${value.id} should not be terminated"
  )
}

fun Thread.shouldBeAlive() = this should beAlive()
fun Thread.shouldNotBeAlive() = this shouldNot beAlive()
fun beAlive() = object : Matcher<Thread> {
  override fun test(value: Thread) = MatcherResult(
      value.isAlive,
      "Thread ${value.id} should be alive",
      "Thread ${value.id} should not be alive"
  )
}

fun Thread.shouldBeDaemon() = this should beDaemon()
fun Thread.shouldNotBeDaemon() = this shouldNot beDaemon()
fun beDaemon() = object : Matcher<Thread> {
  override fun test(value: Thread) = MatcherResult(
      value.isDaemon,
      "Thread ${value.id} should be a daemon thread",
      "Thread ${value.id} should not be a daemon thread"
  )
}
