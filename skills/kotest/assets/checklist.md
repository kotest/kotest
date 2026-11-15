# Kotest Setup Verification Checklist

Use after setting up Kotest or migrating from another framework.

## Build & Dependencies
- [ ] `./gradlew test` (or `check` for KMP) succeeds and reports a non-zero test count
- [ ] Every `io.kotest` artifact uses the same version (no 5.x / 6.x mix)
- [ ] Kotlin 2.2+ and JDK 11+
- [ ] No removed artifacts (`kotest-framework-datatest`, `kotest-assertions-api`)

## JVM
- [ ] `useJUnitPlatform()` on the test tasks
- [ ] `kotest-runner-junit5` in `testImplementation` (KMP: in `jvmTest`)
- [ ] `io.kotest` Gradle plugin applied if enhanced IntelliJ support / power-assert / `kotest {}` options are wanted

## Multiplatform / Non-JVM
- [ ] `com.google.devtools.ksp` and `io.kotest` plugins applied (KSP first)
- [ ] `kotest-framework-engine` in `commonTest`
- [ ] Data tests end in a leaf scope (e.g. `withTests`) on JS / Wasm / Native
- [ ] ProjectConfig on Native is a `class`, not an `object`

## Test Discovery
- [ ] Specs extend a Kotest style and are not abstract
- [ ] Tests are declared in the constructor lambda or `init {}`
- [ ] Report shows the expected names and nesting

## Assertions
- [ ] `kotest-assertions-core` added
- [ ] Failures show expected vs actual; IntelliJ click-to-diff works
- [ ] `assertSoftly` collects multiple failures (non-Kotest assertions wrapped in `shouldNotThrowAnyUnit`)

## Property Testing (if used)
- [ ] `kotest-property` added
- [ ] Custom arbs produce the expected range; shrinking yields small counterexamples

## Project Configuration
- [ ] ProjectConfig is discoverable ([KNOWN-ISSUES.md §2](../references/KNOWN-ISSUES.md#2-projectconfig-not-picked-up)); `KOTEST_DEBUG=true` log shows it was loaded
- [ ] Extensions registered explicitly (config `extensions`, `extension(...)`, or `@ApplyExtension`)
- [ ] Concurrency uses `specExecutionMode` / `testExecutionMode`

## Migration (if applicable)
- [ ] No stale JUnit / TestNG imports in migrated files
- [ ] `@Test` → test DSL, `@BeforeEach`/`@AfterEach` → `beforeEach`/`afterEach`, `@BeforeAll`/`@AfterAll` → `beforeSpec`/`afterSpec`
- [ ] `@ParameterizedTest` → data tests (`withTests` / style-specific `withXXX`)
- [ ] `@Disabled` → `xtest` or `.config(enabled = false)`; `@Tag` → `Tag` objects + `.config(tags = ...)`
- [ ] Test count before and after migration matches

## IDE
- [ ] Kotest IntelliJ plugin installed; gutter icons run individual tests
- [ ] Results tree shows the spec hierarchy

## Advanced (if used)
- [ ] Tag filtering via `KOTEST_TAGS="..."` or `systemProperty("kotest.tags", ...)` on the test task (`-D` alone is not forwarded)
- [ ] Isolation mode behaves as expected (`SingleInstance` or `InstancePerRoot`)
- [ ] Test- and project-level timeouts applied; blocking code uses `blockingTest = true`
- [ ] Each data-test row appears as its own test in the report
- [ ] `eventually` / `continually` / `retry` durations are realistic (`eventually` always uses wall-clock time)
