package com.sksamuel.kotest.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Events

fun Events.shouldHaveNames(vararg names: String) =
   list().map { it.testDescriptor.displayName }.shouldContainExactly(*names)

fun Events.shouldBeEmpty() = list().shouldBeEmpty()

class StringSpecEngineKitTest : FunSpec({

   test("verify all events") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecTestCase::class.java))
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecTestCase",
               "a failing test",
               "a passing test",
               "an erroring test"
            )
            skipped().shouldHaveNames("a skipped test")
            failed().shouldHaveNames(
               "a failing test",
               "an erroring test",
               "com.sksamuel.kotest.junit5.StringSpecTestCase"
            )
            succeeded().shouldHaveNames("a passing test", "Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "com.sksamuel.kotest.junit5.StringSpecTestCase",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecTestCase",
               "a failing test",
               "a passing test",
               "an erroring test",
               "a skipped test"
            )
         }
   }

   test("exception in initializer") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInInit::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 8
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInInit",
               "Spec instantiation failed"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInInit")
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "Spec instantiation failed",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInInit",
               "Kotest"
            )
            aborted().shouldHaveNames("Spec instantiation failed")
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInInit",
               "Spec instantiation failed"
            )
         }
   }

   test("exception in beforeSpec override") {

      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeSpecOverride::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 5
            started().shouldHaveNames("Kotest", "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecOverride")
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecOverride")
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecOverride", "Kotest")
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecOverride")
         }
   }

   test("exception in beforeSpec function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeSpecFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 5
            started().shouldHaveNames("Kotest", "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecFunction")
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecFunction")
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecFunction", "Kotest")
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames("com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeSpecFunction")
         }
   }

   test("exception in afterSpec override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterSpec::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpec",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("a failing test", "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpec")
            succeeded().shouldHaveNames("a passing test", "Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpec",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpec",
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in afterSpec function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterSpecFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpecFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpecFunction"
            )
            succeeded().shouldHaveNames("a passing test", "Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpecFunction",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterSpecFunction",
               "a failing test",
               "a passing test"
            )
         }
   }

   test("exception in beforeTest override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeTest::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTest",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTest"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTest",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTest", "a failing test", "a passing test"
            )
         }
   }

   test("exception in beforeTest function") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInBeforeTestFunction::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTestFunction"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTestFunction",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInBeforeTestFunction", "a failing test", "a passing test"
            )
         }
   }

   test("exception in afterTest override") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(StringSpecExceptionInAfterTest::class.java))
         .execute()
         .allEvents().apply {
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTest",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTest"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTest",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTest",
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
            count() shouldBe 11
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTestFunction",
               "a failing test",
               "a passing test"
            )
            skipped().shouldBeEmpty()
            failed().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTestFunction"
            )
            succeeded().shouldHaveNames("Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTestFunction",
               "Kotest"
            )
            aborted().shouldBeEmpty()
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.junit5.StringSpecExceptionInAfterTestFunction",
               "a failing test",
               "a passing test"
            )
         }
   }

})
