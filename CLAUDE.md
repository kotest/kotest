# Kotest - Working with this Codebase

## What is Kotest

Kotest is a Kotlin Multiplatform testing framework comprising three pillars: a test framework with 9+ spec styles, an assertion/matchers library, and a property-based testing engine. It targets JVM, JS, WasmJS, WasmWASI, and all major Native platforms (Linux, macOS, Windows, iOS, tvOS, watchOS).

## Build and Test

```bash
# Full check (all platforms enabled locally by default)
./gradlew check

# JVM-only check (much faster for most changes)
./gradlew check -PjvmOnly=true

# Single module
./gradlew :kotest-assertions:kotest-assertions-core:check
./gradlew :kotest-framework:kotest-framework-engine:jvmTest

# API compatibility check (required before PR)
./gradlew apiCheck

# Regenerate API dump after public API changes
./gradlew apiDump
```

Gradle properties `kotest_enableKotlinJs` and `kotest_enableKotlinNative` in `gradle.properties` control which targets are built. Set to `false` for faster local iteration when working on JVM-only code.

The project requires JVM heap of 8GB (`-Xmx8g` in `gradle.properties`). Gradle parallel execution and caching are enabled by default.

## Project Structure

```
kotest-common/                          # Shared annotations (@ExperimentalKotest, @KotestInternal, etc.)
kotest-framework/
  kotest-framework-engine/              # Core engine: spec styles, lifecycle, config, extensions
  kotest-framework-standalone/          # Fat jar for standalone execution
  kotest-framework-plugin-gradle/       # Gradle plugin for running tests outside Gradle's test task
  kotest-framework-symbol-processor/    # KSP processor for generating KMP test entries
kotest-assertions/
  kotest-assertions-shared/             # Matcher<T>, MatcherResult, assertSoftly, error collectors
  kotest-assertions-core/              # All standard matchers + nondeterministic helpers (eventually, continually)
  kotest-assertions-json/              # JSON matchers
  kotest-assertions-ktor/             # Ktor HTTP matchers
  kotest-assertions-arrow/            # Arrow matchers
  kotest-assertions-compiler/         # Compile-time assertion matchers
  kotest-assertions-table/            # Legacy data/table testing from kotest 4.x
  kotest-assertions-konform/          # Konform validation matchers
  kotest-assertions-kotlinx-datetime/ # kotlinx-datetime matchers
  kotest-assertions-yaml/            # YAML matchers
kotest-property/                       # Property testing: Gen, Arb, Exhaustive, shrinking, seed persistence
  kotest-property-lifecycle/          # Property testing + framework lifecycle integration
  kotest-property-permutations/       # 6.0+ permutations DSL
  kotest-property-arrow/              # Arrow generators
  kotest-property-datetime/           # kotlinx-datetime generators
kotest-extensions/                     # Third-party integrations (Spring, Testcontainers, Allure, Koin, etc.)
kotest-runner/
  kotest-runner-junit-platform/       # Shared JUnit Platform support
  kotest-runner-junit5/               # Primary JVM runner (Gradle/IntelliJ integration)
  kotest-runner-junit4/               # Android instrumented test runner
  kotest-runner-junit6/               # JUnit6 runner
kotest-intellij-plugin/               # IntelliJ IDEA plugin
kotest-bom/                           # Bill of Materials
kotest-tests/                          # Integration/regression test suites (each is a separate Gradle module)
buildSrc/                             # Convention plugins and CI configuration
```

## Coding Conventions

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html) with **3-space indentation** (not 4).
- Minimize mutability.
- Choose self-explanatory names.
- Source sets follow KMP layout: `commonMain`, `jvmMain`, `jsMain`, `nativeMain`, `commonTest`, `jvmTest`, etc.
- The base package is `io.kotest`. Test code historically also uses `com.sksamuel.kotest` (legacy package from pre-rename era).

## Key Architectural Concepts

### Spec Styles
Each spec style (FunSpec, StringSpec, BehaviorSpec, DescribeSpec, ShouldSpec, WordSpec, FreeSpec, FeatureSpec, ExpectSpec) is an abstract class that extends `DslDrivenSpec -> Spec`. The DSL is injected via scope interfaces (e.g., `FunSpecRootScope`) which define the test registration methods. All styles are functionally equivalent -- they differ only in DSL syntax.

