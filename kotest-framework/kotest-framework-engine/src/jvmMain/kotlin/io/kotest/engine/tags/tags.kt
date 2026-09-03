package io.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.Tags
import io.kotest.common.reflection.IncludingAnnotations
import io.kotest.common.reflection.IncludingSuperclasses
import io.kotest.common.reflection.reflection
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

// tags() is called on every test/container enabled-check and every display-name format, i.e. once
// per test case. But a spec (class) tag will not change cross tests within it, hence the cache.
private val tagsCache = ConcurrentHashMap<Pair<KClass<*>, Boolean>, Set<Tag>>()

/**
 * Returns the tags specified on the given class (and all it's supertypes) from the @[Tags] annotation if present.
 */
actual fun KClass<*>.tags(tagInheritance: Boolean): Set<Tag> {
   return tagsCache.getOrPut(this to tagInheritance) {
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
      tags.toSet()
   }
}
