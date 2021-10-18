package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class AnnotationSpecTest : DescribeSpec({

   describe("An AnnotationSpec") {

      it("should detect public and private methods annotated with @Test") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecClass::class).launch()
         listener.tests.shouldHaveSize(2)
      }

      it("should detect nested classes") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithNested::class).launch()
         listener.tests.shouldHaveSize(3)
      }

      it("should support throwing exceptions with @Test(expected=foo)") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test1")]?.status shouldBe TestStatus.Success
      }

      it("should fail on unexpected exception") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test2")]?.status shouldBe TestStatus.Failure
      }

      it("should fail on expected exception that wasn't thrown") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithExceptions::class).launch()
         val ds = listener.tests.mapKeys { it.key.descriptor.id }
         ds[DescriptorId("test3")]?.status shouldBe TestStatus.Failure
      }
   }
})

private class AnnotationSpecClass : AnnotationSpec() {

   @Test
   fun myTest() {
   }

   @Test
   private fun test2() {
   }

}

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
