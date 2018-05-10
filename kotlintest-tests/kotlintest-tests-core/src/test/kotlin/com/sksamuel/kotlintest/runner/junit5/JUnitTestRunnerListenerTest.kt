package com.sksamuel.kotlintest.runner.junit5

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.runner.junit5.JUnitTestRunnerListener
import io.kotlintest.runner.jvm.TestSet
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import java.time.Duration

class JUnitTestRunnerListenerTest : WordSpec({

  "JUnitTestRunnerListener" should {

    "add spec to root and dynamically register" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      rootDescriptor.children.size.shouldBe(0)

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      listener.prepareSpec(spec)

      rootDescriptor.children.size.shouldBe(1)
      rootDescriptor.children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" })
    }

    "add test to spec descriptor and dynamically register" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec)
      listener.prepareTestCase(tc)
      listener.prepareTestSet(set)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "notify engine listener in sequence" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec)
      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" })

      listener.prepareTestCase(tc)
      then(mock).should(never()).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })

      listener.prepareTestSet(set)
      listener.testRun(set, 1)
      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })

      listener.completeTestCase(tc, TestResult.Success)

      // no completions yet until complete spec is called
      then(mock).should(never()).executionFinished(any(), any())

      listener.completeSpec(spec, null)
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
    }

    "only start a test once" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestCaseConfig(invocations = 3))
      val set = TestSet(tc, Duration.ofMinutes(2), 3, 1)

      listener.prepareSpec(spec)

      listener.prepareTestCase(tc)
      listener.prepareTestSet(set)
      listener.testRun(set, 1)
      listener.testRun(set, 2)
      listener.completeTestCase(tc, TestResult.Success)

      listener.prepareTestCase(tc)
      listener.prepareTestSet(set)
      listener.testRun(set, 1)
      listener.testRun(set, 2)
      listener.completeTestCase(tc, TestResult.Success)

      then(mock).should(times(1)).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "propagate nested error to parent test" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestCaseConfig())
      val tc2 = TestCase(tc1.description.append("test2"), spec, { }, 1, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)
      val set2 = TestSet(tc2, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec)
      listener.prepareTestCase(tc1)
      listener.prepareTestSet(set1)
      listener.testRun(set1, 1)
      listener.completeTestCase(tc1, TestResult.Success)

      listener.prepareSpec(spec)
      listener.prepareTestCase(tc2)
      listener.prepareTestSet(set2)
      listener.testRun(set2, 1)
      listener.completeTestCase(tc2, TestResult.error(RuntimeException("boom")))

      listener.completeSpec(spec, null)
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
    }
  }

})