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

   private val order = mutableListOf<String>()

   override suspend fun beforeEach(testCase: TestCase) {
      order.add("fn_override")
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : BeforeEachListener {
            override suspend fun beforeEach(testCase: TestCase) {
               order.add("extension1")
            }
         },
         object : BeforeEachListener {
            override suspend fun beforeEach(testCase: TestCase) {
               order.add("extension2")
            }
         },
      )
   }

   init {

      afterProject {
         order.joinToString(",") shouldBe "extension1,extension2,fn_override,dsl1,dsl2,extension1,extension2,fn_override,dsl1,dsl2,dsl3,dsl4"
      }

      beforeEach {
         order.add("dsl1")
      }

      beforeEach {
         order.add("dsl2")
      }

      test("foo") { }
      context("bar") {

         beforeEach {
            order.add("dsl3")
         }

         beforeEach {
            order.add("dsl4")
         }

         test("baz") { }
      }
   }
}
