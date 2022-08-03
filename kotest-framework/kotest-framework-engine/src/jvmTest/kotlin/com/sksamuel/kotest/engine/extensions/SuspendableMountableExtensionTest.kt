package com.sksamuel.kotest.engine.extensions

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.SuspendableMountableExtension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

class SuspendableMountableExtensionTest : FunSpec({

   val e1 = MyMountableExtension()
   val e2 = MySuspendableExtension()

   val control1 = install(e1) {
      a = "bar"
   }

   val control2 = install(e2) {
      a = "bar"
   }

   test("SuspendableMountableExtensions should invoke configuration block") {
      control1.a shouldBe "bar"
      control2.a shouldBe "bar"
   }

   test("SuspendableMountableExtensions should be installed as regular extensions") {
      e2.before.get() shouldBe true
   }

   test("SuspendableMountableExtensions and MountableExtensions should coexist") {
      e1.before.get() shouldBe true
      e2.before.get() shouldBe true
   }
})

data class Conf(var a: String)

class MyMountableExtension : MountableExtension<Conf, Conf>, BeforeSpecListener {

   val before = AtomicBoolean(false)

   override suspend fun beforeSpec(spec: Spec) {
      before.set(true)
   }

   override fun mount(configure: (Conf) -> Unit): Conf {
      val config = Conf("foo")
      configure(config)
      return config
   }
}

class MySuspendableExtension : SuspendableMountableExtension<Conf, Conf>, BeforeTestListener {

   val before = AtomicBoolean(false)

   override suspend fun beforeTest(testCase: TestCase) {
      before.set(true)
   }

   override suspend fun mount(configure: suspend Conf.() -> Unit): Conf {
      val config = Conf("foo")
      configure(config)
      return config
   }
}
