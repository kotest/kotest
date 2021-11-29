package com.sksamuel.kotest.engine.listener

import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PinnedSpecTestEngineListenerTest : WordSpec({

   "PinnedSpecTestEngineListener" should {
      "only notify for the running test" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.executionStarted(Node.Spec(spec1::class))
         listener.executionStarted(Node.Spec(spec2::class))
         listener.executionStarted(Node.Spec(spec3::class))

         verify { runBlocking { mock.executionStarted(Node.Spec(spec1::class)) } }
         verify(exactly = 0) { runBlocking { mock.executionStarted(Node.Spec(spec2::class)) } }
         verify(exactly = 0) { runBlocking { mock.executionStarted(Node.Spec(spec3::class)) } }
      }

      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.executionStarted(Node.Spec(spec1::class))
         listener.executionStarted(Node.Spec(spec2::class))
         listener.executionFinished(Node.Spec(spec2::class), TestResult.success)
         listener.executionFinished(Node.Spec(spec3::class), TestResult.success)

         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec1::class))
            }
         }

         verify(exactly = 0) {
            runBlocking {
               mock.executionStarted(Node.Spec(spec2::class))
            }
         }

         listener.executionFinished(Node.Spec(spec1::class), TestResult.success)

         verifyOrder {
            runBlocking {
               mock.executionFinished(Node.Spec(spec1::class), TestResult.success)
               mock.executionStarted(Node.Spec(spec2::class))
               mock.executionFinished(Node.Spec(spec2::class), TestResult.success)
            }
         }
         verify(exactly = 0) {
            runBlocking {
               mock.executionStarted(Node.Spec(spec3::class))
               mock.executionFinished(Node.Spec(spec3::class), TestResult.success)
            }
         }
      }

      "run all callbacks without race conditions" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(mock))

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()
         val spec4 = IsolationTestSpec4()
         val spec5 = IsolationTestSpec5()
         val spec6 = IsolationTestSpec6()

         coroutineScope {
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.executionStarted(Node.Spec(spec1::class))
               listener.executionFinished(Node.Spec(spec1::class), TestResult.success)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.executionStarted(Node.Spec(spec2::class))
               listener.executionFinished(Node.Spec(spec2::class), TestResult.success)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.executionStarted(Node.Spec(spec3::class))
               listener.executionFinished(Node.Spec(spec3::class), TestResult.success)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.executionStarted(Node.Spec(spec4::class))
               listener.executionFinished(Node.Spec(spec4::class), TestResult.success)

               listener.executionStarted(Node.Spec(spec5::class))
               listener.executionFinished(Node.Spec(spec5::class), TestResult.success)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.executionStarted(Node.Spec(spec6::class))
               listener.executionFinished(Node.Spec(spec6::class), TestResult.success)
            }
         }

         // -- we verify each block separately because they will run in different orders,
         // but each spec should be ordered itself

         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec1::class))
               mock.executionFinished(Node.Spec(spec1::class), TestResult.success)
            }
         }
         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec2::class))
               mock.executionFinished(Node.Spec(spec2::class), TestResult.success)
            }
         }
         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec3::class))
               mock.executionFinished(Node.Spec(spec3::class), TestResult.success)
            }
         }
         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec4::class))
               mock.executionFinished(Node.Spec(spec4::class), TestResult.success)

               mock.executionStarted(Node.Spec(spec5::class))
               mock.executionFinished(Node.Spec(spec5::class), TestResult.success)
            }
         }
         verifyOrder {
            runBlocking {
               mock.executionStarted(Node.Spec(spec6::class))
               mock.executionFinished(Node.Spec(spec6::class), TestResult.success)
            }
         }
      }
   }
})

class IsolationTestSpec1 : WordSpec()
class IsolationTestSpec2 : WordSpec()
class IsolationTestSpec3 : WordSpec()
class IsolationTestSpec4 : WordSpec()
class IsolationTestSpec5 : WordSpec()
class IsolationTestSpec6 : WordSpec()
