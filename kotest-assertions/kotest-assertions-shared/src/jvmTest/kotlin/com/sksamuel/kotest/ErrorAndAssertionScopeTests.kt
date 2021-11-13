//package com.sksamuel.kotest
//
//import io.kotest.assertions.*
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import kotlinx.coroutines.runBlocking
//
//private typealias BlockFunction = suspend (suspend () -> Int) -> Int
//
//private fun validAssertions(name: String): Int {
//   when (name) {
//      "all" -> {
//         1 shouldBe 1
//         2 shouldBe 2
//      }
//      "one" -> {
//         1 shouldBe 2
//         1 shouldBe 1
//      }
//      "any" -> {
//         1 shouldBe 2
//         1 shouldBe 1
//      }
//   }
//   return Int.MAX_VALUE
//}
//
//private data class BlockFunctionTest(val a: BlockFunction, val aName: String, val b: BlockFunction, val bName: String) {
//   override fun toString() = "$aName + $bName"
//}
//
//@ExperimentalKotest
//private val blockFunctions: List<Pair<String, BlockFunction>> = listOf(
//   Pair("all", ::all), Pair("one", ::one), Pair("any", ::any)
//)
//
//@ExperimentalKotest
//class ErrorAndAssertionScopeTests : FunSpec({
//   context("assertion block tests should be limited to the maximum depth defined in assertionBlockMaximumDepth") {
//      val cases = blockFunctions.map { a -> blockFunctions.map { b -> Pair(a, b) } }.flatten().map {
//         BlockFunctionTest(it.first.second, it.first.first, it.second.second, it.second.first)
//      }.map {
//         Pair(it.toString(), it)
//      }.toTypedArray()
//
//      forAll(data = cases) {
//         shouldThrow<IllegalStateException> {
//            runBlocking {
//               it.a {
//                  validAssertions(it.aName)
//
//                  it.b {
//                     validAssertions(it.bName)
//                  }
//               }
//            }
//         }
//      }
//   }
//})
