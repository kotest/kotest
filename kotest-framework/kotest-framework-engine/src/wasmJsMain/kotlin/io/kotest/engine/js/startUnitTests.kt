package io.kotest.engine.js

// The JS launcher set up by the Kotlin Gradle plugin calls this function in addition to `main()`.
@OptIn(ExperimentalJsExport::class)
@JsExport
internal fun startUnitTests() {
   // The Kotlin compiler would insert test invocations here for kotlin-test.
   // This mechanism is not used with Kotest.
   // in the future we could consider using this instead of a main function if we have a use case for it
}
