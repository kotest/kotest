package io.kotest.core.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestNameCase

object Defaults {

   val assertionMode: AssertionMode = AssertionMode.None
   val testCaseConfig: TestCaseConfig = TestCaseConfig()
   val testCaseOrder: TestCaseOrder = TestCaseOrder.Sequential
   val isolationMode: IsolationMode = IsolationMode.SingleInstance
   const val specFailureFilePath: String = "./.kotest/spec_failures"

   const val parallelism: Int = 1
   const val defaultTimeoutInMillis: Long = 600 * 1000L
   const val defaultInvocationTimeoutInMillis: Long = 600 * 1000L

   const val failOnIgnoredTests: Boolean = false
   val defaultIncludeTestScopeAffixes: Boolean? = null
   const val writeSpecFailureFile = false
   const val globalAssertSoftly = false

   val defaultTestNameCase: TestNameCase = TestNameCase.AsIs
   val specExecutionOrder = SpecExecutionOrder.Lexicographic
}
