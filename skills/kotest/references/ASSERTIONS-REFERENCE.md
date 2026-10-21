# Kotest Assertions Reference

Catalog of matchers and assertion utilities. Most matchers return the receiver (or a narrowed value) so they chain.

---

## Modules

| Module                                 | Package                                  | Platforms           |
|----------------------------------------|------------------------------------------|---------------------|
| `kotest-assertions-core`               | `io.kotest.matchers.*`, `io.kotest.assertions.*`, `io.kotest.inspectors.*` | all |
| `kotest-assertions-json`               | `io.kotest.assertions.json`              | all (JSONPath/resource matchers JVM only) |
| `kotest-assertions-yaml`               | `io.kotest.assertions.yaml`              | all                 |
| `kotest-assertions-kotlinx-datetime`   | `io.kotest.matchers.kotlinx.datetime`    | all                 |
| `kotest-assertions-arrow`              | `io.kotest.assertions.arrow.core`        | all                 |
| `kotest-assertions-arrow-fx-coroutines`| `io.kotest.assertions.arrow.fx.coroutines` | all               |
| `kotest-assertions-konform`            | `io.kotest.assertions.konform`           | all                 |
| `kotest-assertions-ktor`               | `io.kotest.assertions.ktor.client`       | JVM, JS             |
| `kotest-assertions-jsoup`              | `io.kotest.assertions.jsoup`             | JVM                 |
| `kotest-assertions-compiler`           | `io.kotest.matchers.compilation`         | JVM                 |
| `kotest-assertions-table`              | `io.kotest.data` (4.x-style `forAll(row(...))` tables) | JVM, JS, Wasm, Native |

---

## Equality

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.properties.shouldHaveValue

actual shouldBe expected        // structural equality with diff; actual/expected types may differ
actual shouldNotBe other
actual shouldEqual expected     // like shouldBe but both sides must share a type (compile-time check)
actual shouldBeEqual expected   // plain equals(), non-null types
user::name shouldHaveValue "Al" // property name appears in the failure
flag.shouldBeTrue()
flag.shouldBeFalse()
```

### Per-call custom equality (`withEqs`)

```kotlin
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.eq.Eq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.eq.shouldBe
import io.kotest.assertions.eq.withEqs
import java.math.BigDecimal

object IgnoreScale : Eq<BigDecimal> {
   override fun equals(actual: BigDecimal, expected: BigDecimal, context: EqContext): EqResult =
      if (actual.compareTo(expected) == 0) EqResult.Success
      else EqResult.Failure { AssertionErrorBuilder.create().withMessage("$actual != $expected (ignoring scale)").build() }
}

BigDecimal("3.14") withEqs { register<BigDecimal>(IgnoreScale) } shouldBe BigDecimal("3.140")
```

The override only applies to that one comparison (also inside nested collections, maps and data classes) and is safe
when specs run in parallel. `shouldNotBe` works the same way.

### Field-by-field comparison (JVM)

```kotlin
import io.kotest.matchers.equality.shouldBeEqualUsingFields

actual shouldBeEqualUsingFields expected
actual shouldBeEqualUsingFields {
   excludedProperties = setOf(User::id, User::createdAt)  // or includedProperties (not both)
   overrideMatchers = mapOf(User::email to matchStringsIgnoringCase) // property -> Assertable
   expected
}
```

Other options on the config block: `ignorePrivateFields` (default `true`), `ignoreComputedFields` (default `true`),
`useDefaultShouldBeForFields`.

---

## Exceptions

```kotlin
import io.kotest.assertions.throwables.*

val e = shouldThrow<IllegalArgumentException> { block } // T or subtype; returns the exception
shouldThrowExactly<IllegalArgumentException> { block }  // exactly T
shouldThrowAny { block }                                // any Throwable
shouldThrowMessage("msg") { block }                     // exact message
val v = shouldNotThrowAny { block }                     // returns the block's value
shouldNotThrowAnyUnit { block }                         // for Unit blocks
```

---

## Types, Nulls, Results

```kotlin
import io.kotest.matchers.types.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.result.*

val s: String = obj.shouldBeInstanceOf<String>()  // is T or subtype; returns T and smart-casts
val s2 = obj shouldBeInstanceOf String::class      // KClass overload, returns T
obj.shouldBeTypeOf<String>()                      // exactly T (runtime class), subclasses fail
obj.shouldHaveConcreteType<String>()              // alias of shouldBeTypeOf with a more explicit name
obj shouldBeSameInstanceAs other                  // reference equality
obj.shouldBeNull()
val nn = nullable.shouldNotBeNull()               // returns non-null T, smart-casts

