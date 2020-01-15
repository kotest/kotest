package com.sksamuel.kotest.specs.funspec

import io.kotest.core.SpecClass
import io.kotest.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldStartWith
import io.kotest.shouldBe
import java.nio.file.Paths
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class FunSpecTest : FunSpec() {

   var count = 0

   init {

      test("test without config") {
         "hello world".shouldStartWith("hello")
      }

      test("test with config").config(enabled = true) {
         assertSoftly {
            val file = Paths.get(".password")
            file.shouldNotExist()
         }
      }

      test("test with timeout").config(timeout = 1234.milliseconds) {
         count += 1
      }

      context("a context can hold tests") {
         test("foo") {
            "a".shouldNotBeBlank()
         }
         context("and even other contexts!") {
            test("wibble") {
               "hello".shouldHaveLength(5)
            }
         }
      }
   }

   override fun afterSpec(spec: SpecClass) {
      count.shouldBe(1)
   }
}
