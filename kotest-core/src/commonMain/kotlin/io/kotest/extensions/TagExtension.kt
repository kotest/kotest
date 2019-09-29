package io.kotest.extensions

import io.kotest.Tag
import io.kotest.Tags
import io.kotest.extensions.RuntimeTagExtension.excluded
import io.kotest.extensions.RuntimeTagExtension.included

/**
 * Returns [Tags] to be used by the Test Engine.
 *
 * A [Tag] can be added to any test and then specific tags can be included
 * or excluded via [TagExtension] instances, which will cause tests that do not
 * match to be skipped.
 *
 * Note: If multiple extensions are registered then all returned
 * [Tags] are combined together.
 *
 * The default [SystemPropertyTagExtension] is automatically registered
 * which includes and excludes tags using the system properties
 * 'kotest.tags.include' and 'kotest.tags.exclude'
 *
 * The default [RuntimeTagExtension] is automatically registered, which
 * allows to configure tags at runtime (for example, during a configuration procedure)
 * using the properties `included` and `excluded`
 */
interface TagExtension : ProjectLevelExtension {
  fun tags(): Tags
}

/**
 * Allows including/excluding tags at runtime
 *
 * You can use the properties [included] and [excluded] to modify what behavior you should use for specific tests
 * at runtime. Any test tagged with tags in [included] will be included to run, and any tags in [excluded] will be excluded.
 */
object RuntimeTagExtension : TagExtension {

  val included = mutableSetOf<Tag>()
  val excluded = mutableSetOf<Tag>()

  override fun tags(): Tags {
    return Tags(included, excluded)
  }

}

class SpecifiedTagsTagExtension(private val included: Set<Tag>, private val excluded: Set<Tag>) : TagExtension {
  override fun tags(): Tags {
    return Tags(included, excluded)
  }
}
