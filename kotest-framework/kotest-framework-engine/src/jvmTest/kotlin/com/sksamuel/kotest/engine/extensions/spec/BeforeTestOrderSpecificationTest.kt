package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

/**
 * This test seeks to define the order in which [beforeTest] extensions are invoked.
 */
class BeforeTestOrderSpecificationTest : FunSpec() {

   private var order = ""

   override suspend fun beforeTest(testCase: TestCase) {
      order += "a"
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "f"
            }
         },
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "g"
            }
         },
      )
   }

   init {

      afterProject {
         order shouldBe "fgabcfgabcfgabcdefgabcde"
      }

      beforeTest {
         order += "b"
      }

      beforeTest {
         order += "c"
      }

      test("foo") { }
      context("bar") {

         beforeTest {
            order += "d"
         }

         beforeTest {
            order += "e"
         }

         test("baz") { }
         test("bing") { }
      }
   }
}
