package io.kotest.core.spec.style

import io.kotest.core.SpecClass
import io.kotest.core.config.Project
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.*
import io.kotest.core.internal.unwrapIfReflectionCall
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions

typealias Test = AnnotationSpec.Test

abstract class AnnotationSpec(body: AnnotationSpec.() -> Unit = {}) : SpecConfiguration() {

   init {
      body()
   }

   private fun defaultConfig() = defaultTestCaseConfig ?: defaultTestCaseConfig() ?: Project.testCaseConfig()

   override fun beforeSpec(spec: SpecClass) {
      executeBeforeSpecFunctions()
   }

   override fun beforeSpec(spec: SpecConfiguration) {
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

   override fun afterSpec(spec: SpecConfiguration) {
      executeAfterSpecFunctions()
   }

   override fun afterSpec(spec: SpecClass) {
      executeAfterSpecFunctions()
   }

   private fun executeAfterSpecFunctions() = this::class.findAfterSpecFunctions().forEach { it.call(this) }

   private fun KFunction<*>.toIgnoredTestCase() {
      createTestCase(defaultConfig().copy(enabled = false))
   }

   private fun KFunction<*>.toEnabledTestCase() {
      createTestCase(defaultConfig())
   }

   private fun KFunction<*>.createTestCase(config: TestCaseConfig) {
      if (this.isExpectingException()) {
         val expected = this.getExpectedException()
         addRootTestCase(name, callWhileExpectingException(expected), config, TestType.Test)
      } else {
         addRootTestCase(name, { callSuspend(this@AnnotationSpec) }, config, TestType.Test)
      }
   }

   private fun KFunction<*>.isExpectingException(): Boolean {
      return annotations.filterIsInstance<Test>().first().expected != Test.None::class
   }

   private fun KFunction<*>.getExpectedException(): KClass<out Throwable> {
      return annotations.filterIsInstance<Test>().first().expected
   }

   private fun KFunction<*>.callWhileExpectingException(expected: KClass<out Throwable>): suspend TestContext.() -> Unit {
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

   private fun failNoExceptionThrown(expected: KClass<out Throwable>): Nothing {
      throw Failures.failure("Expected exception of class ${expected.simpleName}, but no exception was thrown.")
   }

   private fun failWrongExceptionThrown(expected: KClass<out Throwable>, thrown: Throwable): Nothing {
      throw Failures.failure("Expected exception of class ${expected.simpleName}, but ${thrown::class.simpleName} was thrown instead.")
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

fun KClass<out AnnotationSpec>.findTestFunctions() =
   findFunctionAnnotatedWithAnyOf(AnnotationSpec.Test::class)

fun KFunction<*>.isIgnoredTest() = isFunctionAnnotatedWithAnyOf(AnnotationSpec.Ignore::class)

private fun KClass<out AnnotationSpec>.findFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
   memberFunctions.filter { it.isFunctionAnnotatedWithAnyOf(*annotation) }

private fun KFunction<*>.isFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
   annotations.any { it.annotationClass in annotation }

@Deprecated("To be removed soon")
private object Failures {

   fun failure(message: String, cause: Throwable? = null): AssertionError = AssertionError(message).apply {
      removeKotestElementsFromStacktrace(this)
      initCause(cause)
   }

   fun removeKotestElementsFromStacktrace(throwable: Throwable) {
      throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
   }
}

private object UserStackTraceConverter {

   fun getUserStacktrace(kotestStacktraces: Array<StackTraceElement>): Array<StackTraceElement> {
      return kotestStacktraces.dropUntilUserClass()
   }

   private fun Array<StackTraceElement>.dropUntilUserClass(): Array<StackTraceElement> {
      return toList().dropUntilFirstKotestClass().dropUntilFirstNonKotestClass().toTypedArray()
   }

   private fun List<StackTraceElement>.dropUntilFirstKotestClass(): List<StackTraceElement> {
      return dropWhile {
         it.isNotKotestClass()
      }
   }

   private fun List<StackTraceElement>.dropUntilFirstNonKotestClass(): List<StackTraceElement> {
      return dropWhile {
         it.isKotestClass()
      }
   }

   private fun StackTraceElement.isKotestClass(): Boolean {
      return className.startsWith("io.kotest")
   }

   private fun StackTraceElement.isNotKotestClass(): Boolean {
      return !isKotestClass()
   }

}
