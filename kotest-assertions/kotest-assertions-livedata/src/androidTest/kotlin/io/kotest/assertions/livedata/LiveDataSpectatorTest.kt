package io.kotest.assertions.livedata

import androidx.lifecycle.MutableLiveData
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.livedata.ImmediateTaskExecutorListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.concurrent.thread
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class LiveDataSpectatorTest : FunSpec({
   listener(ImmediateTaskExecutorListener())

   isolationMode = IsolationMode.InstancePerTest

   test("Last value") {
      val liveData = MutableLiveData(1)

      val spectator = liveData.spectate()

      spectator.lastValue shouldBe 1

      liveData.postValue(2)

      spectator.lastValue shouldBe 2
   }

   context("Await next value"){
      test("Value in range of timeout") {
         val liveData = MutableLiveData(1)
         val spectator = liveData.spectate()

         thread(true) {
            Thread.sleep(10)
            liveData.postValue(10)
         }

         spectator.awaitNextValue(2.seconds) shouldBe 10
      }
      test("Timeout") {
         val liveData = MutableLiveData(1)
         val spectator = liveData.spectate()

         thread(true) {
            Thread.sleep(100.milliseconds.toLongMilliseconds())
            liveData.postValue(10)
         }

         shouldThrow<TimeoutCancellationException> {
            spectator.awaitNextValue(10.milliseconds)
         }
      }
   }

   context("Await value"){
      test("No value, delayed") {
         val liveData = delayedLiveData(100.milliseconds, 10)
         val spectator = liveData.spectate()

         spectator.awaitValue(2.seconds) shouldBe 10
      }

      test("No value, timeout") {
         val liveData = delayedLiveData(100.milliseconds, 10)
         val spectator = liveData.spectate()

         shouldThrow<TimeoutCancellationException> {
            spectator.awaitValue(10.milliseconds)
         }
      }

      test("Immediate") {
         val liveData = MutableLiveData(10)
         val spectator = liveData.spectate()

         spectator.awaitValue(2.seconds) shouldBe 10
      }

      test("Value changed, first taken") {
         val liveData = MutableLiveData(10)
         val spectator = liveData.spectate()

         thread(start = true) {
            Thread.sleep(10)
            liveData.postValue(11)
         }

         spectator.awaitValue(2.seconds) shouldBe 10
      }
   }
})
