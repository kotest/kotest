package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener

object Exclude : Tag()

private object ExcludeTagExtension : TagExtension {
   override fun tags(): Tags = Tags.exclude(Exclude)
}

class ExcludeTagExtensionTest : FunSpec() {
   init {
      test("tag extensions should be applied to tests with tag inherited from spec") {
         val listener = object : TestEngineListener {
            override fun testStarted(testCase: TestCase) {
               fail(testCase.displayName + " should not run")
            }
         }
         configuration.registerExtension(ExcludeTagExtension)
         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(ExcludedSpec::class)
         configuration.deregisterExtension(ExcludeTagExtension)
      }
   }
}

private class ExcludedSpec : StringSpec({

   tags(Exclude)

   "should not run" {
      fail("Shouldn't get here")
   }
})
