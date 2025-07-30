package io.kotest.engine

/**
 * Throw this exception to mark a test as ignored.
 *
 * This is a runtime equivalent to using the `@Ignore` annotation, which can be throw by
 * arbitrary code that determines that a test should not be run.
 */
class TestAbortedException(val reason: String?) : Throwable() {
   constructor() : this(null)
}

/**
 * Throw this exception to mark a test invocation as ignored.
 *
 * This exception type can be used by other modules or libraries to indicate that a single
 * invocation inside a test (eg an iteration in a property test) should be skipped.
 *
 * If this exception bubbles up to the test engine, it will be treated in the same way as [TestAbortedException].
 */
class IterationSkippedException(val reason: String?) : Throwable() {
   constructor() : this(null)
}
