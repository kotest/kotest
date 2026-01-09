package com.sksamuel.kotest.engine.tags

import io.kotest.common.reflection.bestName
import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.annotation.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.SpecRef.Reference
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.spec.Materializer
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.enabled.TestEnabledChecker
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class TagsAnnotationCompositionTest : FunSpec() {
   init {

      test("Class should be enabled with composite annotation") {

         val ext = TagExtension { TagExpression.include(Foo) }

         val c = object : AbstractProjectConfig() {
            override val extensions: List<Extension> = listOf(ext)
         }

        val spec = MyCompositeAnnotationTest()
        val tests =
          Materializer(SpecConfigResolver(c)).materialize(spec, Reference(spec::class, spec::class.bestName()))

         val checker = TestEnabledChecker(
            ProjectConfigResolver(c),
            SpecConfigResolver(c),
            TestConfigResolver(c)
         )

         tests.filter { checker.isEnabled(it).isEnabled }
            .map { it.name.name }
            .toSet() shouldBe setOf("a")
      }
   }
}

object Foo : Tag()

annotation class CompositeAnnotationThatIncludesTagsAnnotation(val tags: Tags)

@CompositeAnnotationThatIncludesTagsAnnotation(Tags("Foo"))
private class MyCompositeAnnotationTest : FunSpec() {
   init {
      test("a") { }
   }
}
