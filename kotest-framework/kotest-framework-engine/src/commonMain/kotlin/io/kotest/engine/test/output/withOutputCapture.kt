package io.kotest.engine.test.output

import io.kotest.common.JVMOnly
import io.kotest.core.spec.SpecRef
import io.kotest.engine.listener.TestEngineListener

/**
 * Runs an engine's [block], restoring the standard streams afterwards if a test installed output capture
 * while it ran. On platforms other than the JVM, runs [block] unchanged.
 */
@JVMOnly
internal expect suspend fun <T> withOutputCapture(block: suspend () -> T): T

/**
 * Returns a listener that invokes [listener] with no current test, so that what listeners write to
 * the standard streams, such as IDE service messages, is never captured as a test's output.
 * On platforms other than the JVM, returns [listener] unchanged.
 */
@JVMOnly
internal expect fun excludeFromOutputCapture(listener: TestEngineListener): TestEngineListener

/**
 * Runs a spec's [block] so that, if [enabled], threads created while it runs are owned by the spec, and output
 * they write is attributed to the spec's running test. On platforms other than the JVM, runs [block] unchanged.
 */
@JVMOnly
internal expect suspend fun <T> withSpecThreadOwnership(ref: SpecRef, enabled: Boolean, block: suspend () -> T): T
