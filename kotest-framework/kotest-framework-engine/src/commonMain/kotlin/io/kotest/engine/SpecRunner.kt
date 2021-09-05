package io.kotest.engine

import io.kotest.core.spec.Spec

expect class SpecRunner() {

   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   fun execute(spec: Spec, onComplete: suspend () -> Unit)
}
