package io.kotest.core.config

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.core.test.TestCaseOrder

data class EngineConfiguration(
   val writeSpecFailureFile: Boolean,
   val specFailureFilePath: String,
   val globalAssertSoftly: Boolean,
   val testNameCase: TestNameCase,
   val failOnIgnoredTests: Boolean,
   val assertionMode: AssertionMode,
   val parallelism: Int,
   val dispatcherAffinity: Boolean,
   val concurrentSpecs: Int?,
   val concurrentTests: Int,
   val timeout: Long,
   val invocationTimeout: Long,
   val projectTimeout: Long,
   val logLevel: LogLevel,
   val testCoroutineDispatcher: Boolean,
   val defaultTestConfig: ResolvedTestConfig,
   val failOnEmptyTestSuite: Boolean,
   val coroutineDebugProbes: Boolean,
   val includeTestScopeAffixes: Boolean?,
   val displaySpecIfNoActiveTests: Boolean,
   val isolationMode: IsolationMode,
   val testCaseOrder: TestCaseOrder,
   val specExecutionOrder: SpecExecutionOrder,
   val removeTestNameWhitespace: Boolean,
   val testNameAppendTags: Boolean,
   val duplicateTestNameMode: DuplicateTestNameMode,
   val displayFullTestPath: Boolean,
)
