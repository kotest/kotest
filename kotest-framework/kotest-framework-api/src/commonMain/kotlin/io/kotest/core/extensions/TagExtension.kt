package io.kotest.core.extensions

import io.kotest.core.TagExpression

/**
 * Provides [TagExpression] to be used by the Test Engine in determining active tests.
 *
 * A [io.kotest.core.Tag] can be added to any test and then specific tags can be included
 * or excluded via [TagExtension] instances, which will cause tests that do not
 * match to be skipped.
 *
 * Note: If multiple extensions are registered then all returned
 * [TagExpression] are combined using ORs
 *
 * The [io.kotest.engine.extensions.SystemPropertyTagExtension] is automatically registered which
 * includes and excludes tags using the system properties
 * 'kotest.tags.include' and 'kotest.tags.exclude'.
 *
 */
fun interface TagExtension : Extension {
   fun tags(): TagExpression
}
