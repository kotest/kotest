//package com.sksamuel.kt.extensions.locale
//
//import io.kotest.core.annotation.Isolate
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.extensions.locale.TimeZoneTestListener
//import io.kotest.matchers.shouldBe
//import java.time.ZoneId
//import java.util.TimeZone
//
//@Isolate
//class TimeZoneListenerTest : FunSpec() {
//
//   private val default = TimeZone.getDefault()
//   private val dakar = ZoneId.of("Africa/Dakar")
//   private val dakarTzl = TimeZoneTestListener(TimeZone.getTimeZone(dakar))
//
//   override fun listeners() = listOf(dakarTzl)
//
//   init {
//      test("time zone default should be set, and then restored after") {
//         TimeZone.getDefault() shouldBe TimeZone.getTimeZone(dakar)
//      }
//      afterSpec {
//         TimeZone.getDefault() shouldBe default
//      }
//   }
//}
