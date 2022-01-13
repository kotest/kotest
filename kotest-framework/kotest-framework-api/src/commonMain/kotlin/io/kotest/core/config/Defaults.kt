package io.kotest.core.config

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.TestCaseConfig
import kotlin.time.Duration.Companion.minutes

object Defaults {

   val assertionMode: AssertionMode = AssertionMode.None
   val testCaseConfig: TestCaseConfig = TestCaseConfig()
   val testCaseOrder: TestCaseOrder = TestCaseOrder.Sequential
   val isolationMode: IsolationMode = IsolationMode.SingleInstance
   val duplicateTestNameMode: DuplicateTestNameMode = DuplicateTestNameMode.Warn
   const val displayFullTestPath: Boolean = false

   val severity: TestCaseSeverityLevel = TestCaseSeverityLevel.NORMAL

   const val coroutineDebugProbes: Boolean = false
   const val testCoroutineDispatcher: Boolean = false
   const val blockingTest: Boolean = false

   const val displaySpecIfNoActiveTests: Boolean = true

   const val failOnEmptyTestSuite = false

   const val specFailureFilePath: String = "./.kotest/spec_failures"

   const val parallelism: Int = 1

   val defaultTimeout = 10.minutes
   val defaultInvocationTimeout = 10.minutes

   const val failOnIgnoredTests: Boolean = false
   const val failfast: Boolean = false
   const val projectWideFailFast: Boolean = false
   val defaultIncludeTestScopeAffixes: Boolean? = null
   const val writeSpecFailureFile = false
   const val globalAssertSoftly = false

   val defaultTestNameCase: TestNameCase = TestNameCase.AsIs
   val specExecutionOrder = SpecExecutionOrder.Lexicographic

   const val concurrentTests = ProjectConfiguration.Sequential
   const val dispatcherAffinity = true
}
