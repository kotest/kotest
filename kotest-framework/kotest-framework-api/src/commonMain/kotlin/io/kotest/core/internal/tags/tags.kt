package io.kotest.core.internal.tags

import io.kotest.core.Tag
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * Returns the tags specified on the given class from the @Tags annotation if present.
 */
expect fun KClass<*>.tags(): Set<Tag>

/**
 * Returns all tags assigned to a [TestCase], taken from the test case config, spec inline function,
 * spec override function, or the spec class.
 */
fun TestCase.allTags(): Set<Tag> = this.config.tags + this.spec.declaredTags() + this.spec::class.tags()
