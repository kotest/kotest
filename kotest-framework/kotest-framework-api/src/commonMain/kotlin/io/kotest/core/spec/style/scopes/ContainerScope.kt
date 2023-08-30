package io.kotest.core.spec.style.scopes

import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.TestName
import io.kotest.core.project.projectContext
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.AfterAny
import io.kotest.core.spec.AfterContainer
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeAny
import io.kotest.core.spec.BeforeContainer
import io.kotest.core.spec.BeforeEach
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.InvalidDslException
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.UnresolvedTestConfig
import kotlin.coroutines.CoroutineContext

@Deprecated("Renamed to ContainerScope in 5.0")
typealias ContainerContext = ContainerScope

private val outOfOrderCallbacksException =
   InvalidDslException("Cannot use afterTest after a test has been defined. To disable this behavior set the global configuration value allowOutOfOrderCallbacks to true")

/**
 * Extends a [TestScope] with convenience methods for registering tests and listeners.
 */
@KotestTestScope
interface ContainerScope : TestScope {

   /**
    * Returns true if this scope has at least one registered child.
    */
   fun hasChildren(): Boolean

   suspend fun registerTest(
      name: TestName,
      disabled: Boolean,
      config: UnresolvedTestConfig?,
      type: TestType,
      test: suspend TestScope.() -> Unit,
   ) {
      registerTestCase(
         NestedTest(
            name = name,
            disabled = disabled,
            config = config,
            test = test,
            type = type,
            source = sourceRef(),
         )
      )
   }

   suspend fun registerContainer(
      name: TestName,
      disabled: Boolean,
      config: UnresolvedTestConfig?,
      test: suspend TestScope.() -> Unit,
   ) {
      registerTest(name, disabled, config, TestType.Container, test)
   }

   suspend fun registerTest(
      name: TestName,
      disabled: Boolean,
      config: UnresolvedTestConfig?,
      test: suspend TestScope.() -> Unit,
   ) {
      registerTest(name, disabled, config, TestType.Test, test)
   }

   private fun prependExtension(listener: TestListener) {
      testCase.spec.prependExtensions(listOf(listener))
   }

   private fun appendExtension(listener: TestListener) {
      testCase.spec.register(listener)
   }

   /**
    * Registers a [BeforeTest] function that executes before every test with any [TestType] in this scope.
    * Only affects tests registered after a call to this function.
    */
   fun beforeTest(f: BeforeTest) {
      beforeAny(f)
   }

   /**
    * Registers an [AfterTest] function that executes after every test with any [TestType] in this scope.
    * Only affects tests registered after a call to this function.
    */
   fun afterTest(f: AfterTest) {
      afterAny(f)
   }

   /**
    * Registers a [BeforeAny] function that executes before every test with any [TestType] in this scope.
    */
   fun beforeAny(f: BeforeAny) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      appendExtension(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterAny] function that executes after every test with any [TestType] in this scope.
    *
    * After-any callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterAny(f: AfterAny) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      prependExtension(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a [BeforeContainer] function that executes before every test with
    * type [TestType.Container] in this context.
    *
    * Only affects test containers registered after a call to this function.
    */
   fun beforeContainer(f: BeforeContainer) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      appendExtension(object : TestListener {
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
    * Only affects test containers registered after a call to this function.
    *
    * After-container callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterContainer(f: AfterContainer) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      prependExtension(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeEach] function that executes before every test with type [TestType.Test] in this scope.
    * Only applies to tests registered after this callback is added.
    */
   fun beforeEach(f: BeforeEach) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      appendExtension(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterEach] function that executes after every test with type [TestType.Test] in this scope.
    * In other words, this callback is only invoked for outer or leaf test blocks.
    *
    * Only applies to tests registered after this callback is added.
    *
    * After-each callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterEach(f: AfterEach) {
      if (hasChildren() && !projectContext.configuration.allowOutOfOrderCallbacks) throw outOfOrderCallbacksException
      val thisTestCase = this.testCase
      prependExtension(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            if (thisTestCase.descriptor.isAncestorOf(testCase.descriptor)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }
}

@KotestTestScope
open class AbstractContainerScope(private val testScope: TestScope) : ContainerScope {

   private var registered = false
   override val testCase: TestCase = testScope.testCase

   override val coroutineContext: CoroutineContext = testScope.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) {
      registered = true
      testScope.registerTestCase(nested)
   }

   override fun hasChildren(): Boolean = registered
}
