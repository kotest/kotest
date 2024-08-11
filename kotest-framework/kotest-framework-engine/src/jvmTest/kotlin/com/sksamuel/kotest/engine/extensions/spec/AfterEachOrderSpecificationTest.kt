package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

/**
 * This test seeks to define the order in which [AfterEach] extensions are invoked.
 */
class AfterEachOrderSpecificationTest : FunSpec() {

   private var order = ""

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      order += "a"
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : AfterEachListener {
            override suspend fun afterEach(testCase: TestCase, result: TestResult) {
               order += "f"
            }
         },
         object : AfterEachListener {
            override suspend fun afterEach(testCase: TestCase, result: TestResult) {
               order += "g"
            }
         },
      )
   }

   init {

      afterProject {
         order shouldBe "fgacbfgaedcbfgaedcb"
      }

      // after each blocks are executed in reverse order, so nested ones run before outer ones
      afterEach {
         order += "b"
      }

      afterEach {
         order += "c"
      }

      test("foo") { }
      context("bar") {

         afterEach {
            order += "d"
         }

         afterEach {
            order += "e"
         }

         test("baz") { }
         test("bing") { }
      }
   }
}
