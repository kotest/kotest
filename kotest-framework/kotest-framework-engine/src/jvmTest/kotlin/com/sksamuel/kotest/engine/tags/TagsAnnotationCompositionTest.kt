package com.sksamuel.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Tags
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxCondition::class)
class TagsAnnotationCompositionTest : FunSpec() {
   init {

      test("Class should be enabled with composite annotation") {

         val ext = TagExtension { TagExpression.include(Foo) }

         val conf = ProjectConfiguration()
         conf.registry.add(ext)

         Materializer(conf).materialize(MyCompositeAnnotationTest())
            .filter { it.isEnabledInternal(conf).isEnabled }
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
