@file:Suppress("DuplicatedCode")

package io.kotest.extensions.spring

import io.kotest.core.Logger
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.test.context.TestContextManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * An [Extension] which adds support for testing spring components.
 *
 * This extension has two parts:
 *
 * 1. Supports creating non-zero arg test classes by delegating to spring's [TestContextManager]
 * to autowire the constructors
 *
 * 2. Adds support for spring lifecycle methods to be called before and after tests.
 */
class SpringExtension(
   private val mode: SpringTestLifecycleMode = SpringTestLifecycleMode.Test
) : ConstructorExtension, SpecExtension, TestCaseExtension, BeforeTestListener, AfterTestListener {

   private val logger = Logger(SpringExtension::class)

   // each time a TestContextManager is created, any @ContextCustomizerFactories are registered again,
   // because each test context has its own application context, so we need to ensure we share the context
   // managers across test classes
   private val managers = ConcurrentHashMap<KClass<*>, TestContextManager>()

   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec {

      val manager = getTestContextManager(clazz)
      val context = manager.testContext.applicationContext

      logger.log { Pair(clazz.simpleName, "Spring extension will try to create autowired instance") }
      return context.autowireCapableBeanFactory.autowire(
         clazz.java,
         AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true
      ) as Spec
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      SpringJavaCompatibility.checkForSafeClassName(spec::class)
      val manager = getTestContextManager(spec::class)
      withContext(SpringTestContextCoroutineContextElement(manager)) {
         testContextManager().beforeTestClass()
         testContextManager().prepareTestInstance(spec)
         execute(spec)
         testContextManager().afterTestClass()
      }
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      val methodName = SpringJavaCompatibility.methodHandle(testCase)
      val applicable = testCase.isApplicable()
      return if (applicable) {
         testContextManager().beforeTestExecution(testCase.spec, methodName)
         execute(testCase).also {
            testContextManager().afterTestExecution(testCase.spec, methodName, null)
         }
      } else {
         execute(testCase)
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (testCase.isApplicable()) {
         val methodName = SpringJavaCompatibility.methodHandle(testCase)
         testContextManager().beforeTestMethod(testCase.spec, methodName)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (testCase.isApplicable()) {
         val methodName = SpringJavaCompatibility.methodHandle(testCase)
         testContextManager().afterTestMethod(testCase.spec, methodName, null)
      }
   }

   private fun getTestContextManager(kclass: KClass<*>): TestContextManager {
      return managers.getOrPut(kclass) {
         TestContextManager(kclass.java)
      }
   }

   /**
    * Returns true if this test case should have the spring lifecycle methods applied
    */
   private fun TestCase.isApplicable() = (mode == SpringTestLifecycleMode.Root && isRootTest()) ||
      (mode == SpringTestLifecycleMode.Test && type == TestType.Test)
}
