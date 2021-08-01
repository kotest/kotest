package com.sksamuel.kotest.engine.internal

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

   "IsolationTestEngineListener" should {
      "only notify for the running test" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(mock))

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

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
         val listener = ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(mock))

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specStarted(spec2::class)
         listener.specInstantiated(spec2)
         listener.specFinished(spec2::class, null, emptyMap())
         listener.specFinished(spec3::class, null, emptyMap())

         verifyOrder {
            runBlocking {
               mock.specStarted(spec1::class)
               mock.specInstantiated(spec1)
            }
         }

         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
            }
         }

         listener.specFinished(spec1::class, null, emptyMap())

         verifyOrder {
            runBlocking {
               mock.specFinished(spec1::class, any(), any())
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
               mock.specFinished(spec2::class, any(), any())
            }
         }
         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(spec3::class)
               mock.specInstantiated(spec3)
               mock.specFinished(spec3::class, any(), any())
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
               delay(100)
               listener.specStarted(spec1::class)
               listener.specInstantiated(spec1)
               listener.specFinished(spec1::class, null, emptyMap())
            }
            launch(Dispatchers.IO) {
               listener.specStarted(spec2::class)
               listener.specInstantiated(spec2)
               listener.specFinished(spec2::class, null, emptyMap())
            }
            launch(Dispatchers.IO) {
               delay(50)
               listener.specStarted(spec3::class)
               listener.specInstantiated(spec3)
               listener.specFinished(spec3::class, null, emptyMap())
            }
            launch(Dispatchers.IO) {
               delay(25)
               listener.specStarted(spec4::class)
               listener.specInstantiated(spec4)
               listener.specFinished(spec4::class, null, emptyMap())
               listener.specStarted(spec5::class)
               listener.specInstantiated(spec5)
               listener.specFinished(spec5::class, null, emptyMap())
            }
            launch(Dispatchers.IO) {
               delay(75)
               listener.specStarted(spec6::class)
               listener.specInstantiated(spec6)
               listener.specFinished(spec6::class, null, emptyMap())
            }
         }

         verifyOrder {
            runBlocking {
               mock.specStarted(spec1::class)
               mock.specInstantiated(spec1)
               mock.specFinished(spec1::class, any(), any())
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec2::class)
               mock.specInstantiated(spec2)
               mock.specFinished(spec2::class, any(), any())
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec3::class)
               mock.specInstantiated(spec3)
               mock.specFinished(spec3::class, any(), any())
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec4::class)
               mock.specInstantiated(spec4)
               mock.specFinished(spec4::class, any(), any())
               mock.specStarted(spec5::class)
               mock.specInstantiated(spec5)
               mock.specFinished(spec5::class, any(), any())
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(spec6::class)
               mock.specInstantiated(spec6)
               mock.specFinished(spec6::class, any(), any())
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
