package io.kotest.engine.filter

import io.kotest.core.NamedTag
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.config.configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.engine.tags.TagProvider
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * A [SpecFilter] which will ignore specs if they are annotated with @[RequiresTag]
 * and those tags are not present in the runtime tags.
 */
internal class RequiresTagSpecFilter(private val provider: TagProvider) : SpecFilter {

   override fun filter(kclass: KClass<*>): SpecFilterResult {
      // if no requires tag was used then we always include the spec
      val anno = kclass.annotation<RequiresTag>() ?: return SpecFilterResult.Include
      val requires = anno.values.map { NamedTag(it) }.toSet()
      return if (provider.tags(configuration).parse().isActive(requires))
         SpecFilterResult.Include
      else
         SpecFilterResult.Ignore("Excluded due to required tags $requires")
   }
}
