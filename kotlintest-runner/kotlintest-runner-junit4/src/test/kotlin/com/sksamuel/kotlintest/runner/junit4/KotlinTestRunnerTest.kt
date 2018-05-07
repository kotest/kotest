//@file:Suppress("UsePropertyAccessSyntax")
//
//package com.sksamuel.kotlintest.runner.junit4
//
//import com.nhaarman.mockito_kotlin.argThat
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.then
//import io.kotlintest.Spec
//import io.kotlintest.matchers.beInstanceOf
//import io.kotlintest.matchers.haveSize
//import io.kotlintest.runner.junit4.KotlinTestRunner
//import io.kotlintest.should
//import io.kotlintest.shouldBe
//import org.junit.Test
//import org.junit.runner.Runner
//import org.junit.runner.notification.RunListener
//import org.junit.runner.notification.RunNotifier
//
//class KotlinTestRunnerTest {
//	@Test
//	fun `should be a runner`() {
//		KotlinTestRunner(Spec::class.java) should beInstanceOf(Runner::class)
//	}
//
//	@Test
//	fun `should return a list of scopes when calling get children`() {
//		val runner = KotlinTestRunner(HelloWorldTest::class.java)
//		val children =
//			runner.getDescription().getChildren()
//		children should haveSize(2)
//		children[0].apply {
//			isTest() shouldBe true
//			isSuite() shouldBe false
//			testCount() shouldBe 1
//			getMethodName() shouldBe "HelloWorldTest first test ()"
//		}
//		children[1].apply {
//			isTest() shouldBe false
//			isSuite() shouldBe true
//			testCount() shouldBe 2
//			getMethodName() shouldBe "HelloWorldTest string tests .@#@$#(!)@#"
//			val tests = getChildren()
//			tests should haveSize(2)
//			tests[0].apply {
//				isTest() shouldBe true
//				isSuite() shouldBe false
//				testCount() shouldBe 1
//				getMethodName() shouldBe "HelloWorldTest string tests .@#@$#(!)@# substring"
//			}
//
//			tests[1].apply {
//				isTest() shouldBe true
//				isSuite() shouldBe false
//				testCount() shouldBe 1
//				getMethodName() shouldBe "HelloWorldTest string tests .@#@$#(!)@# startsWith"
//			}
//		}
//	}
//
//	@Test
//	fun `should run children and report results`() {
//		val listener = mock<RunListener> {}
//		val notifier = RunNotifier()
//		notifier.addListener(listener)
//		val runner = KotlinTestRunner(SomeBehaviourSpec::class.java)
//		runner.run(notifier)
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1" })
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1 When I add a 2" })
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1 When I add a 2 Then I get a 3" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1 When I add a 2 Then I get a 3" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1 When I add a 2" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given I have a 1" })
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5" })
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2" })
//		then(listener).should()
//			.testStarted(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2 Then I should get 5" })
//		then(listener).should()
//			.testFailure(argThat { getDescription().getMethodName() ==  "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2 Then I should get 5" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2 Then I should get 5" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2" })
//		then(listener).should()
//			.testFinished(argThat { getMethodName() == "SomeBehaviourSpec Given Big Brother says 2 + 2 = 5 When I add 2 + 2" })
//	}
//}
//
