package io.kotest.core.listeners

import io.kotest.core.extensions.Extension

/**
 * A [Listener] is a just an [Extension] that is passive.
 */
@Deprecated("This marker interface is being subsumed by Extension and should be ignored in favor of Extension. Deprecated in 5.0.")
interface Listener : Extension
