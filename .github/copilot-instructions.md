# Kotest - GitHub Copilot Instructions

## Project Overview

Kotest is a Kotlin Multiplatform testing framework with three pillars: a test framework (9+ spec styles), an assertion/matchers library, and a property-based testing engine. It targets JVM, JS, WasmJS, WasmWASI, and Native platforms.

## Code Style

- **Indentation**: 3 spaces (not the Kotlin default of 4). This applies throughout the entire codebase.
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html) otherwise.
- Minimize mutability; prefer `val` over `var`, immutable collections over mutable.
- Choose self-explanatory names. Avoid abbreviations unless they are universally understood (e.g., `rs` for `RandomSource` in property testing generators).

## Source Set Layout

This is a Kotlin Multiplatform project. Source sets follow the standard KMP layout:
- `src/commonMain/kotlin/` -- shared code for all platforms
- `src/jvmMain/kotlin/` -- JVM-specific code
- `src/jsMain/kotlin/` -- JS-specific code
- `src/nativeMain/kotlin/` -- Native-specific code
- Platform-specific test sets: `commonTest`, `jvmTest`, `jsTest`, etc.

When writing new code, place it in the most general source set possible. Only use platform-specific source sets when the code depends on platform APIs.

## Writing Tests

Kotest tests itself using its own framework. When writing tests:

```kotlin
// Preferred: FunSpec for most test files
class MyFeatureTest : FunSpec({
   test("should do something") {
      result shouldBe expected
   }

   test("should handle edge case") {
      shouldThrow<IllegalArgumentException> {
         functionUnderTest(invalidInput)
      }
   }
})
```

- Use `shouldBe`, `shouldNot`, `shouldThrow`, and other kotest assertion functions.
- Use `assertSoftly { }` when you want to collect multiple assertion failures.
- Use `eventually { }` for testing asynchronous behavior.
- For data-driven tests, use `withData` inside a FunSpec or DescribeSpec.

## Writing Matchers

When implementing a new `Matcher<T>`:

```kotlin
fun bePositive() = Matcher<Int> { value ->
   MatcherResult.Companion(
      passed = value > 0,
      failureMessageFn = { "$value should be positive" },
      negatedFailureMessageFn = { "$value should not be positive" },
   )
}

// Extension functions for the should DSL
fun Int.shouldBePositive() = this should bePositive()
fun Int.shouldNotBePositive() = this shouldNot bePositive()
```

Prefer `DiffableMatcherResult` when you have distinct actual/expected values, as it enables IntelliJ's "Click to see difference" feature.

## Writing Property-Based Tests

```kotlin
class MyPropertyTest : FunSpec({
   test("addition is commutative") {
      checkAll(Arb.int(), Arb.int()) { a, b ->
         a + b shouldBe b + a
      }
   }
})
```

When creating custom Arb generators:
- Override both `edgecase(rs: RandomSource)` and `sample(rs: RandomSource)`.
- Provide meaningful edge cases (e.g., 0, -1, MAX_VALUE for numeric types).
- Consider implementing a `Shrinker<A>` to help find minimal failing cases.

## Writing Extensions

Extensions modify engine behavior; listeners receive events without changing behavior:

```kotlin
// Listener (preferred for simple callbacks)
class MyListener : BeforeTestListener {
   override suspend fun beforeTest(testCase: TestCase) {
      // setup logic
   }
}

// Extension (for intercepting execution)
class MyExtension : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      // wrap execution
      return execute(testCase)
   }
}
```

## Module Boundaries

- `kotest-assertions-shared` contains the `Matcher` interface and core assertion infrastructure. Do not add specific matchers here.
- `kotest-assertions-core` contains all standard matchers. New matchers for basic types go here.
- `kotest-property` contains the `Gen`/`Arb`/`Exhaustive` hierarchy and built-in generators.
- `kotest-framework-engine` contains spec styles, lifecycle, configuration, and the execution engine.
- `kotest-extensions/kotest-extensions-*` each wrap a specific third-party library.

## API Compatibility

The project uses Kotlin Binary Compatibility Validator. After any public API change:
1. Run `./gradlew apiDump` to update `.api` files.
2. Commit the updated API dump files.
3. Mark internal APIs with `@KotestInternal` to exclude them from validation.

## Annotations to Know

- `@ExperimentalKotest` -- opt-in annotation for experimental features
- `@KotestInternal` -- public for operational reasons but not for end-user consumption
- `@DelicateKotest` -- advanced features requiring careful usage
- `@JVMOnly` -- features only available on JVM
- `@SoftDeprecated` -- features that are soft-deprecated (will be removed in a future major version)

## Build Commands

```bash
./gradlew check                                    # Full check
./gradlew check -PjvmOnly=true                     # JVM-only (fast)
./gradlew :kotest-assertions:kotest-assertions-core:check  # Single module
./gradlew apiDump                                  # Regenerate API dumps
./gradlew apiCheck                                 # Validate API compatibility
```
