package com.sksamuel.kotest.tags

import io.kotest.core.NamedTag
import io.kotest.core.annotation.Tags
import io.kotest.engine.extensions.TagsExcludedDiscoveryExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TagsExcludedDiscoveryExtensionTest : FunSpec() {
   init {

      test("TagFilteredDiscoveryExtension should support include & exclude") {
         TagsExcludedDiscoveryExtension.afterScan(
            listOf(
               // will be included as includes are ignored at the class level
               IncludedSpec::class,
               // will be excluded explicitly
               ExcludedSpec::class,
               // will be included as we can must check the spec itself later to see if the test themselves have the include or exclude
               UntaggedSpec::class
            ),
            io.kotest.core.Tags.Empty.include(NamedTag("SpecIncluded")).exclude(NamedTag("SpecExcluded"))
         ) shouldBe listOf(IncludedSpec::class, UntaggedSpec::class)
      }

      test("TagFilteredDiscoveryExtension should ignore include only") {
         TagsExcludedDiscoveryExtension.afterScan(
            listOf(
               // all will be included as includes are ignored at the class level
               IncludedSpec::class,
               ExcludedSpec::class,
               UntaggedSpec::class
            ),
            io.kotest.core.Tags.Empty.include(NamedTag("SpecIncluded"))
         ) shouldBe listOf(IncludedSpec::class, ExcludedSpec::class, UntaggedSpec::class)
      }

      test("TagFilteredDiscoveryExtension should support exclude only") {
         TagsExcludedDiscoveryExtension.afterScan(
            listOf(
               // will be included as it is not explicitly excluded
               IncludedSpec::class,
               // will be excluded as it has been explicitly excluded
               ExcludedSpec::class,
               // will be included as it is not explicitly excluded
               UntaggedSpec::class
            ),
            io.kotest.core.Tags.Empty.exclude(NamedTag("SpecExcluded"))
         ) shouldBe listOf(IncludedSpec::class, UntaggedSpec::class)
      }
   }
}

@Tags("SpecExcluded")
private class ExcludedSpec : ExpectSpec()

@Tags("SpecIncluded")
private class IncludedSpec : BehaviorSpec()

private class UntaggedSpec : FunSpec()
