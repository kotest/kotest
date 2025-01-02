//package com.sksamuel.kotest.engine.threads
//
//import io.kotest.assertions.assertSoftly
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.collections.shouldContainAnyOf
//import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
//import io.kotest.matchers.maps.shouldHaveSize
//import kotlin.concurrent.getOrSet
//
//private val externalThreadAccum = PersistentThreadLocal<String>()
//
//class SpecThreadWithNestedTestInstancePerRootTest : FunSpec({
//
//   isolationMode = IsolationMode.InstancePerRoot
//   threads = 3
//
//   val innerThreadAccum =
//      PersistentThreadLocal<String>()
//
//   afterSpec {
//      assertSoftly {
//         innerThreadAccum.map shouldHaveSize 1
//         innerThreadAccum.map.values.shouldContainAnyOf("a", "aa", "b", "bb", "c", "cc", "cd", "cdd")
//      }
//   }
//
//   afterProject {
//      assertSoftly {
//         externalThreadAccum.map.shouldHaveSize(3)
//         externalThreadAccum.map.values.shouldContainExactlyInAnyOrder("aaaaa", "bbbbb", "ccccccdcddcdd")
//      }
//   }
//
//   context("First single thread context") {
//      val externalAccum = externalThreadAccum.getOrSet { "" }
//      externalThreadAccum.set(externalAccum + "a")
//
//      val accum = innerThreadAccum.getOrSet { "" }
//      innerThreadAccum.set(accum + "a")
//
//      test("test 1 should create own key in map with value a") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "a")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "a")
//      }
//
//      test("test 2 should create own key in map with value 1") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "a")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "a")
//      }
//   }
//
//   context("Second single thread context") {
//
//      val externalAccum = externalThreadAccum.getOrSet { "" }
//      externalThreadAccum.set(externalAccum + "b")
//
//      val accum = innerThreadAccum.getOrSet { "" }
//      innerThreadAccum.set(accum + "b")
//
//      test("test 1 should create key in map or add value b") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "b")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "b")
//      }
//
//      test("test 2 should create key in map or add value b") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "b")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "b")
//      }
//   }
//
//   context("Third single thread context") {
//
//      val externalAccum = externalThreadAccum.getOrSet { "" }
//      externalThreadAccum.set(externalAccum + "c")
//
//      val accum = innerThreadAccum.getOrSet { "" }
//      innerThreadAccum.set(accum + "c")
//
//      test("test 1 should create new key in map for context or add value c") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "c")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "c")
//      }
//
//      test("test 2 should create new key in map for context or add value c") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "c")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "c")
//      }
//
//      context("First inner single thread context") {
//         val externalAccum = externalThreadAccum.getOrSet { "" }
//         externalThreadAccum.set(externalAccum + "d")
//
//         val accum = innerThreadAccum.getOrSet { "" }
//         innerThreadAccum.set(accum + "d")
//
//         test("test 1 should create new key in map for context or add value d") {
//            val externalAccum = externalThreadAccum.getOrSet { "" }
//            externalThreadAccum.set(externalAccum + "d")
//
//            val accum = innerThreadAccum.getOrSet { "" }
//            innerThreadAccum.set(accum + "d")
//         }
//
//         test("test 2 should create new key in map for context or add value d") {
//            val externalAccum = externalThreadAccum.getOrSet { "" }
//            externalThreadAccum.set(externalAccum + "d")
//
//            val accum = innerThreadAccum.getOrSet { "" }
//            innerThreadAccum.set(accum + "d")
//         }
//      }
//   }
//
//})
