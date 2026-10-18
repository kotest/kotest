# Kotest Debugging Guide

Gather this information before diagnosing a Kotest issue.

## Step 1: Enable Debug Logging

`KOTEST_DEBUG=true` (environment variable or system property) turns on engine logging: spec discovery, config
resolution, extension callbacks, test execution.

| Platform | Where the log goes                                                                                   |
|----------|------------------------------------------------------------------------------------------------------|
| JVM      | stdout **and** a file `kotest-<PID>.log` in the test task's working directory (usually the module dir). Override with `KOTEST_DEBUG_PATH` (`PID` is substituted) |
| Non-JVM  | stdout only                                                                                          |

Gradle hides test stdout unless you pass `--info` or set `testLogging.showStandardStreams = true`.

```bash
KOTEST_DEBUG=true ./gradlew test --rerun
cat kotest-*.log                     # JVM: most reliable source
```

Persistent:

```kotlin
tasks.withType<Test>().configureEach {
   environment("KOTEST_DEBUG", "true")
}
```

IntelliJ: add `KOTEST_DEBUG=true` to the run configuration's environment variables.

Each log line is `<elapsed ms>  <logging class>  <spec/test context>  <message>`.

## Step 2: Collect Versions

- Kotest — `build.gradle.kts` / `libs.versions.toml` (all `io.kotest` artifacts must match)
- Kotlin (2.2+ required), Gradle (`gradle/wrapper/gradle-wrapper.properties`), JVM (`./gradlew --version`)
- Platform — JVM / JS / WasmJS / WasmWASI / Native / Android
- Runner — `kotest-runner-junit5`, `kotest-runner-junit4` (Android device), or engine-only + KSP (non-JVM)

## Step 3: Full Output and Reports

```bash
./gradlew test --info 2>&1 | tee kotest-debug.log
```

- HTML report: `build/reports/tests/<task>/index.html`
- XML: `build/test-results/<task>/`

## Step 4: Isolate the Failure

```bash
./gradlew test --tests "com.example.MyFailingTest"
./gradlew test --tests "com.example.MyFailingTest.my test name"
./gradlew test --tests 'com.example.MySpec.outer -- nested test'   # nested: " -- " delimiter
KOTEST_INCLUDE_PATTERN='com.example.*' ./gradlew check              # Kotest filter, all platforms
```

Passes alone but fails in the suite → shared state (see isolation modes / concurrency in
[FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md)) or a global extension side effect.

## Step 5: ProjectConfig Resolution

```bash
grep -i "project config" kotest-*.log
```

Expect lines like `Searching for project config at …` / `Project config discovered at …`. Discovery rules and the
`kotest.framework.config.fqn` property: [KNOWN-ISSUES.md §2](KNOWN-ISSUES.md#2-projectconfig-not-picked-up).

## Step 6: Common Scenarios

| Symptom                          | Check                                                                                       |
|----------------------------------|---------------------------------------------------------------------------------------------|
| Tests not discovered             | [KNOWN-ISSUES.md §1](KNOWN-ISSUES.md#1-tests-not-discovered-0-tests-executed); `grep -i spec kotest-*.log` |
| Extension / listener not firing  | registered explicitly ([KNOWN-ISSUES.md §3](KNOWN-ISSUES.md#3-autoscan-removed-60)); `grep -i extension kotest-*.log` |
| Hang / timeout ignored           | add `.config(timeout = 5.seconds)`; for blocking calls add `blockingTest = true` ([§6](KNOWN-ISSUES.md#6-timeout-ignored-by-blocking-code-jvm)) |
| Tag filter has no effect         | `-D` isn't forwarded — use `KOTEST_TAGS` ([§11](KNOWN-ISSUES.md#11-5x--6x-and-common-agent-mistakes)) |
| Property test failure            | replay the printed seed (below)                                                             |

Property test failures print:

```
Property test failed for inputs
...
Repeat this test by using seed 8168214184
```

Replay with `checkAll<Int>(PropTestConfig(seed = 8168214184)) { ... }` (match the original generators).

## Step 7: Minimal Reproduction

1. New Gradle project with only the relevant Kotest dependencies
2. A `FunSpec` / `StringSpec` with the minimum code that triggers the bug, no external dependencies
3. Confirm it reproduces
4. Ask the user to file it at https://github.com/kotest/kotest/issues/new/choose with versions and steps
