package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestType
import io.kotlintest.internal.unwrapIfReflectionCall
import io.kotlintest.specs.AbstractAnnotationSpec.After
import io.kotlintest.specs.AbstractAnnotationSpec.AfterAll
import io.kotlintest.specs.AbstractAnnotationSpec.AfterClass
import io.kotlintest.specs.AbstractAnnotationSpec.AfterEach
import io.kotlintest.specs.AbstractAnnotationSpec.Before
import io.kotlintest.specs.AbstractAnnotationSpec.BeforeAll
import io.kotlintest.specs.AbstractAnnotationSpec.BeforeClass
import io.kotlintest.specs.AbstractAnnotationSpec.BeforeEach
import io.kotlintest.specs.AbstractAnnotationSpec.Ignore
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions

typealias Test = AbstractAnnotationSpec.Test

abstract class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

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

  override fun testCases(): List<TestCase> {
    return this::class.findTestFunctions().map {
      if (it.isIgnoredTest()) {
        it.toIgnoredTestCase()
      } else {
        it.toEnabledTestCase()
      }
    }
  }

  private fun KFunction<*>.toIgnoredTestCase(): TestCase {
    return createTestCase(defaultTestCaseConfig.copy(enabled = false))
  }

  private fun KFunction<*>.toEnabledTestCase(): TestCase {
    return createTestCase(defaultTestCaseConfig)
  }

  private fun KFunction<*>.createTestCase(config: TestCaseConfig): TestCase {
    return if (this.isExpectingException()) {
      val expected = this.getExpectedException()
      createTestCase(name, callWhileExpectingException(expected), config, TestType.Test)
    } else {
      createTestCase(name, { callSuspend(this@AbstractAnnotationSpec) }, config, TestType.Test)
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
        callSuspend(this@AbstractAnnotationSpec)
        null
      } catch (t: Throwable) { t.unwrapIfReflectionCall() } ?: failNoExceptionThrown(expected)

      if(thrown::class != expected) failWrongExceptionThrown(expected, thrown)
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
   * This can be used in AnnotationSpec to mark a function to be executed before every test by KotlinTest Engine
   * @see BeforeAll
   * @see AfterEach
   */
  annotation class BeforeEach

  /**
   * Marks a function to be executed before each test
   *
   * This can be used in AnnotationSpec to mark a function to be executed before every test by KotlinTest Engine
   * @see BeforeClass
   * @see After
   */
  annotation class Before

  /**
   * Marks a function to be executed before each spec
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a spec by KotlinTest Engine.
   * @see BeforeEach
   * @see AfterAll
   */
  annotation class BeforeAll

  /**
   * Marks a function to be executed before each spec
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a spec by KotlinTest Engine.
   * @see Before
   * @see AfterClass
   */
  annotation class BeforeClass

  /**
   * Marks a function to be executed after each test
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a test by KotlinTest Engine.
   * @see AfterAll
   * @see BeforeEach
   */
  annotation class AfterEach

  /**
   * Marks a function to be executed after each test
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a test by KotlinTest Engine.
   * @see AfterClass
   * @see Before
   */
  annotation class After


  /**
   * Marks a function to be executed after each spec
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a spec by KotlinTest Engine.
   * @see AfterEach
   * @see BeforeAll
   */
  annotation class AfterAll

  /**
   * Marks a function to be executed after each spec
   *
   * This can be used in AnnotationSpec to mark a function to be executed before a spec by KotlinTest Engine.
   * @see After
   * @see BeforeClass
   */
  annotation class AfterClass

  /**
   * Marks a function to be executed as a Test
   *
   * This can be used in AnnotationSpec to mark a function to be executed as a test by KotlinTest Engine.
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

fun KClass<out AbstractAnnotationSpec>.findBeforeTestFunctions() =
        findFunctionAnnotatedWithAnyOf(BeforeEach::class, Before::class)

fun KClass<out AbstractAnnotationSpec>.findBeforeSpecFunctions() =
        findFunctionAnnotatedWithAnyOf( BeforeAll::class, BeforeClass::class)


fun KClass<out AbstractAnnotationSpec>.findAfterSpecFunctions() =
        findFunctionAnnotatedWithAnyOf(AfterAll::class, AfterClass::class)

fun KClass<out AbstractAnnotationSpec>.findAfterTestFunctions() =
        findFunctionAnnotatedWithAnyOf(AfterEach::class, After::class)


fun KClass<out AbstractAnnotationSpec>.findTestFunctions() =
        findFunctionAnnotatedWithAnyOf(AbstractAnnotationSpec.Test::class)


fun KFunction<*>.isIgnoredTest() = isFunctionAnnotatedWithAnyOf(Ignore::class)

private fun KClass<out AbstractAnnotationSpec>.findFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
        memberFunctions.filter { it.isFunctionAnnotatedWithAnyOf(*annotation) }


private fun KFunction<*>.isFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
        annotations.any { it.annotationClass in annotation }


@Deprecated("To be removed soon")
private object Failures {

  fun failure(message: String, cause: Throwable? = null): AssertionError = AssertionError(message).apply {
    removeKotlintestElementsFromStacktrace(this)
    initCause(cause)
  }

  fun removeKotlintestElementsFromStacktrace(throwable: Throwable) {
    throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
  }

}

private object UserStackTraceConverter {

  fun getUserStacktrace(kotlintestStacktraces: Array<StackTraceElement>): Array<StackTraceElement> {
    return kotlintestStacktraces.dropUntilUserClass()
  }

  private fun Array<StackTraceElement>.dropUntilUserClass(): Array<StackTraceElement> {
    return toList().dropUntilFirstKotlintestClass().dropUntilFirstNonKotlintestClass().toTypedArray()
  }

  private fun List<StackTraceElement>.dropUntilFirstKotlintestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isNotKotlintestClass()
    }
  }

  private fun List<StackTraceElement>.dropUntilFirstNonKotlintestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isKotlintestClass()
    }
  }

  private fun StackTraceElement.isKotlintestClass(): Boolean {
    return className.startsWith("io.kotlintest")
  }

  private fun StackTraceElement.isNotKotlintestClass(): Boolean {
    return !isKotlintestClass()
  }

}
