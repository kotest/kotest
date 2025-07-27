package io.kotest.engine.tags

import io.kotest.core.Tag
import kotlin.reflect.KClass

actual fun KClass<*>.tags(tagInheritance: Boolean): Set<Tag> = emptySet()
