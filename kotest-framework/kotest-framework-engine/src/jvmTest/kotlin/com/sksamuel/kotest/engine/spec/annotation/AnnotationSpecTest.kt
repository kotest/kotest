package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class AnnotationSpecTest : DescribeSpec({

   describe("An AnnotationSpec") {

      it("should detect public and private methods annotated with @Test") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecClass::class).launch()
         listener.tests.shouldHaveSize(2)
      }

      it("should support throwing exceptions with @Test(expected=foo)") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test1")]?.isSuccess shouldBe true
      }

      it("should fail on unexpected exception") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test2")]?.isFailure shouldBe true
      }

      it("should fail on expected exception that wasn't thrown") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test3")]?.isFailure shouldBe true
      }

      describe("with @Nested tests") {

         it("should detect nested classes") {
            val listener = CollectingTestEngineListener()
            TestEngineLauncher(listener).withClasses(AnnotationSpecWithNested::class).launch()
            listener.tests.shouldHaveSize(2)
            val ds = listener.tests.mapKeys { it.key.descriptor.id }
            ds.shouldContainKeys(
               DescriptorId("foo"),
               DescriptorId("bar"),
            )
         }

         it("should succeed with valid tests") {
            val listener = CollectingTestEngineListener()
            TestEngineLauncher(listener).withClasses(AnnotationSpecWithNested::class).launch()
            val ds = listener.tests.mapKeys { it.key.descriptor.id }

            val fooTest = ds[DescriptorId("foo")] ?: fail("invalid test set up - no test 'foo'")
            fooTest.shouldBeSuccess()

            val barTest = ds[DescriptorId("bar")] ?: fail("invalid test set up - no test 'bar'")
            barTest.shouldBeSuccess()
         }
      }
   }
}) {
   companion object {
      private fun TestResult.shouldBeSuccess() {

         val desc = "test '${name}' " + when (this) {
            is TestResult.Error   -> "errored: $cause"
            is TestResult.Failure -> "failed: $cause"
            is TestResult.Ignored -> "ignored: $reason"
            is TestResult.Success -> "passed"
         }

         withClue(desc) {
            isSuccess shouldBe true
         }
      }
   }
}

@Suppress("unused")
private class AnnotationSpecClass : AnnotationSpec() {

   @Test
   fun myTest() {
   }

   @Test
   private fun test2() {
   }
}

@Suppress("unused")
private class AnnotationSpecWithExceptions : AnnotationSpec() {

   private class FooException : RuntimeException()
   private class BarException : RuntimeException()

   @Test(expected = FooException::class)
   fun test1() {
      throw FooException()  // This test should pass!
   }

   @Test(expected = FooException::class)
   fun test2() {
      throw BarException()
   }

   @Test(expected = FooException::class)
   fun test3() {
      // Throw nothing
   }
}

@Suppress("unused")
class AnnotationSpecWithNested : AnnotationSpec() {

   @Test
   fun foo() {
   }

   @Nested
   class MyNested : AnnotationSpec() {
      @Test
      fun bar() {
      }
   }
}
