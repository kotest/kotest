package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

/**
 * This test seeks to define the order in which [AfterTest] extensions are invoked.
 */
class AfterTestOrderSpecificationTest : FunSpec() {

   private val order = mutableListOf<String>()

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      order.add("fn_override")
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : AfterTestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               order.add("extension1")
            }
         },
         object : AfterTestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               order.add("extension2")
            }
         },
      )
   }

   init {

      afterProject {
         order.joinToString(",") shouldBe "extension1,extension2,fn_override,dsl2,dsl1,extension1,extension2,fn_override,dsl4,dsl3,dsl2,dsl1,extension1,extension2,fn_override,dsl2,dsl1"
      }

      // afterTest blocks are executed in reverse order, so nested ones run before outer ones
      afterTest {
         order.add("dsl1")
      }

      afterTest {
         order.add("dsl2")
      }

      test("foo") { }
      context("bar") {

         afterTest {
            order.add("dsl3")
         }

         afterTest {
            order.add("dsl4")
         }

         test("baz") { }
      }
   }
}
