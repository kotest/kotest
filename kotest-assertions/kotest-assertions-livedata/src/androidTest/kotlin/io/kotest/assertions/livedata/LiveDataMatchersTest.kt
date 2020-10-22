package io.kotest.assertions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.livedata.ImmediateTaskExecutorListener
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
class LiveDataMatchersTest : FunSpec({
   listener(ImmediateTaskExecutorListener())

   isolationMode = IsolationMode.InstancePerTest

   context("Immediate assertions") {
      test("Value received") {
         val liveData = MutableLiveData("immediate")
         liveData shouldHaveValue "immediate"
      }

      test("Different value received") {
         val liveData = MutableLiveData("immediate")
         shouldThrowMessage("The value received is [immediate]") {
            liveData shouldNotHaveValue "immediate"
         }
      }
   }

   context("Future assertions") {
      test("Value received after 200ms") {
         val liveData = delayedLiveData(200.milliseconds, "delayed")

         liveData.willReceiveValue("delayed")
      }

      test("Value not received after 100ms") {
         val liveData = delayedLiveData(200.milliseconds, "delayed")

         shouldThrowMessage("Value not received from the LiveData, expected [delayed]") {
            liveData.willReceiveValue("delayed", 100, TimeUnit.MILLISECONDS)
         }
      }

      test("Different value received after 200ms") {
         val liveData = delayedLiveData(200.milliseconds, "delayed")

         liveData.willNotReceiveValue("immediate")
      }

      test("Unexpected different value received after 200ms") {
         val liveData = delayedLiveData(200.milliseconds, "delayed")

         shouldThrowMessage("The value received [delayed] is not the one expected [immediate]") {
            liveData.willReceiveValue("immediate")
         }
      }
   }
})

@OptIn(ExperimentalTime::class)
fun <T>delayedLiveData(delay: Duration, valueEmitted: T): LiveData<T> {
   return object : LiveData<T>() {
      override fun onActive() {
         super.onActive()
         thread(true) {
            Thread.sleep(delay.toLongMilliseconds())
            postValue(valueEmitted)
         }
      }
   }
}
