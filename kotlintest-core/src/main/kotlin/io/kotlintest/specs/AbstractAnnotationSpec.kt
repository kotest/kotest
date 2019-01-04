package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestType
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
import kotlin.reflect.full.memberFunctions

typealias Test = AbstractAnnotationSpec.Test

abstract class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun beforeSpec(description: Description, spec: Spec) {
    super.beforeSpec(description, spec)
    executeBeforeSpecFunctions()
  }

  private fun executeBeforeSpecFunctions() = this::class.findBeforeSpecFunctions().forEach { it.call(this) }

  override fun beforeTest(description: Description) {
    executeBeforeTestFunctions()
  }

  private fun executeBeforeTestFunctions() = this::class.findBeforeTestFunctions().forEach { it.call(this) }

  override fun afterTest(description: Description, result: TestResult) {
    executeAfterTestFunctions()
  }

  private fun executeAfterTestFunctions() = this::class.findAfterTestFunctions().forEach { it.call(this) }

  override fun afterSpec(description: Description, spec: Spec) {
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
    return this.let {
      createTestCase(it.name, { it.call(this@AbstractAnnotationSpec) }, config, TestType.Test)
    }
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
   */
  annotation class Test

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
