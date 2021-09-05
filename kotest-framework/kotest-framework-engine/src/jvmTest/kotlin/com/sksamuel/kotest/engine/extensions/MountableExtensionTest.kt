package com.sksamuel.kotest.engine.extensions

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

class MountableExtensionTest : FunSpec() {

   private val mountable = MyMountable()
   private val control = install(mountable) {
      a = "bar"
   }

   init {
      test("mountable extensions should invoke configuration block") {
         control.a shouldBe "bar"
      }

      test("mountable extensions should be installed as regular extensions") {
         mountable.before.get() shouldBe true
      }
   }
}

data class Config(var a: String)

class MyMountable : MountableExtension<Config, Config>, BeforeSpecExtension {

   val before = AtomicBoolean(false)

   override fun beforeSpec(spec: Spec) {
      before.set(true)
   }

   override fun mount(configure: (Config) -> Unit): Config {
      val config = Config("foo")
      configure(config)
      return config
   }
}
