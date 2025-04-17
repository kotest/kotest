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

   private val order = mutableListOf<String>()

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      order.add("fn_override")
   }

   override val extensions: List<Extension> = listOf(
      object : AfterEachListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            order.add("extension1")
         }
      },
      object : AfterEachListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            order.add("extension2")
         }
      },
   )

   init {

      afterProject {
         order.joinToString(",") shouldBe "extension1,extension2,fn_override,dsl2,dsl1,extension1,extension2,fn_override,dsl4,dsl3,dsl2,dsl1"
      }

      // after each blocks are executed in reverse order, so nested ones run before outer ones
      afterEach {
         order.add("dsl1")
      }

      afterEach {
         order.add("dsl2")
      }

      test("foo") { }
      context("bar") {

         afterEach {
            order.add("dsl3")
         }

         afterEach {
            order.add("dsl4")
         }

         test("baz") { }
      }
   }
}
