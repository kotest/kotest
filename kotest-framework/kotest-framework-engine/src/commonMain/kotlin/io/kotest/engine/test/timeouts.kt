package io.kotest.engine.test

import io.kotest.core.test.TestCase
import kotlin.time.Duration
import kotlin.time.milliseconds

internal fun resolvedTimeout(testCase: TestCase, defaultTimeout: Duration): Duration =
   testCase.config.timeout
      ?: testCase.spec.timeout?.milliseconds
      ?: testCase.spec.timeout()?.milliseconds
      ?: defaultTimeout

internal fun resolvedInvocationTimeout(testCase: TestCase, defaultTimeout: Duration): Duration =
   testCase.config.invocationTimeout
      ?: testCase.spec.invocationTimeout()?.milliseconds
      ?: testCase.spec.invocationTimeout?.milliseconds
      ?: defaultTimeout
