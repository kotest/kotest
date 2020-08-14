package io.kotest.engine.js

// we need to include setting the adapter as a top level val in here so that it runs before any suite/test in js
@Suppress("unused")
val initializeRuntime = configureRuntime()

