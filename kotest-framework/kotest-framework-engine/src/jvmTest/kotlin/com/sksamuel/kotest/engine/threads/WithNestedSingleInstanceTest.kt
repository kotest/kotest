//package com.sksamuel.kotest.engine.threads
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
//import io.kotest.matchers.maps.shouldHaveSize
//import kotlin.concurrent.getOrSet
//
//class WithNestedSingleInstanceTest : FunSpec({
//
//   isolationMode = IsolationMode.SingleInstance
//   threads = 3
//
//   val multipleThreadAccum =
//      PersistentThreadLocal<String>()
//
//   afterSpec {
//      multipleThreadAccum.map.shouldHaveSize(3)
//      multipleThreadAccum.map.values.shouldContainExactlyInAnyOrder("aa", "bb", "ccdd")
//   }
//
//   context("First single thread context") {
//      test("test 1 should create new key in map for context or add value a") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "a")
//      }
//
//      test("test 2 should create new key in map for context or add value a") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "a")
//      }
//   }
//
//   context("Second single thread context") {
//      test("test 1 should create new key in map for context or add value b") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "b")
//      }
//
//      test("test 2 should create new key in map for context or add value b") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "b")
//      }
//   }
//
//   context("Third single thread context") {
//      test("test 1 should create new key in map for context or add value c") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "c")
//      }
//
//      test("test 2 should create new key in map for context or add value c") {
//         val accum = multipleThreadAccum.getOrSet { "" }
//         multipleThreadAccum.set(accum + "c")
//      }
//
//      context("First inner single thread context") {
//         test("test 1 should create new key in map for context or add value d") {
//            val accum = multipleThreadAccum.getOrSet { "" }
//            multipleThreadAccum.set(accum + "d")
//         }
//
//         test("test 2 should create new key in map for context or add value d") {
//            val accum = multipleThreadAccum.getOrSet { "" }
//            multipleThreadAccum.set(accum + "d")
//         }
//      }
//   }
//
//})
