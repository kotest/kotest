package io.kotest.core.spec.style.scopes

import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.AfterAny
import io.kotest.core.spec.AfterContainer
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeAny
import io.kotest.core.spec.BeforeContainer
import io.kotest.core.spec.BeforeEach
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.UnresolvedTestConfig
import kotlin.coroutines.CoroutineContext

@Deprecated("Use ContainerContext. Deprecated since 4.5.")
typealias ContainerScope = ContainerContext

/**
 * Extends a [TestContext] with convenience methods for registering tests and listeners.
 */
@KotestDsl
interface ContainerContext : TestContext {

   suspend fun registerContainer(
      name: TestName,
      disabled: Boolean,
      config: UnresolvedTestConfig?,
      test: suspend TestContext.() -> Unit,
   ) {
      registerTestCase(
         NestedTest(
            name = name,
            disabled = disabled,
            config = config,
            test = test,
            type = TestType.Container,
            source = sourceRef(),
         )
      )
   }

   suspend fun registerTest(
      name: TestName,
      disabled: Boolean,
      config: UnresolvedTestConfig?,
      test: suspend TestContext.() -> Unit,
   ) {
      registerTestCase(
         NestedTest(
            name = name,
            disabled = disabled,
            config = config,
            test = test,
            type = TestType.Test,
            source = sourceRef(),
         )
      )
   }

   private fun addListener(listener: TestListener) {
      testCase.spec.listener(listener)
   }

   /**
    * Registers a [BeforeTest] function that executes before every test in this context.
    * Only affects tests registered after a call to this function.
    */
   fun beforeTest(f: BeforeTest) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterTest] function that executes after every test in this context.
    * Only affects tests registered after a call to this function.
    */
   fun afterTest(f: AfterTest) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a [BeforeContainer] function that executes before every test with
    * type [TestType.Container] in this context.
    *
    * Only affects test conatiners registered after a call to this function.
    */
   fun beforeContainer(f: BeforeContainer) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterContainer] function that executes after every test with
    * type [TestType.Container] in this context.
    *
    * Only affects test conatiners registered after a call to this function.
    */
   fun afterContainer(f: AfterContainer) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeEach] function that executes before every test with type [TestType.Test] in this scope.
    */
   fun beforeEach(f: BeforeEach) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterEach] function that executes after every test with type [TestType.Test] in this scope.
    */
   fun afterEach(f: AfterEach) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeAny] function that executes before every test with any [TestType] in this scope.
    */
   fun beforeAny(f: BeforeAny) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterAny] function that executes after every test with any [TestType] in this scope.
    */
   fun afterAny(f: AfterAny) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(Tuple2(testCase, result))
         }
      })
   }
}

open class AbstractContainerContext(private val testContext: TestContext) : ContainerContext {
   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)
}
