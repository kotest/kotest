package io.kotest.core.js

/**
 * Activates Kotest JS support by running the initialization code required by the JS engine.
 */
actual fun useKotlinJs() {
   setAdapter(KotestFrameworkAdapter)
}
