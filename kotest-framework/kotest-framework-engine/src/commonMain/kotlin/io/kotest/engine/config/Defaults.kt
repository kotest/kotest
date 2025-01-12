package io.kotest.engine.config

import io.kotest.core.config.LogLevel
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object Defaults {

   const val DUMP_CONFIG = false

   // by default, we do not retry tests
   val DEFAULT_RETRIES: Int? = null

   // by default, tests are retried immediately
   val defaultRetryDelay: Duration? = null

   const val INVOCATIONS = 1

   const val IGNORE_PRIVATE_CLASSES = false

   val LOG_LEVEL = LogLevel.Off

   val TEST_EXECUTION_MODE = TestExecutionMode.Sequential

   val SPEC_EXECUTION_MODE = SpecExecutionMode.Sequential

   val ASSERTION_MODE = AssertionMode.None

   val TEST_CASE_ORDER = TestCaseOrder.Sequential

   val ISOLATION_MODE = IsolationMode.SingleInstance

   val DUPLICATE_TEST_NAME_MODE = DuplicateTestNameMode.Warn

   const val DISPLAY_FULL_TEST_PATH = false

   val TEST_CASE_SEVERITY_LEVEL = TestCaseSeverityLevel.NORMAL

   const val COROUTINE_DEBUG_PROBES = false

   const val COROUTINE_TEST_SCOPE = false

   const val BLOCKING_TEST = false

   const val DISPLAY_SPEC_IF_NO_ACTIVE_TESTS = true

   const val FAIL_ON_EMPTY_TEST_SUITE = false

   const val TAG_INHERITANCE = false

   const val SPEC_FAILURE_FILE_PATH = "./.kotest/spec_failures"

   val DEFAULT_TIMEOUT = 10.minutes

   val DEFAULT_INVOCATION_TIMEOUT_MILLIS = 10.minutes

   const val FAIL_ON_IGNORED_TESTS = false

   const val TEST_NAME_APPEND_TAGS = false

   const val PROJECT_WIDE_FAIL_FAST = false

   const val FAILFAST = false

   val DEFAULT_INCLUDE_TEST_SCOPE_AFFIXES = IncludeTestScopeAffixes.STYLE_DEFAULT

   const val WRITE_SPEC_FAILURE_FILE = false

   const val GLOBAL_ASSERT_SOFTLY = false

   val TEST_NAME_CASE = TestNameCase.AsIs

   val SPEC_EXECUTION_ORDER = SpecExecutionOrder.Lexicographic

   const val ALLOW_OUT_OF_ORDER_CALLBACKS = false
}

enum class IncludeTestScopeAffixes {
   STYLE_DEFAULT,
   NEVER,
   ALWAYS,
}
