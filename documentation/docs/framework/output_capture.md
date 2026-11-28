---
id: output_capture
title: Capturing Test Output
sidebar_label: Capturing Test Output
slug: output-capture.html
---

:::note
Available from Kotest 6.3, on the JVM only.
:::

Kotest can capture what each test writes to `System.out` and `System.err`, and report it against that test.
With `OutputCapture.OnFailure`, a passing test's output is discarded, and a failing test's output is shown with
that test. You get a quiet console and still see what went wrong.

```kotlin
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.OutputCapture

class ProjectConfig : AbstractProjectConfig() {
   override val outputCapture = OutputCapture.OnFailure
}
```

| Mode | Behaviour |
| --- | --- |
| `Never` (default) | The standard streams are left untouched. |
| `OnFailure` | Output is captured per test, and reported only for tests that fail or error. |
| `Always` | Output is captured per test, and reported for every test when it finishes. |

Set it with the `kotest.framework.output.capture` system property or the `KOTEST_FRAMEWORK_OUTPUT_CAPTURE`
environment variable, for example in Gradle:

```kotlin
tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.output.capture", "OnFailure")
}
```

The setting can also be made for a spec, a package, a spec's default test config, or a single test. The most
specific setting wins, as for other test settings:

```kotlin
import io.kotest.core.config.OutputCapture
import io.kotest.core.spec.style.FunSpec

class NoisySpec : FunSpec({
   outputCapture = OutputCapture.OnFailure

   test("always show this one").config(outputCapture = OutputCapture.Always) {
      println("shown even when the test passes")
   }
})
```

The standard streams are only replaced once some test asks for its output to be captured, and are restored
when the test run finishes.

Kotest keeps up to 1 MiB of output per test. When a test writes more, its oldest output is dropped and the report
says so. Change the limit with `outputCaptureMaxBytes` or `kotest.framework.output.capture.max.bytes`.

### Works with any logger, and with concurrent specs

Kotest captures the standard streams, so this works for `println` and for any logging library that writes to the
console (Logback, Log4j 2, `java.util.logging`, kotlin-logging and so on). You don't need a logging dependency or
any changes to your logging configuration.

Each write goes to the test whose coroutine made it, on whichever thread that coroutine is running. This includes
`withContext` blocks, child coroutines and dispatcher switches. So output is attributed correctly even when specs
and tests run concurrently. Another test's output never shows up in a failing test's report.

### Where the output is reported

* **JUnit Platform (Gradle, Maven, IntelliJ via Gradle)**: as `stdout` and `stderr` report entries on the test, the
  same keys the JUnit Platform uses for its own output capture. The output is also written to the console just
  before the test is reported finished, so Gradle attaches it to the right test in its reports and in plugins
  that show output for failed tests only.
* **IntelliJ via the Kotest plugin**: as the test's own output in the test tree.
* **Kotest's own JUnit XML report**: as the test case's `system-out` and `system-err`.
* **JUnit 4 runner (Android instrumented tests)**: written to the standard streams just before the test is
  reported finished, since JUnit 4 has no per-test output channel.

### Output from threads a spec created

A spec often starts things that run on their own threads, such as a server and its worker pool. Those threads
don't run a test coroutine, so by default their output isn't attributed to any test and goes straight through to
the console. Set `outputCaptureSpecThreads = true` (or `kotest.framework.output.capture.spec.threads`) to
attribute it:

```kotlin
class ProjectConfig : AbstractProjectConfig() {
   override val outputCapture = OutputCapture.OnFailure
   override val outputCaptureSpecThreads = true
}
```

A thread created while a spec runs belongs to that spec. That includes threads created when the spec is
constructed, in `beforeSpec`, or in a test. Its output goes to the spec's running test, but only while the spec
runs one test at a time (the default). This holds even when specs run concurrently with each other. If the spec
runs several tests at once, or has finished, the output is written through.

Ownership is decided when a thread is created, and a pool creates its threads wherever work is submitted. A pool
shared across specs therefore hands each new thread to whichever spec happened to submit work when it was added.
This happens even if the pool itself was created outside any spec. Kotest never attributes output from the JVM's
common `ForkJoinPool` or the kotlinx.coroutines dispatchers this way.

For your own shared pools, create their threads with `CurrentTestCase.unownedThreadFactory()`
(`io.kotest.engine.test`, `@ExperimentalKotest`). Its threads never belong to a spec, so their output is written
straight through. It wraps an existing factory (`Executors.defaultThreadFactory()` by default) and keeps its thread
names and daemon flag. Use it wherever you create, or a library lets you configure, a `ThreadFactory`. For
example, a pool held in an `object` and used by several specs:

```kotlin
import io.kotest.common.ExperimentalKotest
import io.kotest.engine.test.CurrentTestCase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalKotest::class)
object SharedWorkers {
   val pool: ExecutorService = Executors.newCachedThreadPool(CurrentTestCase.unownedThreadFactory())
}
```

Without the factory, each thread this pool adds would belong to whichever spec submitted the work that made it
add the thread. Connection pools and HTTP clients that accept a `ThreadFactory` are configured the same way.

A shared pool that you can't give a thread factory can still be misattributed while this setting is on, which is
why it is opt-in.

### Limitations

* Output from a thread that is not running one of the test's coroutines is written straight through to the
  console, unless [spec threads](#output-from-threads-a-spec-created) attribute it. To attribute it precisely,
  carry the current test onto that thread: use `CurrentTestCase.wrap(runnable)` for executors, or
  `CurrentTestCase.asContextElement()` for coroutines launched in a scope the test does not own.
* A logger that keeps a reference to the console stream it saw when it was configured is not captured if it was
  configured before the tests started. Examples are Log4j 2's console appender without `follow="true"` and JUL's
  `ConsoleHandler`.
* Some libraries replace `System.out` themselves, for example Jansi through Ktor's `CallLogging` with colours on.
  Output written to the replacement is not captured, and Kotest prints a warning when it detects this.

### Reading the current test

Kotest uses `io.kotest.engine.test.CurrentTestCase` (`@ExperimentalKotest`) to attribute output. You can use it
too. `CurrentTestCase.get()` returns the test running on the current thread, or null. It is not a suspending
function, so you can call it from code that knows nothing about coroutines, such as a logging appender.
