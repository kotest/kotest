package io.kotest.core.extensions

import io.kotest.common.DelicateKotest
import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.spec.SpecRef

/**
 * An extension point that allows the state of the engine to be modified before execution begins.
 */
@DelicateKotest
interface EngineContextExtension : Extension {

   data class EngineContext(val specs: List<SpecRef>, val configuration: Configuration, val tags: Tags)

   /**
    * Returns the context to be used by the engine.
    * Can be the same instance as the input or modified.
    */
   fun getContext(context: EngineContext): EngineContext
}
