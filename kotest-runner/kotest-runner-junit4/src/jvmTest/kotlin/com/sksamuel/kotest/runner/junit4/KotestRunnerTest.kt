package com.sksamuel.kotest.runner.junit4

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.sksamuel.kotest.runner.junit4.samples.SomeBehaviourSpec
import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.collections.haveSize
import io.kotest.runner.junit4.kotestRunner
import io.kotest.should
import io.kotest.shouldBe
import org.junit.Test
import org.junit.runner.Runner
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier

class KotestRunnerTest {

  @Test
  fun `should be a runner`() {
    KotestRunner(HelloWorldTest::class.java) should beInstanceOf(Runner::class)
  }

  @Test
  fun `should return a list of scopes when calling get children`() {
    val runner = KotestRunner(HelloWorldTest::class.java)
    val children = runner.description.children
    children should haveSize(2)
    children[0].apply {
      isTest shouldBe true
      isSuite shouldBe false
      testCount() shouldBe 1
      methodName shouldBe "first test ()"
    }
    children[1].apply {
      isTest shouldBe true
      isSuite shouldBe false
      testCount() shouldBe 1
      methodName shouldBe "string tests .@#@$#(!)@#"
    }
  }

  @Test
  fun `should run children and report results`() {
    val listener = mock<RunListener> {}
    val notifier = RunNotifier()
    notifier.addListener(listener)
    val runner = KotestRunner(SomeBehaviourSpec::class.java)
    runner.run(notifier)
    then(listener).should()
        .testStarted(argThat { methodName == "Given: I have a 1" })
    then(listener).should()
        .testStarted(argThat { methodName == "Given: I have a 1 When: I add a 2" })
    then(listener).should()
        .testStarted(argThat { methodName == "Given: I have a 1 When: I add a 2 Then: I get a 3" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: I have a 1 When: I add a 2 Then: I get a 3" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: I have a 1 When: I add a 2" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: I have a 1" })
    then(listener).should()
        .testStarted(argThat { methodName == "Given: Big Brother says 2 + 2 = 5" })
    then(listener).should()
        .testStarted(argThat { methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2" })
    then(listener).should()
        .testStarted(argThat { methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2 Then: I should get 5" })
//    then(listener).should()
//        .testFailure(argThat { description.methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2 Then: I should get 5" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2 Then: I should get 5" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2" })
    then(listener).should()
        .testFinished(argThat { methodName == "Given: Big Brother says 2 + 2 = 5 When: I add 2 + 2" })
  }
}
