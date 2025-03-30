package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.extensions.LazyMountableExtension
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@EnabledIf(LinuxOnlyGithubCondition::class)
class LazyMountableExtensionTest : FunSpec() {

   private val mountable = MyLazyMountableExtension()
   private val m: LazyMaterialized<String> = install(mountable)

   init {
      test("lazy materialized values") {
         m.get() shouldBe "ready"
      }
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class MyLazyMountableExtension : LazyMountableExtension<Unit, String> {

   override fun mount(configure: (Unit) -> Unit): LazyMaterialized<String> {
      return object : LazyMaterialized<String> {

         var state: String? = null

         override suspend fun get(): String {
            delay(1)
            if (state == null) state = "ready"
            return state ?: error("Must be initialized")
         }
      }
   }
}
