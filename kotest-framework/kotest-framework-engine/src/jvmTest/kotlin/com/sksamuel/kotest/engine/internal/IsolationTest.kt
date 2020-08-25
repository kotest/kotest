package com.sksamuel.kotest.engine.internal

import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.listener.IsolationTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder

class IsolationTestEngineListenerTest : WordSpec({

   "IsolationTestEngineListener" should {
      "only notify for the running test" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = IsolationTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specInstantiated(spec2)
         listener.specInstantiated(spec3)

         verify { mock.specInstantiated(spec1) }
         verify(exactly = 0) { mock.specInstantiated(spec2) }
         verify(exactly = 0) { mock.specInstantiated(spec3) }
      }

      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = IsolationTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(spec1::class)
         listener.specInstantiated(spec1)
         listener.specStarted(spec2::class)
         listener.specInstantiated(spec2)
         listener.specFinished(spec3::class, null, emptyMap())

         verifyOrder {
            mock.specStarted(spec1::class)
            mock.specInstantiated(spec1)
         }

         verify(exactly = 0) {
            mock.specStarted(spec2::class)
            mock.specInstantiated(spec2)
         }

         listener.specFinished(spec1::class, null, emptyMap())

         verifyOrder {
            mock.specFinished(spec1::class, any(), any())
            mock.specStarted(spec2::class)
            mock.specInstantiated(spec2)
         }
         verify(exactly = 0) {
            mock.specStarted(spec3::class)
            mock.specInstantiated(spec3)
            mock.specFinished(spec3::class, any(), any())
         }
      }
   }
})

class IsolationTestSpec1 : WordSpec()
class IsolationTestSpec2 : WordSpec()
class IsolationTestSpec3 : WordSpec()
