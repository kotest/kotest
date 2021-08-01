package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.concurrent.Executors

class StringPatternTest : FunSpec({

   test("generated patterns should match the regex") {
      val arbPattern = Arb.stringPattern("[a-zA-Z0-9]+")
      arbPattern.take(1000).forEach {
         it.shouldMatch("[a-zA-Z0-9]+".toRegex())
      }
   }

   context("should not timeout") {
      timeout = 5000
      test("should be quick") {
         val arbPattern = Arb.stringPattern("[a-zA-Z0-9]+")

         val testDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
         generateSequence { async(testDispatcher) { arbPattern.take(100000).last() } }
            .take(10)
            .toList()
            .awaitAll()
      }
   }
})
