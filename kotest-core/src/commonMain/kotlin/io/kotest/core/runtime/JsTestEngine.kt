package io.kotest.core.runtime

import io.kotest.core.spec.SpecConfiguration

expect class JsTestEngine {
   fun execute(spec: SpecConfiguration)
}
