package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

/**
 * This test seeks to define the order in which [BeforeEach] extensions are invoked.
 */
class BeforeEachOrderSpecificationTest : FunSpec() {

   private var order = ""

   override suspend fun beforeEach(testCase: TestCase) {
      order += "a"
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : BeforeEachListener {
            override suspend fun beforeEach(testCase: TestCase) {
               order += "f"
            }
         },
         object : BeforeEachListener {
            override suspend fun beforeEach(testCase: TestCase) {
               order += "g"
            }
         },
      )
   }

   init {

      afterProject {
         order shouldBe "fgabcfgabcdefgabcde"
      }

      beforeEach {
         order += "b"
      }

      beforeEach {
         order += "c"
      }

      test("foo") { }
      context("bar") {

         beforeEach {
            order += "d"
         }

         beforeEach {
            order += "e"
         }

         test("baz") { }
         test("bing") { }
      }
   }
}