val value = result.shouldBeSuccess()              // Result<T> -> T
val err = result.shouldBeFailure<IllegalStateException> { it.message shouldBe "x" } // returns E
```

---

## Comparables and Numbers

```kotlin
import io.kotest.matchers.comparables.*
import io.kotest.matchers.ints.*        // also longs, doubles, floats, bytes, shorts
import io.kotest.matchers.doubles.plusOrMinus

c shouldBeLessThan other               // also ...OrEqualTo, shouldBeGreaterThan(...OrEqualTo)
c.shouldBeEqualComparingTo(other)
c.shouldBeBetween(lower, upper)        // inclusive

n.shouldBeBetween(1, 10)
n.shouldBeInRange(1..100)
n.shouldBeEven(); n.shouldBeOdd(); n.shouldBeZero()
n.shouldBePositive(); n.shouldBeNegative()
d shouldBe (3.14 plusOrMinus 0.01)     // floating-point tolerance
d.shouldBeNaN(); d.shouldBePositiveInfinity()
```

---

## Strings

```kotlin
import io.kotest.matchers.string.*

str.shouldBeEmpty(); str.shouldBeBlank()
str.shouldBeLowerCase(); str.shouldBeUpperCase()
str shouldContain "sub"; str shouldContain Regex("a+")
str.shouldContainIgnoringCase("SUB")
str.shouldContainOnlyDigits(); str.shouldContainADigit()
str.shouldContainOnlyOnce("sub")
str.shouldContainInOrder("a", "b", "c")
str shouldStartWith "pre"; str shouldEndWith "suf"
str shouldMatch Regex("[a-z]+")        // whole string
str.shouldHaveLength(5); str.shouldHaveMinLength(3); str.shouldHaveMaxLength(10)
str.shouldHaveLineCount(3); str.shouldHaveSameLengthAs("other")
str shouldBeEqualIgnoringCase "OTHER"
val i: Int = str.shouldBeInteger()     // returns parsed Int
str.shouldBeTruthy()                   // "true", "yes", "y", "1"; shouldBeFalsy for the opposite
```

---

## Collections

```kotlin
import io.kotest.matchers.collections.*

c.shouldBeEmpty(); c.shouldBeUnique(); c.shouldContainDuplicates()
c shouldContain e
c.shouldContainAll(e1, e2)                 // any order, extras allowed
c.shouldContainExactly(e1, e2)             // order + no extras
c.shouldContainExactlyInAnyOrder(e1, e2)
c.shouldContainAnyOf(e1, e2)
c.shouldContainNoNulls(); c.shouldContainNull(); c.shouldContainOnlyNulls()
c.shouldHaveSize(3); c.shouldHaveAtLeastSize(2); c.shouldHaveAtMostSize(5)
c shouldBeSmallerThan other; c shouldBeLargerThan other; c shouldBeSameSizeAs other
val only = c.shouldBeSingle()              // returns the element
c.shouldBeSingleton { it shouldBe "x" }
c.shouldHaveLowerBound(min); c.shouldHaveUpperBound(max)

list.shouldBeSorted(); list.shouldBeSortedBy { it.name }
list.shouldContainInOrder(sublist)
list.shouldStartWith(prefix); list.shouldEndWith(suffix)
list.shouldHaveElementAt(index, element)
list.shouldExistInOrder({ it > 0 }, { it < 0 })

x shouldBeIn collection                    // equality
x shouldBeOneOf collection                 // same instance
```

## Maps

```kotlin
import io.kotest.matchers.maps.*

map.shouldContain("key", "value"); map shouldContain ("key" to "value")
map.shouldContainKey("k"); map.shouldContainKeys("k1", "k2")
map.shouldContainValue("v"); map.shouldContainValues("v1", "v2")
map.shouldContainAll(other); map.shouldContainExactly(other)
map.shouldMatchAll("k1" to { it shouldBe "v1" })
map.shouldMatchExactly("k1" to { it shouldBe "v1" })
```

---

## Inspectors

All are infix and return the collection (except `filterMatching`):

```kotlin
import io.kotest.inspectors.*

