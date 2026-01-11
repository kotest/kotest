package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.FilterResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.PostDiscoveryFilter
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder

class PostFilterTest : StringSpec({

   "!KotestJunitPlatformTestEngine should apply post filters from a LauncherDiscoveryRequest" {
      val request = LauncherDiscoveryRequestBuilder.request()
         .selectors(DiscoverySelectors.selectClass(SpecToBeExcluded::class.java))
         .selectors(DiscoverySelectors.selectClass(SpecToBeIncluded::class.java))
         .filters(PostDiscoveryFilter {
            when (it.uniqueId.toString()) {
               "[engine:test-engine]/[spec:com.sksamuel.kotest.runner.junit5.SpecToBeExcluded]" ->
                  FilterResult.excluded("")
               else ->
                  FilterResult.included("")
            }
         }).build()
      val descriptor = KotestJunitPlatformTestEngine()
         .discover(request, UniqueId.forEngine("test-engine"))
      descriptor.specs.first().fqn shouldBe "com.sksamuel.kotest.runner.junit5.SpecToBeIncluded"
      descriptor.specs.size shouldBe 1
   }

})

class SpecToBeExcluded : FunSpec()
class SpecToBeIncluded : FunSpec()
