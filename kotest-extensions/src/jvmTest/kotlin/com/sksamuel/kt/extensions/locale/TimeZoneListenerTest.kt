package com.sksamuel.kt.extensions.locale

import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.TopLevelTest
import io.kotest.extensions.locale.TimeZoneTestListener
import io.kotest.extensions.locale.withDefaultTimeZone
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.shouldThrowAny
import io.kotest.specs.DescribeSpec
import io.kotest.specs.FunSpec
import java.time.ZoneId
import java.util.TimeZone

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

  override fun listeners() = listOf(TimeZoneTestListener(TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))))

  private var deftz: TimeZone? = null

  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    deftz = TimeZone.getDefault()
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    TimeZone.getDefault() shouldBe deftz
  }

  init {
    test("time zone default should be set, and then restored after") {
      TimeZone.getDefault() shouldBe TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))
    }
  }
}
