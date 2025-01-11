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

   private val order = mutableListOf<String>()

   override suspend fun beforeTest(testCase: TestCase) {
      order.add("fn_override")
   }

   override val extensions: List<Extension> {
      return listOf(
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order.add("extension1")
            }
         },
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order.add("extension1")
            }
         },
      )
   }

   init {

      afterProject {
         order.joinToString(",") shouldBe "extension1,extension1,fn_override,dsl1,dsl2,extension1,extension1,fn_override,dsl1,dsl2,extension1,extension1,fn_override,dsl1,dsl2,dsl3,dsl4"
      }

      beforeTest {
         order.add("dsl1")
      }

      beforeTest {
         order.add("dsl2")
      }

      test("foo") { }
      context("bar") {

         beforeTest {
            order.add("dsl3")
         }

         beforeTest {
            order.add("dsl4")
         }

         test("baz") { }
      }
   }
}
