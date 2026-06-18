package io.kotest.raceConditions

import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ParallelRunner(
   private val timeoutInMs: Long,
   vararg tasks: (runner: ParallelRunner) -> Unit) {
   private val tasks = tasks.toList()
   private val barrier = CyclicBarrier(tasks.size)

   constructor(vararg tasks: (runner: ParallelRunner) -> Unit): this(1000L, *(tasks))

   fun run() {
      val threads = tasks.map { task ->
         thread(start = true) {
            task(this)
         }
      }
      threads.forEach { it.join() }
   }

   fun await() = barrier.await(timeoutInMs, TimeUnit.MILLISECONDS)

   companion object {
      fun runInParallel(vararg tasks: (runner: ParallelRunner) -> Unit) {
         ParallelRunner(*(tasks)).run()
      }
   }
}
