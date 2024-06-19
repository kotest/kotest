package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.assertSoftly
import io.kotest.core.TagExpression
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class RequiresTagTest : FunSpec({
   test("RequiresTagInterceptor should include spec if the tag expression contains the required tag") {
      val collector = CollectingTestEngineListener()

      TestEngineLauncher(collector)
         .withTagExpression(TagExpression("Foo"))
         .withClasses(TaggedSpec::class)
         .launch()

      collector
         .specs[TaggedSpec::class].shouldNotBeNull()
         .isIgnored.shouldBeFalse()
   }

   test("RequiresTagInterceptor should exclude spec if the tag expression does not contain the required tag") {
      val collector = CollectingTestEngineListener()

      TestEngineLauncher(collector)
         .withClasses(TaggedSpec::class)
         .withTagExpression(TagExpression("UnrelatedTag"))
         .launch()

      assertSoftly(collector.specs[TaggedSpec::class]) {
         shouldNotBeNull()
         isIgnored.shouldBeTrue()
         reasonOrNull shouldBe "Disabled by @RequiresTag"
      }
   }

   test("RequiresTagInterceptor should exclude spec if the tag expression is blank") {
      withSystemProperty("kotest.tags", null) {
         val collector = CollectingTestEngineListener()

         TestEngineLauncher(collector)
            .withClasses(TaggedSpec::class)
            .withTagExpression(TagExpression.Empty)
            .launch()

         collector
            .specs[TaggedSpec::class].shouldNotBeNull()
            .isIgnored.shouldBeTrue()
      }
   }
})

@RequiresTag("Foo")
private class TaggedSpec : ExpectSpec()
