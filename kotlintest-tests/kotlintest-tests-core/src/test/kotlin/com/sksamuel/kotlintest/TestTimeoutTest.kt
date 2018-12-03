//package com.sksamuel.kotlintest
//
//import io.kotlintest.milliseconds
//import io.kotlintest.seconds
//import io.kotlintest.shouldNotBe
//import io.kotlintest.specs.StringSpec
//import java.lang.System.currentTimeMillis
//
//class TestTimeoutTest : StringSpec({
//
//  "test should timeout".config(timeout = 2.seconds) {
//    val startTime = currentTimeMillis()
//    while (currentTimeMillis() < startTime + 10000) {
//      "this" shouldNotBe "that"
//    }
//  }
//
//  "has an infinite loop test".config(timeout = 100.milliseconds) {
//    while (true) {
//      "this" shouldNotBe "that"
//    }
//  }
//
//})