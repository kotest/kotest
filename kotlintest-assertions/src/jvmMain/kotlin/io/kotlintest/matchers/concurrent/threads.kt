package io.kotlintest.matchers.concurrent

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

fun Thread.shouldBeBlocked() = this should beBlocked()
fun Thread.shouldNotBeBlocked() = this shouldNot beBlocked()
fun beBlocked() = object : Matcher<Thread> {
  override fun test(value: Thread) = Result(
      value.state == Thread.State.BLOCKED,
      "Thread ${value.id} should be blocked",
      "Thread ${value.id} should not be blocked"
  )
}

fun Thread.shouldBeTerminated() = this should beTerminated()
fun Thread.shouldNotBeTerminated() = this shouldNot beTerminated()
fun beTerminated() = object : Matcher<Thread> {
  override fun test(value: Thread) = Result(
      value.state == Thread.State.TERMINATED,
      "Thread ${value.id} should be terminated",
      "Thread ${value.id} should not be terminated"
  )
}

fun Thread.shouldBeAlive() = this should beAlive()
fun Thread.shouldNotBeAlive() = this shouldNot beAlive()
fun beAlive() = object : Matcher<Thread> {
  override fun test(value: Thread) = Result(
      value.isAlive,
      "Thread ${value.id} should be alive",
      "Thread ${value.id} should not be alive"
  )
}

fun Thread.shouldBeDaemon() = this should beDaemon()
fun Thread.shouldNotBeDaemon() = this shouldNot beDaemon()
fun beDaemon() = object : Matcher<Thread> {
  override fun test(value: Thread) = Result(
      value.isDaemon,
      "Thread ${value.id} should be a daemon thread",
      "Thread ${value.id} should not be a daemon thread"
  )
}