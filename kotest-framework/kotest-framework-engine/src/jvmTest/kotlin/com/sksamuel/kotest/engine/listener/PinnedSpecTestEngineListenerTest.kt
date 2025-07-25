package com.sksamuel.kotest.engine.listener

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.test.TestResult
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
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class PinnedSpecTestEngineListenerTest : WordSpec({

   "PinnedSpecTestEngineListener" should {
      "only notify for the running test" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(SpecRef.Reference(spec1::class))
         listener.specStarted(SpecRef.Reference(spec2::class))
         listener.specStarted(SpecRef.Reference(spec3::class))

         verify { runBlocking { mock.specStarted(SpecRef.Reference(spec1::class)) } }
         verify(exactly = 0) { runBlocking { mock.specStarted(SpecRef.Reference(spec2::class)) } }
         verify(exactly = 0) { runBlocking { mock.specStarted(SpecRef.Reference(spec3::class)) } }
      }

      "run queued callbacks for a single next spec when current spec completes" {

         val mock = mockk<TestEngineListener>(relaxed = true)
         val listener = PinnedSpecTestEngineListener(mock)

         val spec1 = IsolationTestSpec1()
         val spec2 = IsolationTestSpec2()
         val spec3 = IsolationTestSpec3()

         listener.specStarted(SpecRef.Reference(spec1::class))
         listener.specStarted(SpecRef.Reference(spec2::class))
         listener.specFinished(SpecRef.Reference(spec2::class), TestResult.Success(0.seconds))
         listener.specFinished(SpecRef.Reference(spec3::class), TestResult.Success(0.seconds))

         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec1::class))
            }
         }

         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec2::class))
            }
         }

         listener.specFinished(SpecRef.Reference(spec1::class), TestResult.Success(0.seconds))

         verifyOrder {
            runBlocking {
               mock.specFinished(SpecRef.Reference(spec1::class), TestResult.Success(0.seconds))
               mock.specStarted(SpecRef.Reference(spec2::class))
               mock.specFinished(SpecRef.Reference(spec2::class), TestResult.Success(0.seconds))
            }
         }
         verify(exactly = 0) {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec3::class))
               mock.specFinished(SpecRef.Reference(spec3::class), TestResult.Success(0.seconds))
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
               listener.specStarted(SpecRef.Reference(spec1::class))
               listener.specFinished(SpecRef.Reference(spec1::class), TestResult.Success(0.seconds))
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(SpecRef.Reference(spec2::class))
               listener.specFinished(SpecRef.Reference(spec2::class), TestResult.Success(0.seconds))
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(SpecRef.Reference(spec3::class))
               listener.specFinished(SpecRef.Reference(spec3::class), TestResult.Success(0.seconds))
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(SpecRef.Reference(spec4::class))
               listener.specFinished(SpecRef.Reference(spec4::class), TestResult.Success(0.seconds))

               listener.specStarted(SpecRef.Reference(spec5::class))
               listener.specFinished(SpecRef.Reference(spec5::class), TestResult.Success(0.seconds))
            }
            launch(Dispatchers.IO) {
               delay(kotlin.random.Random.nextLong(1, 100))
               listener.specStarted(SpecRef.Reference(spec6::class))
               listener.specFinished(SpecRef.Reference(spec6::class), TestResult.Success(0.seconds))
            }
         }

         // -- we verify each block separately because they will run in different orders,
         // but each spec should be ordered itself

         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec1::class))
               mock.specFinished(SpecRef.Reference(spec1::class), TestResult.Success(0.seconds))
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec2::class))
               mock.specFinished(SpecRef.Reference(spec2::class), TestResult.Success(0.seconds))
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec3::class))
               mock.specFinished(SpecRef.Reference(spec3::class), TestResult.Success(0.seconds))
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec4::class))
               mock.specFinished(SpecRef.Reference(spec4::class), TestResult.Success(0.seconds))

               mock.specStarted(SpecRef.Reference(spec5::class))
               mock.specFinished(SpecRef.Reference(spec5::class), TestResult.Success(0.seconds))
            }
         }
         verifyOrder {
            runBlocking {
               mock.specStarted(SpecRef.Reference(spec6::class))
               mock.specFinished(SpecRef.Reference(spec6::class), TestResult.Success(0.seconds))
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
