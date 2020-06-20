package com.sksamuel.kotest.extensions

import io.kotest.core.StringTag
import io.kotest.core.annotation.Tags
import io.kotest.core.config.Project
import io.kotest.core.extensions.TagExtension
import io.kotest.core.extensions.TagFilteredDiscoveryExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TagFilteredDiscoveryExtensionTest : FunSpec() {
   init {

      test("TagFilteredDiscoveryExtension should support include and exclude") {

         val tags = object : TagExtension {
            override fun tags(): io.kotest.core.Tags {
               return io.kotest.core.Tags.Empty.include(StringTag("SpecIncluded")).exclude(StringTag("SpecExcluded"))
            }
         }

         Project.registerExtension(tags)

         TagFilteredDiscoveryExtension.afterScan(
            listOf(
               // will be included as we've specified it
               IncludedSpec::class,
               // will be excluded as we specified to exclude it
               ExcludedSpec::class,
               // will not be included as it's not in the inclusion list
               UntaggedSpec::class
            )
         ) shouldBe listOf(IncludedSpec::class)

         Project.deregisterExtension(tags)
      }

      test("TagFilteredDiscoveryExtension should support include only") {

         val tags = object : TagExtension {
            override fun tags(): io.kotest.core.Tags {
               return io.kotest.core.Tags.Empty.include(StringTag("SpecIncluded"))
            }
         }

         Project.registerExtension(tags)

         TagFilteredDiscoveryExtension.afterScan(
            listOf(
               // will be included as we've specified it
               IncludedSpec::class,
               // will be excluded as not on the inclusion list
               ExcludedSpec::class,
               // will be excluded as not on the inclusion list
               UntaggedSpec::class
            )
         ) shouldBe listOf(IncludedSpec::class)

         Project.deregisterExtension(tags)
      }

      test("TagFilteredDiscoveryExtension should support exclude only") {

         val tags = object : TagExtension {
            override fun tags(): io.kotest.core.Tags {
               return io.kotest.core.Tags.Empty.exclude(StringTag("SpecExcluded"))
            }
         }

         Project.registerExtension(tags)

         TagFilteredDiscoveryExtension.afterScan(
            listOf(
               // will be included as it is not excluded
               IncludedSpec::class,
               // will be excluded as it has been explicitly excluded
               ExcludedSpec::class,
               // will be included as it is not excluded
               UntaggedSpec::class
            )
         ) shouldBe listOf(IncludedSpec::class, UntaggedSpec::class)

         Project.deregisterExtension(tags)
      }
   }
}

@Tags("SpecExcluded")
private class ExcludedSpec : ExpectSpec()

@Tags("SpecIncluded")
private class IncludedSpec : BehaviorSpec()

private class UntaggedSpec : FunSpec()
