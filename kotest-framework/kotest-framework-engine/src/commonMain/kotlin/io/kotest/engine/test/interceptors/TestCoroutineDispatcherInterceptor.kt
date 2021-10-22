package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase

@ExperimentalKotest
fun TestCase.isTestCoroutineDispatcher(configuration: Configuration): Boolean =
   config.testCoroutineDispatcher ?: spec.testCoroutineDispatcher ?: configuration.testCoroutineDispatcher

expect class TestCoroutineDispatcherInterceptor() : TestExecutionInterceptor
