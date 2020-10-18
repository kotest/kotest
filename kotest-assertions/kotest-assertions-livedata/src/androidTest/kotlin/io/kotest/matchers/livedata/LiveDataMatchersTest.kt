package io.kotest.matchers.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.livedata.ImmediateTaskExecutorListener
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

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
         val liveData = delayedLiveData(200, "delayed")

         liveData.willReceiveValue("delayed")
      }

      test("Value not received after 100ms") {
         val liveData = delayedLiveData(200, "delayed")

         shouldThrowMessage("Value not received from the LiveData, expected [delayed]") {
            liveData.willReceiveValue("delayed", 100, TimeUnit.MILLISECONDS)
         }
      }

      test("Different value received after 200ms") {
         val liveData = delayedLiveData(200, "delayed")

         liveData.willNotReceiveValue("immediate")
      }

      test("Unexpected different value received after 200ms") {
         val liveData = delayedLiveData(200, "delayed")

         shouldThrowMessage("The value received [delayed] is not the one expected [immediate]") {
            liveData.willReceiveValue("immediate")
         }
      }
   }
})

private fun delayedLiveData(delayMillis: Long, valueEmitted: String): LiveData<String> {
   return object : LiveData<String>() {
      override fun onActive() {
         super.onActive()
         thread(true) {
            Thread.sleep(delayMillis)
            postValue(valueEmitted)
         }
      }
   }
}
