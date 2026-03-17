package com.sksamuel.kotest.runner.junit4

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.KotestTestRunner
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import kotlin.random.Random

class KotestTestRunnerTest : FunSpec({
   test("should use same thread for all events") {
      val threads = mutableSetOf<String>()
      val listener = RunNotifier()
      listener.addListener(object : RunListener() {
         override fun testStarted(description: Description?) {
            threads.add(Thread.currentThread().id.toString())
         }

         override fun testFinished(description: Description?) {
            threads.add(Thread.currentThread().id.toString())
         }
      })
      KotestTestRunner(DummySpec::class.java).run(listener)
      threads.shouldHaveSize(1)
   }

   test("should apply @Rule annotated MethodRule before and after each test") {
      RuleTracker.invocations.clear()
      KotestTestRunner(SpecWithTrackedMethodRule::class.java).run(RunNotifier())
      // MethodRule receives a synthetic FrameworkMethod wrapping Object.toString(),
      // so method.name is "toString" rather than the Kotest test name.
      RuleTracker.invocations shouldBe listOf(
         "before: toString",
         "after: toString",
         "before: toString",
         "after: toString",
      )
   }

   test("should apply @Rule annotated TestRule before and after each test") {
      RuleTracker.invocations.clear()
      KotestTestRunner(SpecWithTrackedRule::class.java).run(RunNotifier())
      RuleTracker.invocations shouldBe listOf(
         "before: rule test one",
         "after: rule test one",
         "before: rule test two",
         "after: rule test two",
      )
   }
})

private object RuleTracker {
   val invocations = mutableListOf<String>()
}

private class SpecWithTrackedRule : FunSpec() {

   @get:Rule
   val rule: TestRule = MyTestRule()

   init {
      test("rule test one") { /* noop */ }
      test("rule test two") { /* noop */ }
   }
}

class MyTestRule : TestRule {
   override fun apply(
      base: Statement,
      description: Description
   ): Statement {
      return object : Statement() {
         override fun evaluate() {
            RuleTracker.invocations.add("before: ${description.methodName}")
            base.evaluate()
            RuleTracker.invocations.add("after: ${description.methodName}")
         }
      }
   }
}

private class SpecWithTrackedMethodRule : FunSpec() {

   @get:Rule
   val rule: MethodRule = MyMethodRule()

   init {
      test("method rule test one") { /* noop */ }
      test("method rule test two") { /* noop */ }
   }
}

class MyMethodRule : MethodRule {
   override fun apply(base: Statement, method: FrameworkMethod, target: Any): Statement {
      return object : Statement() {
         override fun evaluate() {
            RuleTracker.invocations.add("before: ${method.name}")
            base.evaluate()
            RuleTracker.invocations.add("after: ${method.name}")
         }
      }
   }
}

private class DummySpec : FreeSpec() {
   init {
      testExecutionMode = TestExecutionMode.Concurrent
      repeat(100) { k ->
         "foo_$k" {
            delay(Random.nextLong(1, 15)) // force some bouncing around
         }
      }
   }
}
