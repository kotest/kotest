package com.sksamuel.kotest.engine.config.specextensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

/**
 * Detected by [io.kotest.engine.config.PackageConfigLoader] for specs in this package.
 */
class PackageConfig : AbstractPackageConfig() {
   override val extensions: List<Extension> = listOf(PackageWideSpecListener, PackageWideSpecExtension)
}

private object PackageWideSpecListener : BeforeSpecListener, AfterSpecListener {

   val beforeSpecCounter = AtomicInteger(0)
   val afterSpecCounter = AtomicInteger(0)

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpecCounter.incrementAndGet()
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpecCounter.incrementAndGet()
   }
}

private object PackageWideSpecExtension : SpecExtension {

   val interceptCounter = AtomicInteger(0)

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      interceptCounter.incrementAndGet()
      execute(spec)
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class PackageConfigSpecExtensionsTest : FunSpec() {
   init {

      beforeTest {
         PackageWideSpecListener.beforeSpecCounter.set(0)
         PackageWideSpecListener.afterSpecCounter.set(0)
         PackageWideSpecExtension.interceptCounter.set(0)
      }

      test("spec level extensions declared in a package config should be invoked") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withoutEnvFilters()
            .withListener(collector)
            .withSpecRefs(SpecRef.Reference(PackageConfigTargetSpec::class))
            .execute()

         collector.tests.size shouldBe 1
         PackageWideSpecListener.beforeSpecCounter.get() shouldBe 1
         PackageWideSpecListener.afterSpecCounter.get() shouldBe 1
         PackageWideSpecExtension.interceptCounter.get() shouldBe 1
      }
   }
}

private class PackageConfigTargetSpec : FunSpec() {
   init {
      test("a") {}
   }
}
