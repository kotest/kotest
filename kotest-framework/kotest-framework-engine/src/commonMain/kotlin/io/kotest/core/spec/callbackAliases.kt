package io.kotest.core.spec

import io.kotest.core.Tuple2
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

typealias BeforeTest = suspend (TestCase) -> Unit
typealias AfterTest = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeEach = suspend (TestCase) -> Unit
typealias AfterEach = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeContainer = suspend (TestCase) -> Unit
typealias AfterContainer = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeAny = suspend (TestCase) -> Unit
typealias BeforeInvocation = suspend (TestCase, Int) -> Unit
typealias AfterAny = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeSpec = suspend (Spec) -> Unit
typealias AfterSpec = suspend (Spec) -> Unit
typealias AfterInvocation = suspend (TestCase, Int) -> Unit
typealias AfterProject = suspend () -> Unit
typealias TestCaseExtensionFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
typealias AroundTestFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
