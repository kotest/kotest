package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Events

fun Events.shouldHaveNames(vararg names: String) =
   list().map { it.testDescriptor.displayName }.shouldContainExactly(*names)

fun Events.shouldBeEmpty() = list().shouldBeEmpty()

@EnabledIf(LinuxOnlyGithubCondition::class)
class StringSpecEngineKitTest : FunSpec({

   test("verify all events") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecTestCase::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecTestCase",
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
               "StringSpecTestCase",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "StringSpecTestCase",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInInit::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 7
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInInit",
               "SpecInstantiationException",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "SpecInstantiationException",
               "StringSpecExceptionInInit"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "SpecInstantiationException",
               "StringSpecExceptionInInit",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            dynamicallyRegistered().shouldHaveNames(
               "SpecInstantiationException",
            )
         }
   }

   test("exception in beforeSpec override") {

      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInBeforeSpecOverride::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 12L
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInBeforeSpecOverride",
               "a failing test",
               "Before Spec Error",
            )
            skipped().shouldHaveNames(
               "a passing test",
            )
            failed().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecOverride",
            )
            succeeded().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecOverride",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInBeforeSpecFunction::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInBeforeSpecFunction",
               "a failing test",
               "Before Spec Error",
            )
            skipped().shouldHaveNames(
               "a passing test",
            )
            failed().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecFunction",
            )
            succeeded().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecFunction",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInAfterSpec::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 13
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInAfterSpec",
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "After Spec Error",
               "StringSpecExceptionInAfterSpec",
            )
            succeeded().shouldHaveNames(
               "a passing test",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
               "StringSpecExceptionInAfterSpec",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInAfterSpecFunction::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 13
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInAfterSpecFunction",
               "a failing test",
               "a passing test",
               "After Spec Error",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "After Spec Error",
               "StringSpecExceptionInAfterSpecFunction",
            )
            succeeded().shouldHaveNames(
               "a passing test",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "After Spec Error",
               "StringSpecExceptionInAfterSpecFunction",
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInBeforeTest::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInBeforeTest",
               "a failing test",
               "a passing test",
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "StringSpecExceptionInBeforeTest",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "StringSpecExceptionInBeforeTest",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInBeforeTestFunction::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInBeforeTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "StringSpecExceptionInBeforeTestFunction",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "StringSpecExceptionInBeforeTestFunction",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         "StringSpecExceptionInInitializerErrorInBeforeTestFunction"

      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInInitializerErrorInBeforeTestFunction::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               fullyQualifiedTestClassName,
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInAfterTest::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInAfterTest",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "StringSpecExceptionInAfterTest",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "StringSpecExceptionInAfterTest",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInAfterTestFunction::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            count() shouldBe 10
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInAfterTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
            )
            succeeded().shouldHaveNames(
               "StringSpecExceptionInAfterTestFunction",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "StringSpecExceptionInAfterTestFunction",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in beforeSpec with isolation mode") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(StringSpecExceptionInBeforeSpecForInstancePerRoot::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "StringSpecExceptionInBeforeSpecForInstancePerRoot",
               "a failing test",
               "a passing test",
               "Before Spec Error"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecForInstancePerRoot",
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "Before Spec Error",
               "StringSpecExceptionInBeforeSpecForInstancePerRoot",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
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

private class
StringSpecExceptionInBeforeSpecForInstancePerRoot : StringSpec({
   "a failing test" {
      1 shouldBe 2
   }

   "a passing test" {
      1 shouldBe 1
   }
}) {
   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   override suspend fun beforeSpec(spec: Spec) {
      throw RuntimeException("zopp!!")
   }
}
