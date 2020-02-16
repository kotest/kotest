package com.sksamuel.kt.extensions.locale

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.locale.TimeZoneTestListener
import io.kotest.extensions.locale.withDefaultTimeZone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.ZoneId
import java.util.TimeZone
import kotlin.reflect.KClass

class TimeZoneExtensionFunctionTest : DescribeSpec() {

   init {
      describe("The TimeZone extension function") {

         val timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))

         TimeZone.getDefault() shouldNotBe timeZone // Guaranteeing pre-condition, as we'll use this timezone in all tests

         it("Should change the TimeZone to the expected one") {
            withDefaultTimeZone(timeZone) {
               TimeZone.getDefault() shouldBe timeZone
            }
         }

         it("Should reset the TimeZone to the previous one after the execution") {
            val previousTimeZone = TimeZone.getDefault()

            withDefaultTimeZone(timeZone) { }

            TimeZone.getDefault() shouldBe previousTimeZone
         }

         it("Should reset the TimeZone to the previous one even if code throws an exception") {
            val previousTimeZone = TimeZone.getDefault()

            shouldThrowAny { withDefaultTimeZone<Unit>(timeZone) { throw RuntimeException() } }

            TimeZone.getDefault() shouldBe previousTimeZone
         }

         it("Should return the result of block") {

            val v = withDefaultTimeZone(timeZone) { "Foo!" }
            v shouldBe "Foo!"
         }
      }
   }

}

class TimeZoneListenerTest : FunSpec() {

   override fun listeners() = listOf(TimeZoneTestListener(TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))), listener)

   private var deftz: TimeZone? = null

   private val listener = object : TestListener {
      override suspend fun prepareSpec(kclass: KClass<out Spec>) {
         deftz = TimeZone.getDefault()
      }

      override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
         TimeZone.getDefault() shouldBe deftz
      }
   }

   init {
      test("time zone default should be set, and then restored after") {
         TimeZone.getDefault() shouldBe TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))
      }
   }
}
