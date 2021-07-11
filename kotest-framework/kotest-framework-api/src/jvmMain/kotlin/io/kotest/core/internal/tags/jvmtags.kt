package io.kotest.core.internal.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Returns the tags specified on the given class from the @Tags annotation if present.
 */
actual fun KClass<*>.tags(): Set<Tag> {
   val annotation = annotation<io.kotest.core.annotation.Tags>() ?: return emptySet()
   return annotation.values.map { NamedTag(it) }.toSet()
}
