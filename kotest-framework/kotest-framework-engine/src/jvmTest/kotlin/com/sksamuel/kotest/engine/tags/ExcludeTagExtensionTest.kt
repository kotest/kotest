package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.tags.TagExpression

object Exclude : Tag()

private object ExcludeTagExtension : TagExtension {
   override fun tags(): TagExpression = TagExpression.exclude(Exclude)
}

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class ExcludeTagExtensionTest : FunSpec() {
   init {
      test("tag extensions should be applied to tests with tag inherited from spec") {

         val listener = object : AbstractTestEngineListener() {
            override suspend fun testStarted(testCase: TestCase) {
               fail(testCase.name.name + " should not run")
            }
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ExcludeTagExtension)
         }

         TestEngineLauncher(listener)
            .withClasses(ExcludedSpec::class)
            .withProjectConfig(c)
            .launch()
      }
   }
}

private class ExcludedSpec : StringSpec({

   tags(Exclude)

   "should not run" {
      fail("Shouldn't get here")
   }
})
