package com.sksamuel.kt.extensions.locale

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.extensions.locale.TimezoneExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.ZoneId
import java.util.*

class TimezoneExtensionTest : FunSpec() {

  override fun extensions() = listOf(TimezoneExtension(TimeZone.getTimeZone(ZoneId.of("Africa/Dakar"))))

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