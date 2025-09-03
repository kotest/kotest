package com.sksamuel.kotest.engine.extensions.spec.beforespec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class BeforeSpecConcurrentRaceConditionTest : FunSpec() {
   init {
      test("concurrent tests should wait for beforeSpec to complete before executing") {
         val events = ParallelTestsInSpec.events
         events.clear()
         ParallelTestsInSpec.beforeSpecCompleted.set(false)
         ParallelTestsInSpec.testRanBeforeSpecCompleted.set(false)

         val listener = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(listener)
            .withClasses(ParallelTestsInSpec::class)
            .launch()

         val eventList = events.toList()
         println("Event order:")
         eventList.forEach { println("  $it") }

         val beforeSpecEndIndex = eventList.indexOf("beforeSpec:end")
         val testEvents = eventList.filter { it.startsWith("test") }
         val testBeforeSpecEnd = testEvents.any { eventList.indexOf(it) < beforeSpecEndIndex }

         testBeforeSpecEnd shouldBe false
         ParallelTestsInSpec.testRanBeforeSpecCompleted.get() shouldBe false
      }
   }
}

@OptIn(ExperimentalKotest::class)
class ParallelTestsInSpec : StringSpec(), BeforeSpecListener {
   
   companion object {
      val events = ConcurrentLinkedQueue<String>()
      val beforeSpecCompleted = AtomicBoolean(false)
      val testRanBeforeSpecCompleted = AtomicBoolean(false)
   }

   override suspend fun beforeSpec(spec: Spec) {
      events.add("beforeSpec:start")
      println("> beforeSpec start")
      
      runBlocking {
         for (i in 1..2) {
            async {
               delay(1000)
               events.add("beforeSpec:task$i")
               println("  task $i done")
            }
         }
      }
      
      beforeSpecCompleted.set(true)
      events.add("beforeSpec:end")
      println("> beforeSpec end")
   }

   override suspend fun afterSpec(spec: Spec) {
      events.add("afterSpec")
      println("> afterSpec")
   }

   init {
      testExecutionMode = TestExecutionMode.Concurrent

      "test 1" {
         if (!beforeSpecCompleted.get()) {
            testRanBeforeSpecCompleted.set(true)
            events.add("test1:ranBeforeSpecCompleted")
         }
         events.add("test1")
         println("- test 1")
      }
      
      "test 2" {
         if (!beforeSpecCompleted.get()) {
            testRanBeforeSpecCompleted.set(true)
            events.add("test2:ranBeforeSpecCompleted") 
         }
         events.add("test2")
         println("- test 2")
      }
   }
}