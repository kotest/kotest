package io.kotest.core.spec.style

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestResult
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

typealias Test = AnnotationSpec.Test

abstract class AnnotationSpec : Spec() {

   // we need to track instances because we have to create instances of nested classes
   private val instances = mutableMapOf<KClass<*>, AnnotationSpec>()

   init {
      instances[this::class] = this
   }

   override suspend fun beforeSpec(spec: Spec) {
      executeBeforeSpecFunctions()
   }

   private suspend fun executeBeforeSpecFunctions() = this::class.findBeforeSpecFunctions().forEach {
      if (it.isSuspend) it.callSuspend(this) else it.call(this)
   }

   override suspend fun beforeTest(testCase: TestCase) {
      executeBeforeTestFunctions()
   }

   private suspend fun executeBeforeTestFunctions() = this::class.findBeforeTestFunctions().forEach {
      if (it.isSuspend) it.callSuspend(this) else it.call(this)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      executeAfterTestFunctions()
   }

   private suspend fun executeAfterTestFunctions() = this::class.findAfterTestFunctions().forEach {
      if (it.isSuspend) it.callSuspend(this) else it.call(this)
   }

   override suspend fun afterSpec(spec: Spec) {
      executeAfterSpecFunctions()
   }

   private suspend fun executeAfterSpecFunctions() = this::class.findAfterSpecFunctions().forEach {
      if (it.isSuspend) it.callSuspend(this) else it.call(this)
   }

   private fun KFunction<*>.toIgnoredRootTest(spec: AnnotationSpec): RootTest {
      return deriveRootTest(true, spec)
   }

   private fun KFunction<*>.toEnabledRootTest(spec: AnnotationSpec): RootTest {
      return deriveRootTest(false, spec)
   }

   private fun KFunction<*>.deriveRootTest(disabled: Boolean, spec: Spec): RootTest {
      val test = if (this.isExpectingException()) {
         val expected = this.getExpectedException()
         createTestFnExceptingException(expected, spec)
      } else {
         createTestFunctionNotExpectingException(spec)
      }
      return RootTest(
         name = TestNameBuilder.builder(name).build(),
         test = test,
         source = sourceRef(),
         type = TestType.Test,
         config = null,
         xmethod = if (disabled) TestXMethod.DISABLED else TestXMethod.NONE,
         factoryId = null,
      )
   }

   override fun rootTests(): List<RootTest> {
      val tests = this::class.findRootTests()
      val nested = this::class.findNestedTests()
      return tests + nested
   }

   private fun KFunction<*>.isExpectingException(): Boolean {
      return annotations.filterIsInstance<Test>().first().expected != Test.None::class
   }

   private fun KFunction<*>.getExpectedException(): KClass<out Throwable> {
      return annotations.filterIsInstance<Test>().first().expected
   }

   private fun KClass<*>.findRootTests(): List<RootTest> {
      val spec = instances.getOrPut(this) { this.java.constructors.first().newInstance() as AnnotationSpec }
      return findTestFunctions().map { f ->
         f.isAccessible = true
         if (f.isIgnoredTest()) {
            f.toIgnoredRootTest(spec)
         } else {
            f.toEnabledRootTest(spec)
         }
      }
   }

   private fun KClass<out AnnotationSpec>.findNestedTests(): List<RootTest> {
      return nestedClasses
         .filter { kclass -> kclass.annotations.map { it.annotationClass }.contains(Nested::class) }
         .flatMap { it.findRootTests() }
   }

   private fun KFunction<*>.createTestFnExceptingException(
      expected: KClass<out Throwable>,
      spec: Spec,
   ): suspend TestScope.() -> Unit {
      return {
         val thrown = try {
            callSuspend(spec)
            null
         } catch (t: Throwable) {
            t.unwrapIfReflectionCall()
         } ?: failNoExceptionThrown(expected)

         if (thrown::class != expected) failWrongExceptionThrown(expected, thrown)
      }
   }

   private fun KFunction<*>.createTestFunctionNotExpectingException(
      spec: Spec
   ): suspend TestScope.() -> Unit {
      return {
         try {
            callSuspend(spec)
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
    * This can be used in AnnotationSpec to mark a function to be executed after each test by Kotest Engine.
    * @see AfterAll
    * @see BeforeEach
    */
   annotation class AfterEach

   /**
    * Marks a function to be executed after each test
    *
    * This can be used in AnnotationSpec to mark a function to be executed after each test by Kotest Engine.
    * @see AfterClass
    * @see Before
    */
   annotation class After


   /**
    * Marks a function to be executed after each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed after a spec by Kotest Engine.
    * @see AfterEach
    * @see BeforeAll
    */
   annotation class AfterAll

   /**
    * Marks a function to be executed after each spec
    *
    * This can be used in AnnotationSpec to mark a function to be executed after a spec by Kotest Engine.
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
      object None : Throwable() {
         private fun readResolve(): Any = None
      }
   }

   /**
    * Marks a Test to be ignored
    *
    * This can be used in AnnotationSpec to mark a Test as Ignored.
    */
   annotation class Ignore

   annotation class Nested

}

internal fun KClass<out AnnotationSpec>.findBeforeTestFunctions() =
   findMethodsAnnotatedWithAnyOf(AnnotationSpec.BeforeEach::class, AnnotationSpec.Before::class)

internal fun KClass<out AnnotationSpec>.findBeforeSpecFunctions() =
   findMethodsAnnotatedWithAnyOf(AnnotationSpec.BeforeAll::class, AnnotationSpec.BeforeClass::class)

internal fun KClass<out AnnotationSpec>.findAfterSpecFunctions() =
   findMethodsAnnotatedWithAnyOf(AnnotationSpec.AfterAll::class, AnnotationSpec.AfterClass::class)

internal fun KClass<out AnnotationSpec>.findAfterTestFunctions() =
   findMethodsAnnotatedWithAnyOf(AnnotationSpec.AfterEach::class, AnnotationSpec.After::class)

internal fun KClass<*>.findTestFunctions(): List<KFunction<*>> =
   findMethodsAnnotatedWithAnyOf(AnnotationSpec.Test::class)

internal fun KFunction<*>.isIgnoredTest() = isFunctionAnnotatedWithAnyOf(AnnotationSpec.Ignore::class)

internal fun KClass<*>.findMethodsAnnotatedWithAnyOf(vararg annotation: KClass<*>): List<KFunction<*>> =
   memberFunctions.filter { it.isFunctionAnnotatedWithAnyOf(*annotation) }

internal fun KFunction<*>.isFunctionAnnotatedWithAnyOf(vararg annotation: KClass<*>) =
   annotations.any { it.annotationClass in annotation }

