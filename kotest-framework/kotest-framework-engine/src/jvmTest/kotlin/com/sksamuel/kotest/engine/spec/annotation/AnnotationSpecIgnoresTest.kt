package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.assertions.fail
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.maps.shouldBeEmpty

class AnnotationSpecIgnoresTest : DescribeSpec({

   describe("An AnnotationSpec") {

      it("should ignore banged tests") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithIgnoredTests::class).launch()
         listener.tests.shouldBeEmpty()
      }

      it("should ignore tests annotated with @Ignore") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithIgnoredTests::class).launch()
         listener.tests.shouldBeEmpty()
      }
   }
})

class AnnotationSpecWithIgnoredTests : AnnotationSpec() {

   @Test
   fun `!foo`() {
      fail("This should never execute as the test name starts with !")
   }

   @Ignore
   @Test
   fun bar() {
      fail("This should never execute as the test is marked with @Ignore")
   }
}
