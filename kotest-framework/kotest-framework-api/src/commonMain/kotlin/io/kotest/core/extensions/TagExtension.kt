package io.kotest.core.extensions

import io.kotest.core.Tags

/**
 * Provides [Tags] to be used by the Test Engine in determining active tests.
 *
 * A [Tag] can be added to any test and then specific tags can be included
 * or excluded via [TagExtension] instances, which will cause tests that do not
 * match to be skipped.
 *
 * Note: If multiple extensions are registered then all returned
 * [Tags] are combined together using ORs
 *
 * The default [SystemPropertyTagExtension] is automatically registered
 * which includes and excludes tags using the system properties
 * 'kotest.tags.include' and 'kotest.tags.exclude'.
 *
 * The default [RuntimeTagExtension] is automatically registered, which
 * allows to configure tags at runtime (for example, during a configuration procedure)
 * by setting the properties `included` and `excluded`.
 */
interface TagExtension : Extension {
   fun tags(): Tags
}
