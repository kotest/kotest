package com.sksamuel.kotest.runner.junit5

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.core.TestCaseConfig
import io.kotest.runner.junit5.JUnitTestRunnerListener
import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@ExperimentalTime
class JUnitTestRunnerListenerTest : WordSpec({

  "JUnitTestRunnerListener" should {

    "add spec to root and dynamically register" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      rootDescriptor.children.size.shouldBe(0)

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      listener.beforeSpecClass(spec::class)
      listener.afterSpecClass(spec::class, null)

      rootDescriptor.children.size.shouldBe(1)
      rootDescriptor.children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]" })
    }

    "add test to spec descriptor and dynamically register" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.container(spec.description().append("my test"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc)
      listener.invokingTestCase(tc, 1)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "set TestDescriptor.Type to CONTAINER when TestType is Container" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.container(spec.description().append("my test"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc)
      listener.invokingTestCase(tc, 1)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]")
      rootDescriptor.children.first().children.first().type shouldBe TestDescriptor.Type.CONTAINER
    }

    "set TestDescriptor.Type to TEST when TestType is Test" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.test(spec.description().append("my test"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc)
      listener.invokingTestCase(tc, 1)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]")
      rootDescriptor.children.first().children.first().type shouldBe TestDescriptor.Type.TEST
    }

    "notify engine listener in sequence" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.container(spec.description().append("my test"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc)

      // no start notifications until we see a test run
      then(mock).should(never()).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]" })

      listener.invokingTestCase(tc, 1)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))

      // no finished notifications until complete spec is called
      then(mock).should(never()).executionFinished(any(), any())

      listener.afterSpecClass(spec::class, null)

      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]" })
      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]" })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
    }

    "only start a test once" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.container(spec.description().append("my test"), spec) { }
          .copy(config = TestCaseConfig(invocations = 3, timeout = 2.minutes))

      listener.beforeSpecClass(spec::class)

      listener.enterTestCase(tc)
      listener.invokingTestCase(tc, 1)
      listener.invokingTestCase(tc, 2)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))

      listener.enterTestCase(tc)
      listener.invokingTestCase(tc, 1)
      listener.invokingTestCase(tc, 2)
      listener.exitTestCase(tc, TestResult.success(Duration.ZERO))

      listener.afterSpecClass(spec::class, null)

      then(mock).should(times(1)).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "nested failure should propagate to parent" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase.container(spec.description().append("test1"), spec) { }
          .copy(config = TestCaseConfig(timeout = 2.minutes))
      val tc2 = TestCase.container(tc1.description.append("test2"), spec) { }
          .copy(config = TestCaseConfig(timeout = 2.minutes))

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc1)
      listener.invokingTestCase(tc1, 1)
      listener.enterTestCase(tc2)
      listener.invokingTestCase(tc2, 1)
      listener.exitTestCase(tc2, TestResult.error(RuntimeException("boom"), Duration.ZERO))
      listener.exitTestCase(tc1, TestResult.error(RuntimeException("boom"), Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
    }

    "mark inactive test as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase.container(spec.description().append("test"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc)
      listener.exitTestCase(tc, TestResult.Ignored)
      listener.afterSpecClass(spec::class, null)

      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test]" }, any())
      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test]" }, any())
    }

    "mark nested inactive test as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase.container(spec.description().append("test1"), spec) { }
      val tc2 = TestCase.container(tc1.description.append("test2"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc1)
      listener.invokingTestCase(tc1, 1)
      listener.enterTestCase(tc2)
      listener.exitTestCase(tc2, TestResult.Ignored)
      listener.exitTestCase(tc1, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(times(1)).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" }, any())
    }

    "a skipped child should not notify parent as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase.container(spec.description().append("test1"), spec) { }
      val tc2 = TestCase.container(tc1.description.append("test2"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc1)
      listener.invokingTestCase(tc1, 1)
      listener.enterTestCase(tc2)
      listener.exitTestCase(tc2, TestResult.Ignored)
      listener.exitTestCase(tc1, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())

      then(mock).should(never()).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" }, any())
      then(mock).should(times(1)).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" }, argThat { status == TestExecutionResult.Status.SUCCESSFUL })
    }

    "only notify for descriptions that belong to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec1 = JUnitTestRunnerListenerTest()
      val spec2 = Kotest()
      val tc1 = TestCase.container(spec1.description().append("test1"), spec1) { }
      val tc2 = TestCase.container(spec2.description().append("test2"), spec2) { }

      listener.beforeSpecClass(spec1::class)
      listener.beforeSpecClass(spec2::class)

      listener.enterTestCase(tc1)
      listener.enterTestCase(tc2)

      listener.invokingTestCase(tc1, 1)
      listener.invokingTestCase(tc2, 1)

      listener.exitTestCase(tc1, TestResult.success(Duration.ZERO))
      listener.exitTestCase(tc2, TestResult.success(Duration.ZERO))

      listener.afterSpecClass(spec1::class, null)
      listener.afterSpecClass(spec2::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" },
          any()
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.Kotest]/[test:test2]" },
          argThat { status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }

    "a failed test should not be propagated to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase.test(spec.description().append("test1"), spec) { }
      val tc2 = TestCase.test(spec.description().append("test2"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc1)
      listener.enterTestCase(tc2)
      listener.invokingTestCase(tc1, 1)
      listener.invokingTestCase(tc2, 1)
      listener.exitTestCase(tc1, TestResult.failure(AssertionError("boom"), Duration.ZERO))
      listener.exitTestCase(tc2, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" },
          argThat { this.status == TestExecutionResult.Status.FAILED }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test2]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }

    "an errored test should not be propagated to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase.test(spec.description().append("test1"), spec) { }
      val tc2 = TestCase.test(spec.description().append("test2"), spec) { }

      listener.beforeSpecClass(spec::class)
      listener.enterTestCase(tc1)
      listener.enterTestCase(tc2)
      listener.invokingTestCase(tc1, 1)
      listener.invokingTestCase(tc2, 1)
      listener.exitTestCase(tc1, TestResult.error(RuntimeException("boom"), Duration.ZERO))
      listener.exitTestCase(tc2, TestResult.success(Duration.ZERO))
      listener.afterSpecClass(spec::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test1]" },
          argThat { this.status == TestExecutionResult.Status.FAILED }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]/[test:test2]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:com.sksamuel.kotest.runner.junit5.JUnitTestRunnerListenerTest]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }
  }
})
