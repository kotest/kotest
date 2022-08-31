package io.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.Tags
import io.kotest.mpp.annotation
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

/**
 * Returns the tags specified on the given class (and all it's supertypes) from the @[Tags] annotation if present.
 */
actual fun KClass<*>.tags(): Set<Tag> {
   val myTags = annotation<Tags>()?.values?.map(::NamedTag) ?: emptyList()
   val supertypeTags = superclasses.flatMap { it.tags() }

   return (supertypeTags + myTags).toSet()
}
