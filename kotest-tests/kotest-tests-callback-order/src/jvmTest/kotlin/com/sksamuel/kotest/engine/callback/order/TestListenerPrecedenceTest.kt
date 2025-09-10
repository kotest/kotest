package com.sksamuel.kotest.engine.callback.order

import io.kotest.core.annotation.Description
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

val afterEachEvents = mutableListOf<String>()
val afterTestEvents = mutableListOf<String>()
val beforeEachEvents = mutableListOf<String>()
val beforeTestEvents = mutableListOf<String>()

@Description("Tests the specification of precedence for TestListener callbacks")
class TestListenerPrecedenceTest : FunSpec() {

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      afterEachEvents.add("fn_override")
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTestEvents.add("fn_override")
   }

   override suspend fun beforeEach(testCase: TestCase) {
      beforeEachEvents.add("fn_override")
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTestEvents.add("fn_override")
   }

   override val extensions: List<Extension> = listOf(
      object : BeforeEachListener {
         override suspend fun beforeEach(testCase: TestCase) {
            beforeEachEvents.add("extension1")
         }
      },
      object : BeforeEachListener {
         override suspend fun beforeEach(testCase: TestCase) {
            beforeEachEvents.add("extension2")
         }
      },
      object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            beforeTestEvents.add("extension1")
         }
      },
      object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            beforeTestEvents.add("extension1")
         }
      },
      object : AfterEachListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            afterEachEvents.add("extension1")
         }
      },
      object : AfterEachListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            afterEachEvents.add("extension2")
         }
      },
      object : AfterTestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            afterTestEvents.add("extension1")
         }
      },
      object : AfterTestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            afterTestEvents.add("extension2")
         }
      },
   )

   init {

      afterProject {
         // only runs for leaf tests - of which we have 2 - foo and baz, so bar would be ignored
         beforeEachEvents.joinToString(",") shouldBe "projectBeforeEach,extension1,extension2,fn_override,dsl1,dsl2,projectBeforeEach,extension1,extension2,fn_override,dsl1,dsl2,dsl3,dsl4"
         // runs for all tests - of which we have 3 - foo, bar and baz
         beforeTestEvents.joinToString(",") shouldBe "projectBeforeTest,extension1,extension1,fn_override,dsl1,dsl2,projectBeforeTest,extension1,extension1,fn_override,dsl1,dsl2,projectBeforeTest,extension1,extension1,fn_override,dsl1,dsl2,dsl3,dsl4"
         // only runs for leaf tests - of which we have 2 - foo and baz, so bar would be ignored
         // the dsl3 and dsl4 are registered inside a context, so would apply to baz only
         // after events are executed in reverse order, so the nested ones run first
         afterEachEvents.joinToString(",") shouldBe "dsl2,dsl1,fn_override,extension2,extension1,projectAfterEach,dsl4,dsl3,dsl2,dsl1,fn_override,extension2,extension1,projectAfterEach"
         // runs for all tests - of which we have 3 - foo, bar and baz
         // after events are executed in reverse order, so the nested ones run first
         afterTestEvents.joinToString(",") shouldBe "dsl2,dsl1,fn_override,extension2,extension1,projectAfterTest,dsl4,dsl3,dsl2,dsl1,fn_override,extension2,extension1,projectAfterTest,dsl2,dsl1,fn_override,extension2,extension1,projectAfterTest"
      }

      beforeEach {
         beforeEachEvents.add("dsl1")
      }

      beforeEach {
         beforeEachEvents.add("dsl2")
      }

      beforeTest {
         beforeTestEvents.add("dsl1")
      }

      beforeTest {
         beforeTestEvents.add("dsl2")
      }

      // after each blocks are executed in reverse order, so nested ones run before outer ones
      afterEach {
         afterEachEvents.add("dsl1")
      }

      afterEach {
         afterEachEvents.add("dsl2")
      }

      // afterTest blocks are executed in reverse order, so nested ones run before outer ones
      afterTest {
         afterTestEvents.add("dsl1")
      }

      afterTest {
         afterTestEvents.add("dsl2")
      }

      test("foo") { }
      context("bar") {

         beforeEach {
            beforeEachEvents.add("dsl3")
         }

         beforeEach {
            beforeEachEvents.add("dsl4")
         }

         beforeTest {
            beforeTestEvents.add("dsl3")
         }

         beforeTest {
            beforeTestEvents.add("dsl4")
         }

         afterEach {
            afterEachEvents.add("dsl3")
         }

         afterEach {
            afterEachEvents.add("dsl4")
         }

         afterTest {
            afterTestEvents.add("dsl3")
         }

         afterTest {
            afterTestEvents.add("dsl4")
         }

         test("baz") { }
      }
   }
}
