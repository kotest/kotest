package com.sksamuel.kotest.runner.junit4

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.runner.junit4.KotestTestRunner
import kotlinx.coroutines.delay
import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import kotlin.random.Random

class KotestTestRunnerTest : FunSpec({
   test("should use same thread for all events") {
      val threads = mutableSetOf<String>()
      val listener = RunNotifier()
      listener.addListener(object : RunListener() {
         override fun testStarted(description: Description?) {
            threads.add(Thread.currentThread().id.toString())
         }

         override fun testFinished(description: Description?) {
            threads.add(Thread.currentThread().id.toString())
         }
      })
      KotestTestRunner(DummySpec::class.java).run(listener)
      threads.shouldHaveSize(1)
   }
})

private class DummySpec : FreeSpec() {
   init {
      testExecutionMode = TestExecutionMode.Concurrent
      repeat(100) { k ->
         "foo_$k" {
            delay(Random.nextLong(1, 15)) // force some bouncing around
         }
      }
   }
}
