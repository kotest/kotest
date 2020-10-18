package io.kotest.matchers.livedata

import androidx.lifecycle.LiveData
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun <T> haveValue(expectedValue: T, time: Long = 0, timeUnit: TimeUnit = TimeUnit.SECONDS) =
   object : Matcher<LiveData<T>> {
      override fun test(value: LiveData<T>): MatcherResult {
         val receivedValue = try {
            value.getOrAwaitValue(time, timeUnit)
         } catch (e: TimeoutException) {
            null
         }

         return MatcherResult(
            receivedValue == expectedValue, {
               if (receivedValue == null) "Value not received from the LiveData, expected [$expectedValue]"
               else "The value received [$receivedValue] is not the one expected [$expectedValue]"
            }, {
               if (receivedValue == null) "Value not received from the LiveData, expected different from [$expectedValue]"
               else "The value received is [$receivedValue]"
            })
      }
   }

infix fun <T> LiveData<T>.shouldHaveValue(value: T) = this should haveValue(value)
infix fun <T> LiveData<T>.shouldNotHaveValue(value: T) = this shouldNot haveValue(value)

fun <T> LiveData<T>.willReceiveValue(value: T, timeout: Long = 2, timeUnit: TimeUnit = TimeUnit.SECONDS) =
   this should haveValue(value, timeout, timeUnit)

fun <T> LiveData<T>.willNotReceiveValue(value: T, timeout: Long = 2, timeUnit: TimeUnit = TimeUnit.SECONDS) =
   this shouldNot haveValue(value, timeout, timeUnit)
