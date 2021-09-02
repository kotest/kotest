package io.kotest.engine

import io.kotest.core.spec.Spec

actual class SpecRunner {
   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
   }
}

