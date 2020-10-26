package com.sksamuel.kotest.specs.annotation

import io.kotest.assertions.fail
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class AnnotationSpecAnnotationsTest : AnnotationSpec() {
   companion object {
      var counterBeforeAll = AtomicInteger(0)
      var counterBeforeEach = AtomicInteger(0)

      var counterAfterAll = AtomicInteger(0)
      var counterAfterEach = AtomicInteger(0)
   }

   // All annotations are repeated sometimes, to validate that all annotations are correctly read by the engine

   @BeforeAll
   fun beforeSpec1() {
      counterBeforeAll.incrementAndGet()
   }

   @BeforeClass
   fun beforeSpec2() = counterBeforeAll.incrementAndGet()

   @BeforeEach
   fun beforeTest1() {
      counterBeforeEach.incrementAndGet()
   }

   @Before
   fun beforeTest2() = counterBeforeEach.incrementAndGet()

   @AfterEach
   fun afterTest1() = counterAfterEach.incrementAndGet()

   @After
   fun afterTest2() = counterAfterEach.incrementAndGet()

   @AfterAll // You're my wonderwall
   fun afterSpec1() {
      counterAfterAll.incrementAndGet()
   }

   @AfterClass
   fun afterSpec2() {
      counterAfterAll.incrementAndGet()
   }


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

   override fun isolationMode() = IsolationMode.SingleInstance

   override fun testCaseOrder() = TestCaseOrder.Sequential

}

@AutoScan
object AssertionListener : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == AnnotationSpecAnnotationsTest::class) {

         AnnotationSpecAnnotationsTest.counterBeforeEach.get() shouldBe 6
         AnnotationSpecAnnotationsTest.counterBeforeAll.get() shouldBe 2

         AnnotationSpecAnnotationsTest.counterAfterEach.get() shouldBe 6
         AnnotationSpecAnnotationsTest.counterAfterAll.get() shouldBe 2
      }
   }
}
