//package com.sksamuel.kotlintest
//
//import io.kotlintest.Description
//import io.kotlintest.TestCase
//import io.kotlintest.TestCaseConfig
//import io.kotlintest.TestContext
//import io.kotlintest.TestStatus
//import io.kotlintest.TestType
//import io.kotlintest.runner.jvm.TestEngineListener
//import io.kotlintest.runner.jvm.TestSet
//import io.kotlintest.runner.jvm.TestSetExecutor
//import io.kotlintest.shouldBe
//import io.kotlintest.specs.StringSpec
//import kotlinx.coroutines.GlobalScope
//import java.time.Duration
//
//class TestSetExecutorTest : StringSpec() {
//  init {
//
//    "multiple invocations and threads should complete" {
//
//      var counter = 0
//
//      val context = object : TestContext(GlobalScope.coroutineContext) {
//        override fun registerTestCase(testCase: TestCase) {}
//        override fun description(): Description = Description.root("wibble")
//      }
//
//      val testCase = TestCase(Description.root("wibble"), this@TestSetExecutorTest, { counter++ }, 0, TestType.Test, TestCaseConfig(true, 10, parallelism = 3))
//
//      val executor = TestSetExecutor(object : TestEngineListener {})
//      executor.execute(TestSet(testCase, Duration.ofDays(1), 10, 3), context).status shouldBe TestStatus.Success
//
//      counter shouldBe 10
//    }
//
//    // this is to test that if there is an error and invocations > 1 then the test completes with an error
//    // https://github.com/kotlintest/kotlintest/issues/413
//    "multiple invocations with a failure should complete" {
//
//      val context = object : TestContext(GlobalScope.coroutineContext) {
//        override fun registerTestCase(testCase: TestCase) {}
//        override fun description(): Description = Description.root("wibble")
//      }
//
//      val testCase = TestCase(Description.root("wibble"), this@TestSetExecutorTest, { "a" shouldBe "b" }, 0, TestType.Test, TestCaseConfig(true, 10, parallelism = 1))
//
//      val executor = TestSetExecutor(object : TestEngineListener {})
//      executor.execute(TestSet(testCase, Duration.ofDays(1), 10, 1), context).status shouldBe TestStatus.Failure
//    }
//
//    "multiple invocations with an error should complete" {
//
//      val context = object : TestContext(GlobalScope.coroutineContext) {
//        override fun registerTestCase(testCase: TestCase) {}
//        override fun description(): Description = Description.root("wibble")
//      }
//
//      val testCase = TestCase(Description.root("wibble"), this@TestSetExecutorTest, { throw RuntimeException() }, 0, TestType.Test, TestCaseConfig(true, 10, parallelism = 1))
//
//      val executor = TestSetExecutor(object : TestEngineListener {})
//      executor.execute(TestSet(testCase, Duration.ofDays(1), 10, 1), context).status shouldBe TestStatus.Error
//    }
//
//    // this is to test that if there is an error and invocations > 1 then the test completes with an error
//    // https://github.com/kotlintest/kotlintest/issues/413
//    "multiple invocations and parallelism with a failure should complete" {
//
//      val context = object : TestContext(GlobalScope.coroutineContext) {
//        override fun registerTestCase(testCase: TestCase) {}
//        override fun description(): Description = Description.root("wibble")
//      }
//
//      val testCase = TestCase(Description.root("wibble"), this@TestSetExecutorTest, { "a" shouldBe "b" }, 0, TestType.Test, TestCaseConfig(true, 10, parallelism = 3))
//
//      val executor = TestSetExecutor(object : TestEngineListener {})
//      executor.execute(TestSet(testCase, Duration.ofDays(1), 10, 3), context).status shouldBe TestStatus.Failure
//    }
//
//    "multiple invocations and threads with an error should complete" {
//
//      val context = object : TestContext(GlobalScope.coroutineContext) {
//        override fun registerTestCase(testCase: TestCase) {}
//        override fun description(): Description = Description.root("wibble")
//      }
//
//      val testCase = TestCase(Description.root("wibble"), this@TestSetExecutorTest, { throw RuntimeException() }, 0, TestType.Test, TestCaseConfig(true, 10, parallelism = 3))
//
//      val executor = TestSetExecutor(object : TestEngineListener {})
//      executor.execute(TestSet(testCase, Duration.ofDays(1), 10, 3), context).status shouldBe TestStatus.Error
//    }
//  }
//}