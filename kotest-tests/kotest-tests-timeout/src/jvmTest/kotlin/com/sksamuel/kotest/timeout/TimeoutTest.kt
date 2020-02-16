//package com.sksamuel.kotest.timeout
//
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.test.TestStatus
//import io.kotest.core.extensions.SpecLevelExtension
//import io.kotest.core.extensions.TestCaseExtension
//import io.kotest.core.spec.style.StringSpec
//import kotlinx.coroutines.delay
//import kotlin.time.ExperimentalTime
//import kotlin.time.milliseconds
//
//@ExperimentalTime
//class TimeoutTest : StringSpec() {
//
//  init {
//
//    "a blocked thread should timeout a test".config(timeout = 250.milliseconds) {
//      Thread.sleep(1000)
//    }
//
//    "a suspended coroutine should timeout a test".config(timeout = 250.milliseconds) {
//      delay(1000)
//    }
//  }
//
//  override fun extensions(): List<SpecLevelExtension> = listOf(object : TestCaseExtension {
//    override suspend fun intercept(testCase: TestCase,
//                                   execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
//                                   complete: suspend (TestResult) -> Unit) {
//      execute(testCase) {
//        when (it.status) {
//          TestStatus.Failure, TestStatus.Error -> complete(
//              TestResult.success(1000.milliseconds))
//          else -> throw RuntimeException("${testCase.description} should fail")
//        }
//      }
//    }
//  })
//}
