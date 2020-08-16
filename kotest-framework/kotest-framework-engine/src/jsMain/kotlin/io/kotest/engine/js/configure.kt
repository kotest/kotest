package io.kotest.engine.js

import io.kotest.core.js.setAdapter

actual fun useKotest() {
   setAdapter(KotestFrameworkAdapter)
}
