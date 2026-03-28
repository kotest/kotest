package com.sksamuel.kotest.engine.spec.execution

import io.kotest.common.KotestTesting
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineContext
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEventsTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.spec.execution.SpecRefExecutor
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@Suppress("DEPRECATION")
@OptIn(KotestTesting::class)
class SpecExecutorIsolationModeTest : FunSpec() {
   init {
      context("FunSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeFunSpecTest, IsolationModeFunSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeFunSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeFunSpecTest::class, null),
               )
            }
         }
      }

      context("BehaviorSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeBehaviorSpecTest, IsolationModeBehaviorSpecTest::class))
               listener.events.shouldHaveSize(12).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeBehaviorSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("e"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("e", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeBehaviorSpecTest::class, null),
               )
            }
         }
      }

      context("DescribeSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeDescribeSpecTest, IsolationModeDescribeSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeDescribeSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeDescribeSpecTest::class, null),
               )
            }
         }
      }

      context("ExpectSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeExpectSpecTest, IsolationModeExpectSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeExpectSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeExpectSpecTest::class, null),
               )
            }
         }
      }

      context("FeatureSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeFeatureSpecTest, IsolationModeFeatureSpecTest::class))
               listener.events.shouldHaveSize(12).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeFeatureSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("e"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("e", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeFeatureSpecTest::class, null),
               )
            }
         }
      }

      context("FreeSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeFreeSpecTest, IsolationModeFreeSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeFreeSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeFreeSpecTest::class, null),
               )
            }
         }
      }

      context("ShouldSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeShouldSpecTest, IsolationModeShouldSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeShouldSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeShouldSpecTest::class, null),
               )
            }
         }
      }

      context("StringSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeStringSpecTest, IsolationModeStringSpecTest::class))
               listener.events.shouldHaveSize(6).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeStringSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeStringSpecTest::class, null),
               )
            }
         }
      }

      context("WordSpec") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
            IsolationMode.InstancePerTest,
            IsolationMode.InstancePerLeaf,
         ) { isolationMode ->
            withData(
               TestExecutionMode.Sequential,
               TestExecutionMode.Concurrent,
            ) { executionMode ->
               val listener = TestEventsTestEngineListener()
               val config = object : AbstractProjectConfig() {
                  override val isolationMode: IsolationMode = isolationMode
                  override val testExecutionMode: TestExecutionMode = executionMode
               }
               val executor = SpecRefExecutor(
                  TestEngineContext(config, ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener)))
               )
               executor.execute(SpecRef.Function(::IsolationModeWordSpecTest, IsolationModeWordSpecTest::class))
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(IsolationModeWordSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(IsolationModeWordSpecTest::class, null),
               )
            }
         }
      }
   }
}

private class IsolationModeFunSpecTest : FunSpec() {
   init {
      context("a") {
         delay(2)
         context("b") {
            delay(2)
            test("c") {
               delay(2)
            }
         }
      }
      test("d") {
         delay(2)
      }
   }
}

private class IsolationModeBehaviorSpecTest : BehaviorSpec() {
   init {
      given("a") {
         delay(2)
         `when`("b") {
            delay(2)
            then("c") { delay(2) }
         }
      }
      given("d") {
         delay(2)
         then("e") { delay(2) }
      }
   }
}

private class IsolationModeDescribeSpecTest : DescribeSpec() {
   init {
      describe("a") {
         delay(2)
         context("b") {
            delay(2)
            it("c") { delay(2) }
         }
      }
      it("d") { delay(2) }
   }
}

private class IsolationModeExpectSpecTest : ExpectSpec() {
   init {
      context("a") {
         delay(2)
         context("b") {
            delay(2)
            expect("c") { delay(2) }
         }
      }
      expect("d") { delay(2) }
   }
}

private class IsolationModeFeatureSpecTest : FeatureSpec() {
   init {
      feature("a") {
         delay(2)
         feature("b") {
            delay(2)
            scenario("c") { delay(2) }
         }
      }
      feature("d") {
         delay(2)
         scenario("e") { delay(2) }
      }
   }
}

private class IsolationModeFreeSpecTest : FreeSpec() {
   init {
      "a" - {
         delay(2)
         "b" - {
            delay(2)
            "c" { delay(2) }
         }
      }
      "d" { delay(2) }
   }
}

private class IsolationModeShouldSpecTest : ShouldSpec() {
   init {
      context("a") {
         delay(2)
         context("b") {
            delay(2)
            should("c") { delay(2) }
         }
      }
      should("d") { delay(2) }
   }
}

private class IsolationModeStringSpecTest : StringSpec() {
   init {
      "a" { delay(2) }
      "b" { delay(2) }
   }
}

private class IsolationModeWordSpecTest : WordSpec() {
   init {
      "a" `when` {
         delay(2)
         "b" should {
            delay(2)
            "c" { delay(2) }
         }
      }
      "d" should { delay(2) }
   }
}
