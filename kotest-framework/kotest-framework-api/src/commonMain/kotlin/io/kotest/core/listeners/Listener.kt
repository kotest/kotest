package io.kotest.core.listeners

import io.kotest.core.extensions.Extension

@Deprecated("This marker interface is being subsumed by Extension. Will be removed in 6.0")
interface Listener : Extension {
   val name: String
}
