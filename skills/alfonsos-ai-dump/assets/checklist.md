# Kotest Setup Verification Checklist

Use this checklist after setting up Kotest or migrating from another test framework.

## Build & Dependencies
- [ ] `./gradlew test` succeeds with no errors
- [ ] `./gradlew test` reports tests discovered and executed (not 0 tests)
- [ ] No dependency conflicts between Kotest and other test frameworks
- [ ] All Kotest module versions match (don't mix 5.x and 6.x)
- [ ] Kotlin version is 2.2+ (required for Kotest 6.x)

## JVM Setup
- [ ] `useJUnitPlatform()` present in `tasks.withType<Test>()` configuration
- [ ] `kotest-runner-junit5` dependency added to `testImplementation`
- [ ] Kotest Gradle plugin applied (optional but recommended for enhanced IDE support)

## Multiplatform Setup
- [ ] KSP plugin applied before Kotest plugin in `plugins {}` block
- [ ] `kotest-framework-engine` added to `commonTest` dependencies
- [ ] Kotest Gradle plugin applied (`id("io.kotest")`)
- [ ] Tests placed in `commonTest` or platform-specific test source sets

## Test Discovery
- [ ] Test classes extend a Kotest Spec style (e.g., `FunSpec`, `DescribeSpec`)
- [ ] Tests are inside `init {}` block or class body lambda
- [ ] IntelliJ shows gutter icons for running individual tests
- [ ] Test report shows correct test names and hierarchy

## Assertions
- [ ] `kotest-assertions-core` dependency added
- [ ] Assertions produce clear failure messages with expected vs actual diffs
- [ ] IntelliJ click-to-diff works on assertion failures
- [ ] `assertSoftly` collects multiple failures when used

## Property Testing (if applicable)
- [ ] `kotest-property` dependency added
- [ ] `forAll` / `checkAll` tests execute with default 1000 iterations
- [ ] Custom generators produce expected value ranges
- [ ] Shrinking produces minimal failing examples

## Project Configuration
- [ ] `ProjectConfig` class (if used) is at `io.kotest.provided.ProjectConfig` or configured via `kotest.framework.config.fqn`
- [ ] Global settings (timeout, assertion mode, etc.) applied correctly
- [ ] Extensions registered explicitly (not via `@AutoScan` which is removed in 6.0)

## Migration (if applicable)
- [ ] No stale JUnit/TestNG imports in migrated test files
- [ ] All `@Test` methods converted to Kotest test DSL
- [ ] `@BeforeEach`/`@AfterEach` converted to `beforeEach`/`afterEach`
- [ ] `@BeforeAll`/`@AfterAll` converted to `beforeSpec`/`afterSpec`
- [ ] `assertEquals`/`assertTrue` replaced with `shouldBe`/`shouldBeTrue()`
- [ ] `@ParameterizedTest` replaced with `withTests(...)` data-driven testing
- [ ] `@Disabled` replaced with `xtest` or `.config(enabled = false)`
- [ ] `@Tag` replaced with Kotest `Tag` objects and `.config(tags = ...)`

## IDE Support
- [ ] Kotest IntelliJ plugin installed (recommended)
- [ ] Individual tests can be run from gutter icons
- [ ] Test results tree shows proper hierarchy
- [ ] Failed test output shows clickable diffs

## Advanced Features (if used)
- [ ] Tags filter correctly via `-Dkotest.tags="..."` or `KOTEST_TAGS` env var
- [ ] Isolation mode behaves as expected (`SingleInstance` vs `InstancePerRoot`)
- [ ] Timeouts applied correctly (test-level and project-level)
- [ ] Concurrency settings work as expected (`SpecConcurrencyMode`, `TestConcurrencyMode`)
- [ ] Data-driven tests generate individual test entries in the report
- [ ] Non-deterministic helpers (`eventually`, `continually`, `retry`) work as expected
- [ ] `autoClose` properly closes resources after spec execution

