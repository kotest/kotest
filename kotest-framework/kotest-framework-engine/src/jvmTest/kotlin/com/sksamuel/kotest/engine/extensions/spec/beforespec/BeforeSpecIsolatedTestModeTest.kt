package com.sksamuel.kotest.engine.extensions.spec.beforespec

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.install
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class BeforeSpecIsolatedTestModeTest : FunSpec({

   xtest("parallelism with IsolationMode.InstancePerRoot and beforeSpec") {

      val collector = CollectingTestEngineListener()
      TestEngineLauncher(collector)
         .withClasses(ParallelTests::class)
         .launch()

      collector.tests.values.size shouldBe 20
      collector.tests.values.forAll { it.isSuccess shouldBe true }
   }

})

private class ParallelTests : DescribeSpec({

   isolationMode = IsolationMode.InstancePerRoot
//   concurrency = 5

   val mountable = install(MyMountable())

   describe("foo1") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo2") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo3") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo4") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo5") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo6") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo7") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo8") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo9") { it("a") { mountable.counter.get() shouldBe 1 } }
   describe("foo10") { it("a") { mountable.counter.get() shouldBe 1 } }
})

class MyMountable : MountableExtension<Nothing, MyMountable>, BeforeSpecListener {

   val counter = AtomicInteger()

   override suspend fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   override fun mount(configure: Nothing.() -> Unit): MyMountable {
      return this
   }
}
