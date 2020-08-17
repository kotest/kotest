package io.kotest.core.spec.style

import io.kotest.core.config.configuration
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.createRootTestCase
import io.kotest.core.test.createTestName
import io.kotest.mpp.unwrapIfReflectionCall
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

typealias Test = AnnotationSpec.Test

abstract class AnnotationSpec : Spec() {

   private fun defaultConfig() = defaultTestConfig ?: defaultTestCaseConfig() ?: configuration.defaultTestConfig

   override fun addTest(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = TODO()

   override fun beforeSpec(spec: Spec) {
      executeBeforeSpecFunctions()
   }

   private fun executeBeforeSpecFunctions() = this::class.findBeforeSpecFunctions().forEach { it.call(this) }

   override fun beforeTest(testCase: TestCase) {
      executeBeforeTestFunctions()
   }

   private fun executeBeforeTestFunctions() = this::class.findBeforeTestFunctions().forEach { it.call(this) }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      executeAfterTestFunctions()
   }

   private fun executeAfterTestFunctions() = this::class.findAfterTestFunctions().forEach { it.call(this) }

   override fun afterSpec(spec: Spec) {
      executeAfterSpecFunctions()
   }

   private fun executeAfterSpecFunctions() = this::class.findAfterSpecFunctions().forEach { it.call(this) }

   private fun KFunction<*>.toIgnoredTestCase(): TestCase {
      return deriveTestCase(defaultConfig().copy(enabled = false))
   }

   private fun KFunction<*>.toEnabledTestCase(): TestCase {
      return deriveTestCase(defaultConfig())
   }

   private fun KFunction<*>.deriveTestCase(config: TestCaseConfig): TestCase {
      return if (this.isExpectingException()) {
         val expected = this.getExpectedException()
         createRootTestCase(
            this@AnnotationSpec,
            createTestName(name),
            callExpectingException(expected),
            config,
            TestType.Test,
         )
      } else {
         createRootTestCase(
            this@AnnotationSpec,
            createTestName(name),
            callNotExpectingException(),
            config,
            TestType.Test,
         )
      }
   }

   override fun materializeRootTests(): List<RootTest> {
      return this::class.findTestFunctions().withIndex().map { (index, f) ->
         f.isAccessible = true
         if (f.isIgnoredTest()) {
            RootTest(f.toIgnoredTestCase(), index)
         } else {
            RootTest(f.toEnabledTestCase(), index)
         }
      }
   }

   private fun KFunction<*>.isExpectingException(): Boolean {
      return annotations.filterIsInstance<Test>().first().expected != Test.None::class
   }

   private fun KFunction<*>.getExpectedException(): KClass<out Throwable> {
      return annotations.filterIsInstance<Test>().first().expected
   }

   private fun KFunction<*>.callExpectingException(expected: KClass<out Throwable>): suspend TestContext.() -> Unit {
      return {
         val thrown = try {
            callSuspend(this@AnnotationSpec)
            null
         } catch (t: Throwable) {
            t.unwrapIfReflectionCall()
         } ?: failNoExceptionThrown(expected)

         if (thrown::class != expected) failWrongExceptionThrown(expected, thrown)
      }
   }

   private fun KFunction<*>.callNotExpectingException(): suspend TestContext.() -> Unit {
      return {
         try {
            callSuspend(this@AnnotationSpec)
         } catch (t: Throwable) {
            throw t.unwrapIfReflectionCall()
         }
      }
   }

   private fun failNoExceptionThrown(expected: KClass<out Throwable>): Nothing {
      throw AssertionError("Expected exception of class ${expected.simpleName}, but no exception was thrown.")
   }

   private fun failWrongExceptionThrown(expected: KClass<out Throwable>, thrown: Throwable): Nothing {
      throw AssertionError("Expected exception of class ${expected.simpleName}, but ${thrown::class.simpleName} was thrown instead.")
   }


   // All annotations should be kept inside this class, to avoid any usage outside of AnnotationSpec.
   // One can only use annotations to execute code inside AnnotationSpec.

   /**
    * Marks a function to be executed before each test
    *
    * This can be used in AnnotationSpec to mark a function to be executed before every test by Kotest Engine
    * @see BeforeAll
    * @see AfterEach
    */
   annotation class BeforeEach

   /**
    * Marks a function to be executed before each test
    *
    * This can be used in AnnotationSpec to mark a function to be executed before every test by Kotest Engine
    * @see BeforeClass
    * @see After
    */
   annotation class Before

   /**
    * Marks a function to be executed before each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a spec by Kotest Engine.
    * @see BeforeEach
    * @see AfterAll
    */
   annotation class BeforeAll

   /**
    * Marks a function to be executed before each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a spec by Kotest Engine.
    * @see Before
    * @see AfterClass
    */
   annotation class BeforeClass

   /**
    * Marks a function to be executed after each test
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a test by Kotest Engine.
    * @see AfterAll
    * @see BeforeEach
    */
   annotation class AfterEach

   /**
    * Marks a function to be executed after each test
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a test by Kotest Engine.
    * @see AfterClass
    * @see Before
    */
   annotation class After


   /**
    * Marks a function to be executed after each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a spec by Kotest Engine.
    * @see AfterEach
    * @see BeforeAll
    */
   annotation class AfterAll

   /**
    * Marks a function to be executed after each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed before a spec by Kotest Engine.
    * @see After
    * @see BeforeClass
    */
   annotation class AfterClass

   /**
    * Marks a function to be executed as a Test
    *
    * This can be used in AnnotationSpec to mark a function to be executed as a test by Kotest Engine.
    *
    *
    * [expected] can be used to mark a test to expect a specific exception.
    *
    * This is useful when moving from JUnit, in which you use expected to verify for an exception.
    *
    * ```
    *  @Test(expected = FooException::class)
    *  fun foo() {
    *    throw FooException()  // Pass
    *  }
    *
    *  @Test(expected = FooException::class
    *  fun bar() {
    *    throw BarException() // Fails, FooException was expected
    *  }
    * ```
    */
   annotation class Test(val expected: KClass<out Throwable> = None::class) {
      object None : Throwable()
   }

   /**
    * Marks a Test to be ignored
    *
    * This can be used in AnnotationSpec to mark a Test as Ignored.
    */
   annotation class Ignore

}

fun KClass<out AnnotationSpec>.findBeforeTestFunctions() =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.BeforeEach::class, AnnotationSpec.Before::class)

fun KClass<out AnnotationSpec>.findBeforeSpecFunctions() =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.BeforeAll::class, AnnotationSpec.BeforeClass::class)

fun KClass<out AnnotationSpec>.findAfterSpecFunctions() =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.AfterAll::class, AnnotationSpec.AfterClass::class)

fun KClass<out AnnotationSpec>.findAfterTestFunctions() =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.AfterEach::class, AnnotationSpec.After::class)

fun KClass<out AnnotationSpec>.findTestFunctions(): List<KFunction<*>> =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.Test::class)

fun KFunction<*>.isIgnoredTest() = isFunctionAnnotatedWithAnyOf(AnnotationSpec.Ignore::class)

private fun KClass<out AnnotationSpec>.findFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
   memberFunctions.filter { it.isFunctionAnnotatedWithAnyOf(*annotation) }

private fun KFunction<*>.isFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
   annotations.any { it.annotationClass in annotation }
