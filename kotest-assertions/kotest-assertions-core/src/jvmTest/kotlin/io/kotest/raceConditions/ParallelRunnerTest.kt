package io.kotest.raceConditions

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.raceConditions.ParallelRunner.Companion.runInParallel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.time.Clock
import java.time.LocalDateTime

@EnabledIf(LinuxOnlyGithubCondition::class)
class ParallelRunnerTest: StringSpec() {
   init {
      "two tasks wait on each other" {
         runInParallel({ runner: ParallelRunner ->
            timedPrint("a1")
            runner.await()
            timedPrint("a2")
         },
            { runner: ParallelRunner ->
               timedPrint("b1")
               runner.await()
               timedPrint("b2")
            }
         )
      }

      "two tasks wait on each other, twice" {
         runInParallel({ runner: ParallelRunner ->
            timedPrint("-a1")
            runner.await()
            timedPrint("-a2")
            runner.await()
            timedPrint("-a3")
         },
            { runner: ParallelRunner ->
               timedPrint("-b1")
               runner.await()
               runner.await()
               timedPrint("-b2")
            }
         )
      }

      "one thread blows up, another times out" {
         runInParallel({ runner: ParallelRunner ->
            runner.await()
            timedPrint("first task")
            runner.await()
         },
            { runner: ParallelRunner ->
               runner.await()
               timedPrint("second task")
               throw RuntimeException("Oops")
            }
         )
         timedPrint("All done")
      }

      "demo for mockkStatic".config(enabled = true) {
         runInParallel({ runner: ParallelRunner ->
            timedPrint("Before mock on same thread: ${LocalDateTime.now().toString()}")
            runner.await()
            mockkStatic(LocalDateTime::class)
            val localTime = LocalDateTime.of(2022, 4, 27, 12, 34, 56)
            every { LocalDateTime.now(any<Clock>()) } returns localTime
            runner.await()
            timedPrint("After mock on same thread: ${LocalDateTime.now().toString()}")
         },
            { runner: ParallelRunner ->
               timedPrint("Before mock on other thread: ${LocalDateTime.now().toString()}")
               runner.await()
               runner.await()
               timedPrint("After mock on other thread: ${LocalDateTime.now().toString()}")
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
            timedPrint("First thread - after mocking ${LocalDateTime.now().toString()}")
            clearAllMocks()
            runner.await()
            timedPrint("First thread - after clearing mock ${LocalDateTime.now().toString()}")
         },
            { runner: ParallelRunner ->
               runner.await()
               mockkStatic(LocalDateTime::class)
               val localTime = LocalDateTime.of(2023, 1, 2, 3, 4, 5)
               every { LocalDateTime.now(any<Clock>()) } returns localTime
               timedPrint("Second thread - after mocking ${LocalDateTime.now().toString()}")
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
}
