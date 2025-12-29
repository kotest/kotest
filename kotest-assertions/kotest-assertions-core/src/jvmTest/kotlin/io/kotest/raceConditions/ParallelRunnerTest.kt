package io.kotest.raceConditions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.raceConditions.ParallelRunner.Companion.runInParallel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.time.Clock
import java.time.LocalDateTime

@EnabledIf(LinuxOnlyGithubCondition::class)
class ParallelRunnerTest: StringSpec() {
   init {
      /*
      A typical race condition - two tasks mutate shared state without synchronization
       */
      "two tasks share one mutable state, both make the same decision at the same time" {
         val box = Box(maxCapacity = 2)
         box.addItem("apple")
         runInParallel({ runner: ParallelRunner ->
            val hasCapacity = box.hasCapacity()
            runner.await()
            if(hasCapacity) {
               box.addItem("banana")
            }
         },
            { runner: ParallelRunner ->
               val hasCapacity = box.hasCapacity()
               runner.await()
               if(hasCapacity) {
                  box.addItem("orange")
               }
            }
         )
         // capacity is exceeded as a result of race condition
         box.items() shouldContainExactlyInAnyOrder listOf("apple", "banana", "orange")
      }

      "demo for mockkStatic".config(enabled = true) {
         runInParallel({ runner: ParallelRunner ->
            timedPrint("Before mock on same thread: ${LocalDateTime.now()}")
            runner.await()
            mockkStatic(LocalDateTime::class)
            val localTime = LocalDateTime.of(2022, 4, 27, 12, 34, 56)
            every { LocalDateTime.now(any<Clock>()) } returns localTime
            runner.await()
            timedPrint("After mock on same thread: ${LocalDateTime.now()}")
         },
            { runner: ParallelRunner ->
               timedPrint("Before mock on other thread: ${LocalDateTime.now()}")
               runner.await()
               runner.await()
               timedPrint("After mock on other thread: ${LocalDateTime.now()}")
            }
         )
         /*
Time: 2023-05-12T13:14:07.815923, Thread: 51, Before mock on other thread: 2023-05-12T13:14:07.737748
Time: 2023-05-12T13:14:07.816011, Thread: 50, Before mock on same thread: 2023-05-12T13:14:07.737736
Time: 2022-04-27T12:34:56, Thread: 51, After mock on other thread: 2022-04-27T12:34:56
Time: 2022-04-27T12:34:56, Thread: 50, After mock on same thread: 2022-04-27T12:34:56
          */
      }

      "demo for mockkStatic - can remock".config(enabled = true) {
         runInParallel({ runner: ParallelRunner ->
            mockkStatic(LocalDateTime::class)
            val localTime = LocalDateTime.of(2022, 4, 27, 12, 34, 56)
            every { LocalDateTime.now(any<Clock>()) } returns localTime
            timedPrint("First thread - after mocking ${LocalDateTime.now()}")
            clearAllMocks()
            runner.await()
            timedPrint("First thread - after clearing mock ${LocalDateTime.now()}")
         },
            { runner: ParallelRunner ->
               runner.await()
               mockkStatic(LocalDateTime::class)
               val localTime = LocalDateTime.of(2023, 1, 2, 3, 4, 5)
               every { LocalDateTime.now(any<Clock>()) } returns localTime
               timedPrint("Second thread - after mocking ${LocalDateTime.now()}")
            }
         )
         /*
Time: 2022-04-27T12:34:56, Thread: 50, First thread - after mocking 2022-04-27T12:34:56
Time: 2023-01-02T03:04:05, Thread: 50, First thread - after clearing mock 2023-05-12T13:30:06.908020
Time: 2023-01-02T03:04:05, Thread: 51, Second thread - after mocking 2023-01-02T03:04:05
          */
      }

   }

   private fun timedPrint(message: String) =
      println("Time: ${LocalDateTime.now()}, Thread: ${Thread.currentThread()}, $message")

   private data class Box(val maxCapacity: Int) {
      private val items = mutableListOf<String>()

      fun addItem(item: String) = items.add(item)

      fun hasCapacity() = items.size < maxCapacity

      fun items() = items.toList()
   }
}
