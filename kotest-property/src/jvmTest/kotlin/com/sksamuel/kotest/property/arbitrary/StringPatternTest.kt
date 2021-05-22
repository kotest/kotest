package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.concurrent.Executors

class StringPatternTest : FunSpec({
   test("should be cached") {
      val arbPattern = Arb.stringPattern("[a-zA-Z0-9]+")

      val testDispatcher = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
      val (first, second, third) = awaitAll(
         async(testDispatcher) { arbPattern.take(1000, RandomSource.seeded(1234L)).toList().takeLast(10) },
         async(testDispatcher) { arbPattern.take(1000, RandomSource.seeded(2345L)).toList().takeLast(10) },
         async(testDispatcher) { arbPattern.take(1000, RandomSource.seeded(1324L)).toList().takeLast(10) }
      )

      first shouldContainInOrder listOf(
         "Sg",
         "T8",
         "7Jx0zg5",
         "o",
         "Mu",
         "e5",
         "b0",
         "g",
         "uF3",
         "h"
      )
      second shouldContainInOrder listOf(
         "8",
         "6I",
         "i4",
         "j4",
         "w",
         "TiOO",
         "E",
         "Hz",
         "raE",
         "2974dU"
      )
      third shouldContainInOrder listOf(
         "Z3",
         "K",
         "rs3V7",
         "8",
         "P7",
         "9058",
         "F",
         "LuU",
         "tB9",
         "5"
      )
   }

   context("should not timeout") {
      timeout = 30000
      test("should be quick") {
         val arbPattern = Arb.stringPattern("[a-zA-Z0-9]+")

         val testDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
         generateSequence { async(testDispatcher) { arbPattern.take(500000).last() } }
            .take(10)
            .toList()
            .awaitAll()
      }
   }
})
