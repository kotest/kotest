package com.sksamuel.kotest.engine.extensions

import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.extensions.LazyMountableExtension
import io.kotest.core.extensions.install
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class LazyMountableExtensionTest : FunSpec() {

   private val mountable = MyLazyMountable()
   private val m: LazyMaterialized<String> = install(mountable)

   init {
      test("lazy materialized values") {
         m.get() shouldBe "ready"
      }
   }
}

class MyLazyMountable : LazyMountableExtension<Unit, String>, BeforeSpecListener {

   private val m = LazyMaterialized("notready")

   override suspend fun beforeSpec(spec: Spec) {
      delay(5) // simulate slow db
      m.set("ready")
   }

   override fun mount(configure: (Unit) -> Unit): LazyMaterialized<String> {
      return m
   }
}
