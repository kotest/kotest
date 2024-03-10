package com.sksamuel.kotest.runner.junit5

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Events

fun Events.shouldHaveNames(vararg names: String) =
   list().map { it.testDescriptor.displayName }.shouldContainExactly(*names)

fun Events.shouldBeEmpty() = list().shouldBeEmpty()

class StringSpecEngineKitTest : FunSpec({

   beforeSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "true")
   }

   afterSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "false")
   }

   test("verify all events") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecTestCase::class.java))
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecTestCase",
               "a failing test",
               "a passing test",
               "an erroring test",
            )
            skipped().shouldHaveNames("a skipped test")
            failed().shouldHaveNames(
               "a failing test",
               "an erroring test",
            )
            succeeded().shouldHaveNames(
               "a passing test",
               "com.sksamuel.kotest.runner.junit5.StringSpecTestCase",
               "Kotest",
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "com.sksamuel.kotest.runner.junit5.StringSpecTestCase",
               "Kotest",
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "a skipped test",
            )
         }
   }

   test("exception in initializer") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInInit::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 7
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInInit",
               "SpecInstantiationException",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "SpecInstantiationException",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInInit"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "SpecInstantiationException",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInInit",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "SpecInstantiationException",
            )
         }
   }

   test("exception in beforeSpec override") {

      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeSpecOverride::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 12L
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecOverride",
               "a failing test",
               "Before Spec Error",
            )
            skipped().shouldHaveNames(
               "a passing test",
            )
            failed().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecOverride",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecOverride",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "Before Spec Error",
            )
         }
   }

   test("exception in beforeSpec function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeSpecFunction::class.java))
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecFunction",
               "a failing test",
               "Before Spec Error",
            )
            skipped().shouldHaveNames(
               "a passing test",
            )
            failed().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecFunction",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecFunction",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "Before Spec Error",
            )
         }
   }

   test("exception in afterSpec override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterSpec::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 13
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpec",
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "After Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpec",
            )
            succeeded().shouldHaveNames(
               "a passing test",
               "Kotest",
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpec",
               "Kotest",
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
         }
   }

   test("exception in afterSpec function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterSpecFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 13
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpecFunction",
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "After Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpecFunction",
            )
            succeeded().shouldHaveNames(
               "a passing test",
               "Kotest",
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterSpecFunction",
               "Kotest",
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
         }
   }

   test("exception in beforeTest override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeTest::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTest",
               "a failing test",
               "a passing test",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTest",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTest",
               "Kotest",
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in beforeTest function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeTestFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTestFunction",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeTestFunction",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("ExceptionInInitializerError exception in beforeTest") {
      val fullyQualifiedTestClassName =
         "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInInitializerErrorInBeforeTestFunction"

      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInInitializerErrorInBeforeTestFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               "Kotest",
               fullyQualifiedTestClassName,
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               fullyQualifiedTestClassName,
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               fullyQualifiedTestClassName,
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in afterTest override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterTest::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTest",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTest",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTest",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in afterTest function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterTestFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTestFunction",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInAfterTestFunction",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in beforeSpec with isolation mode instance per leaf") {
      val fullyQualifiedTestClassName =
         "com.sksamuel.kotest.runner.junit5.StringSpecExceptionInBeforeSpecForInstancePerLeaf"
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeSpecForInstancePerLeaf::class.java))
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               fullyQualifiedTestClassName,
               "a failing test",
               "Before Spec Error"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               fullyQualifiedTestClassName,
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               fullyQualifiedTestClassName,
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
            )
         }
   }
})

private class StringSpecExceptionInBeforeSpecOverride : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }
   }

   override suspend fun beforeSpec(spec: Spec) {
      throw RuntimeException("zopp!!")
   }

}

private class StringSpecExceptionInBeforeSpecFunction : StringSpec() {
   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      beforeSpec {
         throw RuntimeException("zopp!!")
      }
   }
}

private class StringSpecExceptionInAfterTest : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      throw RuntimeException("craack!!")
   }
}

private class StringSpecExceptionInAfterTestFunction : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      afterTest {
         throw RuntimeException("craack!!")
      }
   }
}

private class StringSpecExceptionInAfterSpec : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

   }

   override suspend fun afterSpec(spec: Spec) {
      throw RuntimeException("splatt!!")
   }

}

private class StringSpecExceptionInAfterSpecFunction : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      afterSpec {
         throw RuntimeException("splatt!!")
      }
   }
}

private class StringSpecExceptionInBeforeTest : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }
   }

   override suspend fun beforeTest(testCase: TestCase) {
      throw RuntimeException("oooff!!")
   }
}

private class StringSpecExceptionInBeforeTestFunction : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      beforeTest {
         throw RuntimeException("oooff!!")
      }
   }
}

private class StringSpecExceptionInInitializerErrorInBeforeTestFunction : StringSpec() {
   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      beforeTest {
         throw ExceptionInInitializerError("Unable to initialize")
      }
   }
}

private class StringSpecTestCase : StringSpec({

   "a failing test" {
      1 shouldBe 2
   }

   "a passing test" {
      1 shouldBe 1
   }

   "an erroring test" {
      throw RuntimeException()
   }

   "a skipped test".config(enabled = false) {
   }

})

private class StringSpecExceptionInInit : StringSpec({
   throw RuntimeException("kapow")
})

private class StringSpecExceptionInBeforeSpecForInstancePerLeaf : StringSpec({
   "a failing test" {
      1 shouldBe 2
   }

   "a passing test" {
      1 shouldBe 1
   }
}) {
   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

   override suspend fun beforeSpec(spec: Spec) {
      throw RuntimeException("zopp!!")
   }
}
