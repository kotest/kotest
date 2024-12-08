package io.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.Tags
import io.kotest.mpp.IncludingAnnotations
import io.kotest.mpp.IncludingSuperclasses
import io.kotest.mpp.reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

/**
 * Returns the tags specified on the given class (and all it's supertypes) from the @[Tags] annotation if present.
 */
actual fun KClass<*>.tags(tagInheritance: Boolean): Set<Tag> {
   val meta = setOf(IncludingAnnotations) + if (tagInheritance) setOf(IncludingSuperclasses) else emptySet()
   val annotations: List<Annotation> = reflection.annotations(this, meta)
   val tags = annotations.flatMap { a ->
      if (a is Tags) {
         a.values.map { NamedTag(it) }
      } else {
         a.annotationClass
            .declaredMemberProperties
            .map { it.call(a) }
            .filterIsInstance<Tags>()
            .flatMap { tags -> tags.values.map { NamedTag(it) } }
      }
   }
   return tags.toSet()
}
