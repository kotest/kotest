package com.sksamuel.kotest.specs.annotation

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestCaseOrder
import io.kotest.TestResult
import io.kotest.assertions.fail
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.shouldBe
import io.kotest.specs.AnnotationSpec
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AnnotationSpecTest : AnnotationSpec() {

   private class FooException : RuntimeException()
   private class BarException : RuntimeException()

   private var count = 0

   @Test
   fun test1() {
      count += 1
   }

   @Test
   fun test2() {
      count += 1
   }

   @Test
   fun `!bangedTest`() {
      throw FooException()  // Test should pass as this should be banged
   }

   @Test(expected = FooException::class)
   fun test3() {
      throw FooException()  // This test should pass!
   }

   @Test(expected = FooException::class)
   fun test4() {
      throw BarException()
   }

   @Test(expected = FooException::class)
   fun test5() {
      // Throw nothing
   }

   override fun afterSpec(spec: Spec) {
      count shouldBe 2
   }

   override fun extensions(): List<SpecLevelExtension> = listOf(IgnoreFailedTestExtension)

   private object IgnoreFailedTestExtension : TestCaseExtension {

      override suspend fun intercept(testCase: TestCase,
                                     execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                     complete: suspend (TestResult) -> Unit) {
         if (testCase.name !in listOf("test4", "test5")) return execute(testCase, complete)

         execute(testCase) {
            if (it.error !is AssertionError) {
               complete(TestResult.failure(AssertionError("Expecting an assertion error!"), Duration.ZERO))
            }

            val errorMessage = it.error!!.message
            val wrongExceptionMessage = "Expected exception of class FooException, but BarException was thrown instead."
            val noExceptionMessage = "Expected exception of class FooException, but no exception was thrown."

            when (testCase.name) {
               "test4" -> if (errorMessage == wrongExceptionMessage) {
                  complete(TestResult.success(Duration.ZERO))
               } else {
                  complete(TestResult.failure(AssertionError("Wrong message."), Duration.ZERO))
               }
               "test5" -> if (errorMessage == noExceptionMessage) {
                  complete(TestResult.success(Duration.ZERO))
               } else {
                  complete(TestResult.failure(AssertionError("Wrong message."), Duration.ZERO))
               }
            }
         }
      }
   }
}


class AnnotationSpecAnnotationsTest : AnnotationSpec() {

  private var counterBeforeAll = AtomicInteger(0)
  private var counterBeforeEach = AtomicInteger(0)

  private var counterAfterAll = AtomicInteger(0)
  private var counterAfterEach = AtomicInteger(0)


  // All annotations are repeated sometimes, to validate that all annotations are correctly read by the engine

  @BeforeAll
  fun beforeSpec1() = counterBeforeAll.incrementAndGet()
  @BeforeClass
  fun beforeSpec2() = counterBeforeAll.incrementAndGet()


  @BeforeEach
  fun beforeTest1() = counterBeforeEach.incrementAndGet()
  @Before
  fun beforeTest2() = counterBeforeEach.incrementAndGet()

  @AfterEach
  fun afterTest1() = counterAfterEach.incrementAndGet()
  @After
  fun afterTest2() = counterAfterEach.incrementAndGet()

  @AfterAll // You're my wonderwall
  fun afterSpec1() = counterAfterAll.incrementAndGet()
  @AfterClass
  fun afterSpec2() = counterAfterAll.incrementAndGet()


  // Testing depends on method discovery happening in this order, verifying the assertions in the order tests are declared
  @Test
  fun test1() {
    counterBeforeAll.get() shouldBe 2 // Both BeforeSpec should be executed once
    counterBeforeEach.get() shouldBe 2 // Both BeforeTest should be executed once


    // No tests finished executing yet, both should be 0
    counterAfterAll.get() shouldBe 0
    counterAfterEach.get() shouldBe 0
  }

  @Test
  fun test2() {
    counterBeforeAll.get() shouldBe 2 // BeforeSpecs should not be executed again
    counterBeforeEach.get() shouldBe 4 // Before tests should be executed twice (test1 + test2)

    counterAfterAll.get() shouldBe 0  // Not all tests finished yet, it shouldn't have executed
    counterAfterEach.get() shouldBe 2 // AfterTest should be executed (after test1)
  }

  @Test
  fun test3() {
    counterBeforeAll.get() shouldBe 2
    counterBeforeEach.get() shouldBe 6

    counterAfterAll.get() shouldBe 0
    counterAfterEach.get() shouldBe 4
  }

  @Ignore
  @Test
  fun testIgnore() {
    fail("This should never execute as the test is marked with @Ignore")
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    counterAfterAll.get() shouldBe 2
    counterAfterEach.get() shouldBe 6

    counterBeforeAll.get() shouldBe 2
    counterBeforeEach.get() shouldBe 6
  }

  override fun isolationMode() = IsolationMode.SingleInstance

  override fun testCaseOrder() = TestCaseOrder.Sequential
}

@ExperimentalTime
class AnnotationSpecFailureTest : AnnotationSpec() {
   class FooException : Exception()

   private val thrownException = FooException()

   @Test
   fun foo() {
      throw thrownException
   }

   override fun extensions() = listOf(ExceptionCaptureExtension())

   inner class ExceptionCaptureExtension : TestCaseExtension {

      override suspend fun intercept(testCase: TestCase,
                                     execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                     complete: suspend (TestResult) -> Unit) {
         execute(testCase) {
            it.error shouldBe thrownException
            complete(TestResult.success(Duration.ZERO))
         }
      }

   }
}
