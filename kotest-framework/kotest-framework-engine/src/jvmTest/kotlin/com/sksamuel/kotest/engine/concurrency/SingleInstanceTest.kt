//package com.sksamuel.kotest.engine.threads
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.maps.shouldHaveSize
//import io.kotest.matchers.shouldBe
//import kotlin.concurrent.getOrSet
//
//class SingleInstanceTest : FunSpec({
//
//   isolationMode = IsolationMode.SingleInstance
//   threads = 3
//
//   val multipleThreadCounter = PersistentThreadLocal<Int>()
//
//   afterSpec {
//      multipleThreadCounter.map.shouldHaveSize(3)
//      multipleThreadCounter.map.values.sum() shouldBe 6
//   }
//
//   test("test 1 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//
//   test("test 2 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//
//   test("test 3 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//
//   test("test 4 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//
//   test("test 5 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//
//   test("test 6 should create own key in map with value 1") {
//      val counter = multipleThreadCounter.getOrSet { 0 }
//      multipleThreadCounter.set(counter + 1)
//   }
//})
