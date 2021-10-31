package io.kotest.core.extensions

import io.kotest.core.Tag
import io.kotest.core.Tags

/**
 * Allows including/excluding tags at runtime by specifying the [included] and [excluded] properties.
 */
class RuntimeTagExtension(private val included: Set<Tag>, private val excluded: Set<Tag>) : TagExtension {
   override fun tags(): Tags {
      return Tags(included, excluded)
   }
}

