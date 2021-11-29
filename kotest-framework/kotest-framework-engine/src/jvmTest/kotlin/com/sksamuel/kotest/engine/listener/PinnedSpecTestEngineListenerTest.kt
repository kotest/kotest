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

         listener.specStarted(spec1::class)
         listener.specStarted(spec2::class)
         listener.specStarted(spec3::class)

         verify { runBlocking { mock.specStarted(spec1::class) } }
         verify(exactly = 0) { runBlocking { mock.specStarted(spec2::class) } }
         verify(exactly = 0) { runBlocking { mock.specStarted(spec3::class) } }
      }

      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specStarted(spec2::class)
         listener.specFinished(spec2::class, null)
         listener.specFinished(spec3::class, null)

         verifyOrder {
            runBlocking {
               mock.specStarted(spec1::class)
            }
         }

         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(spec2::class)
            }
         }

         listener.specFinished(spec1::class, null)

         verifyOrder {
            runBlocking {
               mock.specFinished(spec1::class, null)
               mock.specStarted(spec2::class)
               mock.specFinished(spec2::class, null)
            }
         }
         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(spec3::class)
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
               listener.specStarted(spec1::class)
               listener.specFinished(spec1::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(spec2::class)
               listener.specFinished(spec2::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(spec3::class)
               listener.specFinished(spec3::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(spec4::class)
               listener.specFinished(spec4::class, null)

               listener.specStarted(spec5::class)
               listener.specFinished(spec5::class, null)
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(spec6::class)
               listener.specFinished(spec6::class, null)
            }
         }

         // -- we verify each block separately because they will run in different orders,
         // but each spec should be ordered itself

         verifyOrder {
            runBlocking {
               mock.specStarted(spec1::class)
               mock.specFinished(spec1::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec2::class)
               mock.specFinished(spec2::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec3::class)
               mock.specFinished(spec3::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec4::class)
               mock.specFinished(spec4::class, null)

               mock.specStarted(spec5::class)
               mock.specFinished(spec5::class, null)
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec6::class)
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
