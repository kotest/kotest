package io.kotest.core.spec

import io.kotest.core.Tuple2
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

typealias BeforeTest = suspend (TestCase) -> Unit
typealias AfterTest = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeEach = suspend (TestCase) -> Unit
typealias AfterEach = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeContainer = suspend (TestCase) -> Unit
typealias AfterContainer = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeAny = suspend (TestCase) -> Unit
typealias AfterAny = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeSpec = suspend (Spec) -> Unit
typealias AfterSpec = suspend (Spec) -> Unit
typealias AfterProject = () -> Unit
typealias PrepareSpec = suspend (KClass<out Spec>) -> Unit
typealias FinalizeSpec = suspend (Tuple2<KClass<out Spec>, Map<TestCase, TestResult>>) -> Unit
typealias TestCaseExtensionFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
typealias AroundTestFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
typealias AroundSpecFn = suspend (Tuple2<KClass<out Spec>, suspend () -> Unit>) -> Unit
