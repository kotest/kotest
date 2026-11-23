package io.kotest.core.config

/**
 * Controls what happens to output written to `System.out` and `System.err` while a test runs.
 *
 * When enabled, Kotest routes each write to the test whose coroutine made it, on whichever thread
 * that coroutine is running, so output is attributed correctly even when specs and tests run concurrently.
 * The captured output is then reported against that test: on the JUnit Platform as `stdout`/`stderr`
 * report entries and on the standard streams just before the test is reported finished, to IntelliJ as the
 * test's own output, and in Kotest's JUnit XML report as the test case's `system-out`/`system-err`.
 *
 * Output that cannot be attributed to a test, such as writes from threads no test coroutine is running on,
 * is written through unchanged, unless [AbstractProjectConfig.outputCaptureSpecThreads] attributes it.
 *
 * Can be set for the project, a package, a spec, or a single test; the most specific setting wins.
 *
 * Output capture is supported on the JVM only.
 */
enum class OutputCapture {

   /**
    * The standard streams are left untouched. This is the default.
    */
   Never,

   /**
    * Output is captured per test, and only reported for tests that fail or error.
    * The output of a passing test is discarded.
    */
   OnFailure,

   /**
    * Output is captured per test, and reported for every test when it finishes.
    */
   Always,
}