list forAll { it.shouldBePositive() }        // every element
list forNone { it.shouldBeNegative() }
list forOne { it shouldBe 42 }              // exactly one
list forAtLeastOne { it shouldBe 42 }       // forAny is an alias
list.forAtLeast(3) { it.shouldBePositive() }
list.forAtMost(2) { it.shouldBeNegative() }
list.forExactly(2) { it shouldBe 42 }
list forSome { it.shouldBePositive() }      // 1..n-1 elements
val passing: List<Int> = list filterMatching { it.shouldBePositive() }
```

The failure message lists every passing and failing element.

---

## Soft Assertions and Clues

```kotlin
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.asClue
import io.kotest.assertions.withClue

assertSoftly {
   name shouldBe "John"
   age shouldBe 30
}
assertSoftly(person) {           // receiver form
   name shouldBe "John"
}

withClue("user ${user.id}") { user.name shouldBe "John" }
user.asClue { it.age shouldBe 30 }
```

Non-Kotest assertions and `fail()` bypass soft mode — see [KNOWN-ISSUES.md §4](KNOWN-ISSUES.md#4-assertsoftly-not-collecting-failures).
Project-wide soft mode (`globalAssertSoftly`): [FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md).

---

## Non-Deterministic Testing

```kotlin
import io.kotest.assertions.nondeterministic.continually
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.assertions.retry
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

eventually(5.seconds) { repo.findById(id).status shouldBe "DONE" }   // until it passes

val config = eventuallyConfig {
   duration = 5.seconds
   interval = 250.milliseconds
   retries = 20
}
eventually(config) { /* ... */ }

continually(2.seconds) { server.isHealthy().shouldBeTrue() }          // must keep passing

retry(maxRetry = 3, timeout = 10.seconds, delay = 1.seconds) {        // fixed number of attempts
   api.call().status shouldBe 200
}
```

`eventually` always uses wall-clock time, even under `coroutineTestScope` / `runTest`.

---

## Custom Matchers

```kotlin
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// simple
fun beValidEmail() = Matcher<String> { value ->
   MatcherResult(
      value.contains("@"),
      { "$value should be a valid email" },
      { "$value should not be a valid email" },
   )
}

// with expected/actual -> IntelliJ "click to see difference"
fun haveName(expected: String) = Matcher<User> { user ->
   MatcherResultBuilder.create(user.name == expected)
      .withValues(expected = { expected.print() }, actual = { user.name.print() })
      .withFailureMessage { "user name should be $expected but was ${user.name}" }
      .withNegatedFailureMessage { "user name should not be $expected" }
      .build()
}

fun String.shouldBeValidEmail(): String = apply { this should beValidEmail() }
fun String.shouldNotBeValidEmail(): String = apply { this shouldNot beValidEmail() }
```

Compose with `beA and beB`, `beA or beB`, and adapt with `matcher.contramap<User> { it.email }`.

---

## Power Assert

Shows each sub-expression's value when a `shouldBe` fails.

```kotlin
// Kotest Gradle plugin 6.2+: configures the Kotlin power-assert plugin for you
kotest {
   enablePowerAssert = true
}

// or manually
plugins { id("org.jetbrains.kotlin.plugin.power-assert") version "<kotlin-version>" }
powerAssert { functions = listOf("io.kotest.matchers.shouldBe") }
```

---

## Module Examples

```kotlin
// JSON
import io.kotest.assertions.json.*
json shouldEqualJson """{"a":1}"""
json.shouldContainJsonKey("$.user.name")          // JVM
json.shouldContainJsonKeyValue("$.user.age", 30)  // JVM

// YAML
import io.kotest.assertions.yaml.shouldEqualYaml
yaml shouldEqualYaml expectedYaml

// Ktor client
import io.kotest.assertions.ktor.client.*
response shouldHaveStatus HttpStatusCode.OK
response shouldHaveContentType ContentType.Application.Json
response.shouldHaveHeader("X-Custom", "value")

// Jsoup
import io.kotest.assertions.jsoup.*
document shouldHaveElementWithId "main"

// Arrow
import io.kotest.assertions.arrow.core.*
val r = either.shouldBeRight()                    // returns the right value
either.shouldBeLeft()
val v = option.shouldBeSome()
option.shouldBeNone()

// 4.x tables (kotest-assertions-table)
import io.kotest.data.forAll
import io.kotest.data.row
forAll(row(1, 2, 3), row(2, 3, 5)) { a, b, sum -> a + b shouldBe sum }
```
