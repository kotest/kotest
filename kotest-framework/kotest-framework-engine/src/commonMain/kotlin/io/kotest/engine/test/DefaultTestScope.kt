package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

data class DefaultTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestScope

suspend fun defaultTestScope(testCase: TestCase) = DefaultTestScope(testCase, coroutineContext)
