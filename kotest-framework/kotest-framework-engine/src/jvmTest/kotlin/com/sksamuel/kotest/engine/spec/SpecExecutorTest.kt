package com.sksamuel.kotest.engine.spec

import io.kotest.common.Platform
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
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
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEventsTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.spec.execution.SpecRefExecutor
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class SpecExecutorTest : FunSpec() {
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(FunSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(FunSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(FunSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(BehaviorSpecTest::class)
               listener.events.shouldHaveSize(12).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(BehaviorSpecTest::class),
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
                  TestEventsTestEngineListener.TestEvent.SpecFinished(BehaviorSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(DescribeSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(DescribeSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(DescribeSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(ExpectSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(ExpectSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(ExpectSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(FeatureSpecTest::class)
               listener.events.shouldHaveSize(12).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(FeatureSpecTest::class),
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
                  TestEventsTestEngineListener.TestEvent.SpecFinished(FeatureSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(FreeSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(FreeSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(FreeSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(ShouldSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(ShouldSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(ShouldSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(StringSpecTest::class)
               listener.events.shouldHaveSize(6).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(StringSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(StringSpecTest::class, null),
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
                  EngineContext(config, Platform.JVM).withListener(
                     ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(listener))
                  )
               )
               executor.execute(WordSpecTest::class)
               listener.events.shouldHaveSize(10).toSet() shouldBe setOf(
                  TestEventsTestEngineListener.TestEvent.SpecStarted(WordSpecTest::class),
                  TestEventsTestEngineListener.TestEvent.TestStarted("a"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("b"),
                  TestEventsTestEngineListener.TestEvent.TestStarted("c"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("c", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("b", null),
                  TestEventsTestEngineListener.TestEvent.TestFinished("a", null),
                  TestEventsTestEngineListener.TestEvent.TestStarted("d"),
                  TestEventsTestEngineListener.TestEvent.TestFinished("d", null),
                  TestEventsTestEngineListener.TestEvent.SpecFinished(WordSpecTest::class, null),
               )
            }
         }
      }
   }
}

private class FunSpecTest : FunSpec() {
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

private class BehaviorSpecTest : BehaviorSpec() {
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

private class DescribeSpecTest : DescribeSpec() {
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

private class ExpectSpecTest : ExpectSpec() {
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

private class FeatureSpecTest : FeatureSpec() {
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

private class FreeSpecTest : FreeSpec() {
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

private class ShouldSpecTest : ShouldSpec() {
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

private class StringSpecTest : StringSpec() {
   init {
      "a" { delay(2) }
      "b" { delay(2) }
   }
}

private class WordSpecTest : WordSpec() {
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
