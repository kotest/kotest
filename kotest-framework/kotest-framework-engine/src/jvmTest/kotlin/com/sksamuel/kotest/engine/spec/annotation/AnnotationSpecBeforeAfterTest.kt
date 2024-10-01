package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

@ApplyExtension(AssertionListener::class)
@EnabledIf(LinuxCondition::class)
class AnnotationSpecBeforeAfterTest : AnnotationSpec() {

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

   @Before
   suspend fun beforeSuspendTest() {
      delay(10)
      counterBeforeEach.incrementAndGet()
   }

   @AfterEach
   fun afterTest1() = counterAfterEach.incrementAndGet()

   @After
   fun afterTest2() = counterAfterEach.incrementAndGet()

   @After
   suspend fun afterSuspendTest() {
      delay(10)
      counterAfterEach.incrementAndGet()
   }

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
      counterBeforeEach.get() shouldBe 3 // All 3 BeforeTest should be executed once


      // No tests finished executing yet, both should be 0
      counterAfterAll.get() shouldBe 0
      counterAfterEach.get() shouldBe 0
   }

   @Test
   fun test2() {
      counterBeforeAll.get() shouldBe 2 // BeforeSpecs should not be executed again
      counterBeforeEach.get() shouldBe 6 // Before tests should be executed twice (test1 + test2)

      counterAfterAll.get() shouldBe 0  // Not all tests finished yet, it shouldn't have executed
      counterAfterEach.get() shouldBe 3 // AfterTest should be executed (after test1)
   }

   @Test
   fun test3() {
      counterBeforeAll.get() shouldBe 2
      counterBeforeEach.get() shouldBe 9

      counterAfterAll.get() shouldBe 0
      counterAfterEach.get() shouldBe 6 // three sets of after test executed for test1/test2
   }

   override fun isolationMode() = IsolationMode.SingleInstance

   override fun testCaseOrder() = TestCaseOrder.Sequential

}

object AssertionListener : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == AnnotationSpecBeforeAfterTest::class) {

         AnnotationSpecBeforeAfterTest.counterBeforeEach.get() shouldBe 6
         AnnotationSpecBeforeAfterTest.counterBeforeAll.get() shouldBe 2

         AnnotationSpecBeforeAfterTest.counterAfterEach.get() shouldBe 6
         AnnotationSpecBeforeAfterTest.counterAfterAll.get() shouldBe 2
      }
   }
}
