package io.kotest.core.listeners

import io.kotest.common.SoftDeprecated
import io.kotest.core.extensions.Extension

/**
 * A [Listener] is a just an [Extension] that is passive.
 */
@SoftDeprecated("This marker interface is being subsumed by Extension and should be ignored.")
interface Listener : Extension {
   val name: String
      get() = this::class.simpleName ?: "unspecified"
}
