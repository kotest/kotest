package io.kotest.engine.test

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase

fun resolvedTimeout(testCase: TestCase): Long =
   testCase.config.timeout?.inWholeMilliseconds
      ?: testCase.spec.timeout
      ?: testCase.spec.timeout()
      ?: configuration.timeout

fun resolvedInvocationTimeout(testCase: TestCase): Long =
   testCase.config.invocationTimeout?.inWholeMilliseconds
      ?: testCase.spec.invocationTimeout()
      ?: testCase.spec.invocationTimeout
      ?: configuration.invocationTimeout
