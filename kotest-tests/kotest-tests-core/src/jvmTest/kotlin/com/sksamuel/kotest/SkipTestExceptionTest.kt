package com.sksamuel.kotest

import io.kotest.core.*
import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.SpecConfiguration
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.TestExecutor
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

class SkipTestExceptionTest : FunSpec() {
   init {
      test("A test that throws SkipTestException should have Ignored as a result") {

         var testCasep: TestCase? = null
         var resultp: TestResult? = null

         val listener = object : TestEngineListener {
            override fun testFinished(testCase: TestCase, result: TestResult) {
               testCasep = testCase
               resultp = result
            }
         }

         val executor = TestExecutor(listener)

         val testCase = TestCase.test(Description.spec("wibble"), object : FreeSpec() {}) {
            throw SkipTestException("Foo")
         }

         val context = object : TestContext() {
            override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
            override fun spec(): SpecConfiguration = this@SkipTestExceptionTest
            override suspend fun registerTestCase(testCase: TestCase) {}
            override fun description(): Description = Description.spec("wibble")
         }
         executor.execute(testCase, context)

         resultp!!.status shouldBe TestStatus.Ignored
         testCasep!!.description shouldBe Description.spec("wibble")
      }
   }
}