### Matcher Architecture
The `Matcher<T>` interface has a single `test(value: T): MatcherResult` method. Results come in three variants:
- `SimpleMatcherResult` -- basic pass/fail with message lambdas
- `DiffableMatcherResult` -- includes actual/expected for IntelliJ diff links
- `ThrowableMatcherResult` -- carries a pre-built exception

Build results using `MatcherResultBuilder`. Matchers compose via `and`/`or` infix functions and `contramap` for type adaptation.

### Extension vs Listener
Extensions modify engine behavior (they intercept and can alter execution). Listeners receive events without changing behavior. Types: `TestCaseExtension`, `SpecExtension`, `ProjectExtension`, `MountableExtension`, `TagExtension`, `EnabledExtension`, etc. Listeners: `BeforeTestListener`, `AfterTestListener`, `BeforeSpecListener`, `AfterSpecListener`, `BeforeProjectListener`, `AfterProjectListener`, etc.

### Property Testing
`Gen<A>` is a sealed class with two subclasses: `Arb<A>` (random values with edge cases and shrinking) and `Exhaustive<A>` (closed-space enumeration). Arbs provide `edgecase()` and `sample()` methods. Property tests support up to 22 generators. The `kotest-property-permutations` module adds a newer permutation-based DSL (6.0+).

### Project Configuration
Global config is via a class extending `AbstractProjectConfig` at `io.kotest.provided.ProjectConfig`. Supports isolation modes, concurrency settings, timeouts, assertion modes, test ordering, and more. On JVM, the config class FQN can be overridden with `kotest.framework.config.fqn` system property.

### Conditional Module Inclusion
`settings.gradle.kts` conditionally includes modules based on CI/OS/branch. JVM-only modules skip non-Linux CI. Linux-only extensions (Spring, Testcontainers, Allure, etc.) only build on Linux runners or locally.

## Binary Compatibility

The project uses [Kotlin Binary Compatibility Validator](https://github.com/kotlin/binary-compatibility-validator/). API dump files live in each module's `api/` directory. Any public API change requires running `./gradlew apiDump` and committing the updated `.api` files. The `@KotestInternal` annotation marks internal APIs excluded from validation.

## CI/CD

- PR checks run on GitHub Actions with a matrix: JVM-only, JS+Wasm, Native (Linux), Native (Windows), Native (macOS).
- API validation (`apiCheck`) runs as a separate job before the test matrix.
- Documentation changes (under `documentation/`) skip test jobs and only validate the Docusaurus build.
- The `Ci.kt` buildSrc file controls version computation and platform flags.
- Releases publish to Maven Central via the nmcp aggregation plugin.

## Test Isolation

`kotest-tests/` contains many small Gradle modules rather than one large test module. This is intentional -- each module can have its own system properties, project config, and JVM settings without interfering with others. When adding integration tests that require specific JVM configuration, create a new test module.

## Common Tasks

### Adding a new matcher
1. Add to the appropriate module in `kotest-assertions/` (usually `kotest-assertions-core`).
2. Implement `Matcher<T>` with proper `failureMessage` and `negatedFailureMessage`.
3. Prefer `DiffableMatcherResult` when actual/expected values are available.
4. Add both `should` and `shouldNot` extension functions.
5. Write tests in the corresponding `jvmTest` and/or `commonTest` source set.
6. Run `./gradlew apiDump` if the matcher is public API.

### Adding a new extension
1. Create a new module under `kotest-extensions/` if the dependency is substantial.
2. Implement the appropriate extension interface from `io.kotest.core.extensions`.
3. Consider whether it needs to be registered globally via `AbstractProjectConfig.extensions` or per-spec.

### Adding a new Arb generator
1. Add to `kotest-property/src/commonMain/` (or `jvmMain` for JVM-specific types).
2. Extend `Arb<A>` with `edgecase()` and `sample()` implementations.
3. Consider providing meaningful edge cases for the type.
4. Implement a `Shrinker<A>` if the type can be meaningfully shrunk.
