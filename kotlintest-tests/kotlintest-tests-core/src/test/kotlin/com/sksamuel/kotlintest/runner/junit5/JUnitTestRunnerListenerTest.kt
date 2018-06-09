package com.sksamuel.kotlintest.runner.junit5

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import com.sksamuel.kotlintest.runner.jvm.ReflectionsHelperTest
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestType
import io.kotlintest.runner.junit5.JUnitTestRunnerListener
import io.kotlintest.runner.jvm.TestSet
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
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
      listener.prepareSpec(spec.description(), spec::class)
      listener.completeSpec(spec.description(), spec::class, null)

      rootDescriptor.children.size.shouldBe(1)
      rootDescriptor.children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" })
    }

    "add test to spec descriptor and dynamically register" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestType.Container, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc)
      listener.testRun(set, 1)
      listener.completeTestCase(tc, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]")
      then(mock).should().dynamicTestRegistered(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "set TestDescriptor.Type to CONTAINER when TestType is Container" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestType.Container, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc)
      listener.testRun(set, 1)
      listener.completeTestCase(tc, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]")
      rootDescriptor.children.first().children.first().type shouldBe TestDescriptor.Type.CONTAINER
    }

    "set TestDescriptor.Type to TEST when TestType is Test" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestType.Test, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc)
      listener.testRun(set, 1)
      listener.completeTestCase(tc, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      rootDescriptor.children.first().children.size.shouldBe(1)
      rootDescriptor.children.first().children.first().uniqueId.toString().shouldBe("[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]")
      rootDescriptor.children.first().children.first().type shouldBe TestDescriptor.Type.TEST
    }

    "notify engine listener in sequence" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestType.Container, TestCaseConfig())
      val set = TestSet(tc, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc)

      // no start notifications until we see a test run
      then(mock).should(never()).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })

      listener.testRun(set, 1)
      listener.completeTestCase(tc, TestResult.Success)

      // no finished notifications until complete spec is called
      then(mock).should(never()).executionFinished(any(), any())

      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" })
      then(mock).should().executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" }, argThat { this.status == TestExecutionResult.Status.SUCCESSFUL })
    }

    "only start a test once" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("my test"), spec, { }, 5, TestType.Container, TestCaseConfig(invocations = 3))
      val set = TestSet(tc, Duration.ofMinutes(2), 3, 1)

      listener.prepareSpec(spec.description(), spec::class)

      listener.prepareTestCase(tc)
      listener.testRun(set, 1)
      listener.testRun(set, 2)
      listener.completeTestCase(tc, TestResult.Success)

      listener.prepareTestCase(tc)
      listener.testRun(set, 1)
      listener.testRun(set, 2)
      listener.completeTestCase(tc, TestResult.Success)

      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(times(1)).executionStarted(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:my test]" })
    }

    "propagate nested failure to parent test" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val tc2 = TestCase(tc1.description.append("test2"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)
      val set2 = TestSet(tc2, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc1)
      listener.testRun(set1, 1)
      listener.prepareTestCase(tc2)
      listener.testRun(set2, 1)
      listener.completeTestCase(tc2, TestResult.error(RuntimeException("boom")))
      listener.completeTestCase(tc1, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
      then(mock).should().executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" }, argThat { this.status == TestExecutionResult.Status.FAILED })
    }

    "mark inactive test as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc = TestCase(spec.description().append("test"), spec, { }, 1, TestType.Container, TestCaseConfig())

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc)
      listener.completeTestCase(tc, TestResult.Ignored)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test]" }, any())
      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test]" }, any())
    }

    "mark nested inactive test as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val tc2 = TestCase(tc1.description.append("test2"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc1)
      listener.testRun(set1, 1)
      listener.prepareTestCase(tc2)
      listener.completeTestCase(tc2, TestResult.Ignored)
      listener.completeTestCase(tc1, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(times(1)).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" }, any())
    }

    "a skipped child should not notify parent as skipped" {
      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val tc2 = TestCase(tc1.description.append("test2"), spec, { }, 1, TestType.Container, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc1)
      listener.testRun(set1, 1)
      listener.prepareTestCase(tc2)
      listener.completeTestCase(tc2, TestResult.Ignored)
      listener.completeTestCase(tc1, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(times(1)).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())
      then(mock).should(never()).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]/[test:test2]" }, any())

      then(mock).should(never()).executionSkipped(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" }, any())
      then(mock).should(times(1)).executionFinished(argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" }, argThat { status == TestExecutionResult.Status.SUCCESSFUL })
    }

    "only notify for descriptions that belong to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")

      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec1 = JUnitTestRunnerListenerTest()
      val spec2 = ReflectionsHelperTest()
      val tc1 = TestCase(spec1.description().append("test1"), spec1, { }, 1, TestType.Container, TestCaseConfig())
      val tc2 = TestCase(spec2.description().append("test2"), spec2, { }, 1, TestType.Container, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)
      val set2 = TestSet(tc2, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec1.description(), spec1::class)
      listener.prepareSpec(spec2.description(), spec2::class)

      listener.prepareTestCase(tc1)
      listener.prepareTestCase(tc2)

      listener.testRun(set1, 1)
      listener.testRun(set2, 1)

      listener.completeTestCase(tc1, TestResult.Success)
      listener.completeTestCase(tc2, TestResult.Success)

      listener.completeSpec(spec1.description(), spec1::class, null)
      listener.completeSpec(spec2.description(), spec2::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" },
          any()
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:ReflectionsHelperTest]/[test:test2]" },
          argThat { status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }

    "a failed test should not be propagated to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestType.Test, TestCaseConfig())
      val tc2 = TestCase(spec.description().append("test2"), spec, { }, 1, TestType.Test, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)
      val set2 = TestSet(tc2, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc1)
      listener.prepareTestCase(tc2)
      listener.testRun(set1, 1)
      listener.testRun(set2, 1)
      listener.completeTestCase(tc1, TestResult.failure(AssertionError("boom")))
      listener.completeTestCase(tc2, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" },
          argThat { this.status == TestExecutionResult.Status.FAILED }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test2]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }

    "an errored test should not be propagated to the spec" {

      val rootDescriptor = EngineDescriptor(UniqueId.forEngine("engine-test"), "engine-test")
      val mock = mock<EngineExecutionListener> {}
      val listener = JUnitTestRunnerListener(mock, rootDescriptor)

      val spec = JUnitTestRunnerListenerTest()
      val tc1 = TestCase(spec.description().append("test1"), spec, { }, 1, TestType.Test, TestCaseConfig())
      val tc2 = TestCase(spec.description().append("test2"), spec, { }, 1, TestType.Test, TestCaseConfig())
      val set1 = TestSet(tc1, Duration.ofMinutes(2), 1, 1)
      val set2 = TestSet(tc2, Duration.ofMinutes(2), 1, 1)

      listener.prepareSpec(spec.description(), spec::class)
      listener.prepareTestCase(tc1)
      listener.prepareTestCase(tc2)
      listener.testRun(set1, 1)
      listener.testRun(set2, 1)
      listener.completeTestCase(tc1, TestResult.error(RuntimeException("boom")))
      listener.completeTestCase(tc2, TestResult.Success)
      listener.completeSpec(spec.description(), spec::class, null)

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test1]" },
          argThat { this.status == TestExecutionResult.Status.FAILED }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]/[test:test2]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )

      then(mock).should(times(1)).executionFinished(
          argThat { this.uniqueId.toString() == "[engine:engine-test]/[spec:JUnitTestRunnerListenerTest]" },
          argThat { this.status == TestExecutionResult.Status.SUCCESSFUL }
      )
    }
  }
})