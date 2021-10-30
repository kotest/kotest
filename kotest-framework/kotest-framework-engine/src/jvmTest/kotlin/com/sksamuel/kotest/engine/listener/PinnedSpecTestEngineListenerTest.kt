package com.sksamuel.kotest.engine.listener

import io.kotest.core.spec.style.WordSpec
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

         listener.specEnter(spec1::class)
         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specInstantiated(spec2)
         listener.specInstantiated(spec3)

         verify { runBlocking { mock.specInstantiated(spec1) } }
         verify(exactly = 0) { runBlocking { mock.specInstantiated(spec2) } }
         verify(exactly = 0) { runBlocking { mock.specInstantiated(spec3) } }
      }

      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specEnter(spec1::class)
         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)

         listener.specEnter(spec2::class)
         listener.specStarted(spec2::class)
         listener.specInstantiated(spec2)

         listener.specFinished(spec2::class, null)
         listener.specFinished(spec3::class, null)

         verifyOrder {
            runBlocking {
               mock.specEnter(spec1::class)
               mock.specStarted(spec1::class)
               mock.specInstantiated(spec1)
            }
         }

         verify(exactly = 0) {
            runBlocking {
               mock.specEnter(spec2::class)
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
            }
         }

         listener.specFinished(spec1::class, null)

         verifyOrder {
            runBlocking {
               mock.specFinished(spec1::class, null)
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
               mock.specFinished(spec2::class, null)
            }
         }
         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(spec3::class)
               mock.specInstantiated(spec3)
               mock.specFinished(spec3::class, null)
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
               listener.specEnter(spec1::class)
               listener.specStarted(spec1::class)
               listener.specInstantiated(spec1)
               listener.specFinished(spec1::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specEnter(spec2::class)
               listener.specStarted(spec2::class)
               listener.specInstantiated(spec2)
               listener.specFinished(spec2::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specEnter(spec3::class)
               listener.specStarted(spec3::class)
               listener.specInstantiated(spec3)
               listener.specFinished(spec3::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specEnter(spec4::class)
               listener.specStarted(spec4::class)
               listener.specInstantiated(spec4)
               listener.specFinished(spec4::class, null)

               listener.specEnter(spec5::class)
               listener.specStarted(spec5::class)
               listener.specInstantiated(spec5)
               listener.specFinished(spec5::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specEnter(spec6::class)
               listener.specStarted(spec6::class)
               listener.specInstantiated(spec6)
               listener.specFinished(spec6::class, null)
            }
         }

         // -- we verify each block separately because they will run in different orders,
         // but each spec should be ordered itself

         verifyOrder {
            runBlocking {
               mock.specEnter(spec1::class)
               mock.specStarted(spec1::class)
               mock.specInstantiated(spec1)
               mock.specFinished(spec1::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specEnter(spec2::class)
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
               mock.specFinished(spec2::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specEnter(spec3::class)
               mock.specStarted(spec3::class)
               mock.specInstantiated(spec3)
               mock.specFinished(spec3::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specEnter(spec4::class)
               mock.specStarted(spec4::class)
               mock.specInstantiated(spec4)
               mock.specFinished(spec4::class, null)

               mock.specEnter(spec5::class)
               mock.specStarted(spec5::class)
               mock.specInstantiated(spec5)
               mock.specFinished(spec5::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specEnter(spec6::class)
               mock.specStarted(spec6::class)
               mock.specInstantiated(spec6)
               mock.specFinished(spec6::class, null)
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
