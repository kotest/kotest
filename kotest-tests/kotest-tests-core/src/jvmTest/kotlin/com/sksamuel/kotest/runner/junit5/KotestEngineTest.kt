package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.FunSpec
import io.kotest.runner.junit5.KotestJunitPlatformTestEngine
import io.kotest.shouldBe
import io.kotest.specs.StringSpec
import org.junit.platform.engine.FilterResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.PostDiscoveryFilter
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import kotlin.reflect.jvm.jvmName

class KotestEngineTest : StringSpec({
   "KotestEngine should apply post filters from a LauncherDiscoveryRequest" {
      val request = LauncherDiscoveryRequestBuilder()
         .selectors(DiscoverySelectors.selectClass(SpecToBeExcluded::class.java))
         .selectors(DiscoverySelectors.selectClass(SpecToBeIncluded::class.java))
         .filters(PostDiscoveryFilter {
            println(it.uniqueId)
            when (it.uniqueId.toString()) {
               "[engine:test-engine]/[spec:com.sksamuel.kotest.runner.junit5.SpecToBeExcluded]" -> FilterResult.excluded(
                  ""
               )
               else -> FilterResult.included("")
            }
         }).build()
      val descriptor = KotestJunitPlatformTestEngine().discover(request, UniqueId.forEngine("test-engine"))
      descriptor.classes.first().jvmName shouldBe "com.sksamuel.kotest.runner.junit5.SpecToBeIncluded"
      descriptor.classes.size shouldBe 1
   }
})

class SpecToBeExcluded : FunSpec()
class SpecToBeIncluded : FunSpec()
