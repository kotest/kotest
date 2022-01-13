package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.assertions.fail
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class AnnotationSpecIgnoresTest : DescribeSpec({
   describe("An AnnotationSpec") {
      it("should ignore banged tests") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecWithBangTest::class).launch()
         listener.tests
            .filterNot {  it.value.isSuccess }
            .mapKeys { it.key.name.testName }
            .mapValues { it.value.reasonOrNull } shouldBe mapOf("foo" to "Disabled by bang")
      }

      it("should ignore tests annotated with @Ignore") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener).withClasses(AnnotationSpecAtIgnoreTest::class).launch()
         listener.tests
            .filterNot {  it.value.isSuccess }
            .mapKeys { it.key.name.testName }
            .mapValues { it.value.reasonOrNull } shouldBe mapOf("bar" to "Disabled by xmethod")
      }
   }
})

class AnnotationSpecWithBangTest : AnnotationSpec() {
   @Test
   fun pass() { 1 shouldBe 1 }

   @Test
   fun `!foo`() {
      fail("This should never execute as the test name starts with !")
   }
}

class AnnotationSpecAtIgnoreTest : AnnotationSpec() {
   @Test
   fun pass() { 1 shouldBe 1 }

   @Ignore
   @Test
   fun bar() {
      fail("This should never execute as the test is marked with @Ignore")
   }
}
