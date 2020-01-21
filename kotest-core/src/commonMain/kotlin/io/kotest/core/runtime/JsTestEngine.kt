package io.kotest.core.runtime

import io.kotest.core.spec.Spec

expect class JsTestEngine {
   fun execute(spec: Spec)
}
