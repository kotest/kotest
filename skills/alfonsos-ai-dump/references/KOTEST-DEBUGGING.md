# Kotest Debugging Guide

When a user reports a Kotest issue, gather the following information before diagnosing.

## Step 1: Enable Debug Logging

Set the `KOTEST_DEBUG` environment variable to `true` to enable verbose internal logging from the
Kotest engine. This prints lifecycle events, spec discovery, extension callbacks, and configuration
resolution to stderr.

**Gradle (one-off):**
```bash
KOTEST_DEBUG=true ./gradlew test
```

**Gradle (persistent in build file):**
```kotlin
tasks.withType<Test>().configureEach {
    environment("KOTEST_DEBUG", "true")
}
```

**IntelliJ Run Configuration:**
Add `KOTEST_DEBUG=true` to the environment variables field of the run configuration.

The debug output will include lines such as:
```
[kotest] loading spec io.example.MySpec
[kotest] executing test: my test name
[kotest] extension callback: BeforeSpecListener
```

## Step 2: Collect Version Information

Ask the user to provide:

- **Kotest version** — check `build.gradle.kts` or `libs.versions.toml`
- **Kotlin version** — check `kotlin()` plugin version in build file
- **Gradle version** — from `gradle/wrapper/gradle-wrapper.properties`
- **JVM version** — `java -version` or `./gradlew --version`
- **Platform** — JVM / JS / WasmJS / Native / Android
- **Test runner** — `kotest-runner-junit5`, `kotest-runner-junit4`, or engine-only (KMP)

## Step 3: Collect the Full Error Output

Request the full output from one of:

```bash
./gradlew test --info 2>&1 | tee kotest-debug.log
KOTEST_DEBUG=true ./gradlew test 2>&1 | tee kotest-debug.log
```

Using `--info` surfaces task-level detail that `--debug` buries in Gradle noise. The log should
include the test report summary and any stack traces.

## Step 4: Check the Test Report

HTML and XML test reports are written to `build/reports/tests/test/` by default.

```bash
open build/reports/tests/test/index.html   # macOS
xdg-open build/reports/tests/test/index.html  # Linux
```

The XML reports under `build/test-results/` are useful for CI failures where HTML is unavailable.

## Step 5: Isolate the Failure

Ask the user to try running the failing test in isolation:

```bash
./gradlew test --tests "com.example.MyFailingTest"
./gradlew test --tests "com.example.MyFailingTest.my test name"
```

If the test passes in isolation but fails as part of the full suite, the issue is likely
ordering-dependent state (see `IsolationMode`) or a shared extension side-effect.

## Step 6: Check ProjectConfig Resolution

Kotest scans the classpath for a class at `io.kotest.provided.ProjectConfig`. If configuration
changes appear to have no effect, verify discovery with:

```bash
KOTEST_DEBUG=true ./gradlew test 2>&1 | grep -i "projectconfig\|config"
```

The package `io.kotest.provided` is the default scan location. If a custom location is used, it
must be set via:

```bash
./gradlew test -Dkotest.framework.config.fqn=com.example.MyProjectConfig
```

## Step 7: Reproduce with a Minimal Example and report

If the issue is still unclear, ask the user to create a minimal reproduction:

1. Create a new Gradle project with only the relevant Kotest dependencies
2. Copy the failing spec with the minimum code needed to trigger the bug
3. Confirm the failure reproduces
4. ask the user to share it with the kotest team with a brief summary of the issue and steps to reproduce via https://github.com/kotest/kotest/issues/new/choose

A reproduction that uses `StringSpec` or `FunSpec` with no external dependencies is ideal.

## Common Debug Scenarios

### Tests not discovered

```bash
KOTEST_DEBUG=true ./gradlew test 2>&1 | grep -i "spec\|discover\|scan"
```

Check:
- `useJUnitPlatform()` is present in the `test` task configuration (JVM)
- For KMP: KSP plugin and Kotest Gradle plugin are both applied
- The spec class is not abstract and extends a Kotest spec style

### Extension or lifecycle callback not firing

```bash
KOTEST_DEBUG=true ./gradlew test 2>&1 | grep -i "extension\|listener\|before\|after"
```

Check:
- Extension is registered via `extension(...)` inside the spec, or globally in `ProjectConfig`
- `@AutoScan` was removed in Kotest 6.0 — explicit registration is required

### Timeout or coroutine hang

Add a short timeout to isolate the hanging test:

```kotlin
test("my test").config(timeout = 5.seconds) {
    // ...
}
```

Use `blockingTest = true` if the test body calls blocking (non-suspending) code.

### Property test failure — reproducing a specific seed

When a property test fails, Kotest prints the failing seed:

```
Attempting to shrink arg...
Caused by: Property test failed for inputs listed below after 42 attempts
Repeat this test by using seed 8168214184

```

Reproduce deterministically with:

```kotlin
checkAll(PropTestConfig(seed = 8168214184)) { ... }
```
