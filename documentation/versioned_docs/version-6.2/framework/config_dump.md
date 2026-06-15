---
id: config_dump
title: Config Dump
slug: config-dump.html
---

Kotest can optionally print the configuration that will be used for the test run when the test engine starts up.
To do this, set a system property or environment variable, with the name `kotest.framework.dump.config` and the value `true`.

For example, with gradle, you set the system property inside the `test` task configuration block.

```kotlin
test {
  systemProperty "kotest.framework.dump.config", "true"
}
```

When activated, you should find output like the following in standard out:

```kotlin
~~~ Kotest Configuration ~~~
-> Parallelization factor: 1
-> Concurrent specs: null
-> Global concurrent tests: 1
-> Dispatcher affinity: true
-> Coroutine debug probe: false
-> Spec execution order: Lexicographic
-> Default test execution order: Sequential
-> Default test timeout: 600000ms
-> Default test invocation timeout: 600000ms
-> Default isolation mode: SingleInstance
-> Global soft assertions: false
-> Write spec failure file: false
-> Fail on ignored tests: false
-> Fail on empty test suite: false
-> Duplicate test name mode: Warn
-> Remove test name whitespace: false
-> Append tags to test names: false
-> Extensions
  - io.kotest.engine.extensions.SystemPropertyTagExtension
```
