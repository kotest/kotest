package io.kotest.engine.spec.execution.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.reflection.annotation
import io.kotest.core.NamedTag
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.tags.TagExpressionBuilder
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse

/**
 * A [SpecRefEnabledExtension] which will ignore specs if they are annotated with @[RequiresTag]
 * and those tags are not present in the runtime tag expression.
 */
@JVMOnly
internal class RequiresTagSpecRefEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {
      return when (val annotation = ref.kclass.annotation<RequiresTag>()) {
         null -> EnabledOrDisabled.Enabled
         else -> {
            val requiredTags = annotation.values.map { NamedTag(it) }.toSet()
            val expr = TagExpressionBuilder.build(projectConfigResolver).parse()
            if (requiredTags.isEmpty() || (expr != null && expr.isActive(requiredTags))) {
               EnabledOrDisabled.Enabled
            } else {
               EnabledOrDisabled.Disabled("Disabled by @RequiresTag (${annotation.values.joinToString(",")})")
            }
         }
      }
   }
}
