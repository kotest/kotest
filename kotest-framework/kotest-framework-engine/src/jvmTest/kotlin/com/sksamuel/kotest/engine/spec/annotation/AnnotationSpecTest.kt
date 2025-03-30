package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
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
