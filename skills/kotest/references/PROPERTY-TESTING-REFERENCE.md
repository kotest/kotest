# Kotest Property Testing Reference

`kotest-property` runs on JVM, JS, WasmJS, WasmWASI and Native and can be used from any test framework.

| Module                         | Package                          | Contents                                  |
|--------------------------------|----------------------------------|-------------------------------------------|
| `kotest-property`              | `io.kotest.property`, `io.kotest.property.arbitrary`, `io.kotest.property.exhaustive` | `forAll`, `checkAll`, `Arb`, `Exhaustive` |
| `kotest-property-permutations` | `io.kotest.permutations`         | 6.2+ permutations DSL (experimental)       |
| `kotest-property-datetime`     | `io.kotest.property.kotlinx.datetime` | kotlinx-datetime arbs                 |
| `kotest-property-arrow`        | `io.kotest.property.arrow.core`  | Arrow arbs                                |

---

## forAll / checkAll

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlin.math.abs

class Props : FunSpec({
   test("forAll returns a Boolean") {
      forAll<String, String> { a, b -> (a + b).length == a.length + b.length }
   }
   test("checkAll uses assertions") {
      checkAll<Int> { n -> abs(n.toLong()) shouldBeGreaterThanOrEqual 0L }
   }
   test("explicit generators") {
      checkAll(Arb.int(1..100), Arb.string(5..20)) { age, name -> /* ... */ }
   }
})
```

| Form                                    | Max arity |
|-----------------------------------------|-----------|
| type parameters only: `checkAll<A, B>`  | 12        |
| explicit generators: `checkAll(arbA, …)`| 22        |

Iterations default to 1000 (`kotest.proptest.default.iteration.count` system property, or
`PropertyTesting.defaultIterationCount`). Per call: `checkAll<Int>(10_000) { }`, `forAll(500, Arb.int()) { }`.
A type argument or generator is always required — `checkAll(100) { }` does not compile.

---

## Generators

All in `io.kotest.property.arbitrary` unless noted.

| Generator                                      | Notes                                                    |
|------------------------------------------------|----------------------------------------------------------|
| `Arb.int()`, `Arb.int(1..100)`                 | edge cases: range ends plus -1, 0, 1 when in range       |
| `Arb.positiveInt()`, `Arb.negativeInt()`, `Arb.nonNegativeInt()` |                                        |
| `Arb.long()`, `Arb.long(range)`, `Arb.short()`, `Arb.byte()` |                                            |
| `Arb.double()`                                 | includes NaN and ±Infinity (`includeNaNs = false` to drop NaN) |
| `Arb.numericDouble(min, max)`                  | finite doubles only (no range overload)                  |
| `Arb.float()`, `Arb.boolean()`, `Arb.char()`   |                                                          |
| `Arb.string()`                                 | 0..100 printable ASCII chars                              |
| `Arb.string(size)`, `Arb.string(5..20)`        |                                                          |
| `Arb.stringPattern("[a-z]{3}\\d")`             | JVM only                                                 |
| `Arb.email()`, `Arb.ipAddressV4()`             | `String`                                                 |
| `Arb.uuid()`                                   | `PlatformUuid` (= `java.util.UUID` on JVM), not `String` |
| `Arb.list(arb)`, `Arb.list(arb, 1..10)`        |                                                          |
| `Arb.set(arb)`, `Arb.set(arb, 1..10)`          |                                                          |
| `Arb.map(keyArb, valueArb)`                    |                                                          |
| `Arb.enum<MyEnum>()`                           |                                                          |
| `Arb.element(list)`, `Arb.of(a, b, c)`         | pick from values                                         |
| `Arb.choice(arb1, arb2)`                       | pick from arbs                                           |
| `Arb.pair(a, b)`, `Arb.triple(a, b, c)`        |                                                          |
| `Arb.bind(a, b, …) { x, y -> T(x, y) }`        | compose into a class; `Arb.bind<T>()` reflects (JVM)     |
| `arb.orNull()`, `arb.orNull(0.2)`              | nullable; arg is null probability                        |
| `Arb.localDate()`, `Arb.localDateTime()`, `Arb.localTime()`, `Arb.period()` | `java.time`, core module, JVM only |
| `Arb.duration()`                               | `kotlin.time.Duration`, core module                      |
| `Arb.date()`, `Arb.datetime()`, `Arb.instant()` | `kotest-property-datetime` (kotlinx-datetime)           |
| `Arb.either(l, r)`, `Arb.option(arb)`, `Arb.nonEmptyList(arb)` | `kotest-property-arrow`                  |

---

## Custom Generators

```kotlin
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.IntShrinker

val personArb = Arb.bind(Arb.string(3..50), Arb.int(1..120), Arb.email()) { name, age, email ->
   Person(name, age, email)
}

val evenInt = arbitrary {
   val n = Arb.int().bind()       // bind() draws from another arb
   if (n % 2 == 0) n else n - 1
}

val percent = arbitrary(edgecases = listOf(0, 50, 100)) { Arb.int(0..100).bind() }

val withShrinker = arbitrary(shrinker = IntShrinker(1..1000)) { rs -> rs.random.nextInt(1, 1000) }

val lambdaShrinker = arbitrary(shrinker = { v: Int -> listOf(v / 2, v - 1).filter { it in 1..999 } }) { rs ->
   rs.random.nextInt(1, 1000)
}
```

### Operations

```kotlin
Arb.positiveInt().map { it * 2 }
Arb.list(Arb.int(), 1..10).flatMap { list -> Arb.element(list).map { list to it } }
Arb.int().filter { it % 2 != 0 }               // use sparingly
Arb.positiveInt().merge(Arb.negativeInt())
```

---

## Configuration

```kotlin
import io.kotest.property.EdgeConfig
import io.kotest.property.PropTestConfig

checkAll(
   PropTestConfig(
      seed = 12345L,
      iterations = 5000,
      minSuccess = 4900,
      maxFailure = 3,
      edgeConfig = EdgeConfig(edgecasesGenerationProbability = 0.1),
   ),
   Arb.int(),
   Arb.string(),
) { i, s -> /* ... */ }
```

On failure Kotest prints `Repeat this test by using seed <n>`; replay with
`checkAll<Int>(PropTestConfig(seed = <n>)) { ... }`. Failed seeds are also written to disk and reused on the next run.

---

## Shrinking

Failing inputs are shrunk automatically to a minimal counterexample. Built-in shrinkers cover primitives, strings and
collections; custom arbs need a `Shrinker` (a `fun interface` with `shrink(value): List<A>`). Don't return the
degenerate case first.

---

## Assumptions and Statistics

```kotlin
import io.kotest.property.assume

checkAll<Int, Int> { a, b ->
   assume(b != 0)                  // discard this iteration
   (a * b) / b shouldBe a
}

checkAll<Int> { n ->
   collect(if (n >= 0) "non-negative" else "negative")   // default label
   collect("parity", if (n % 2 == 0) "even" else "odd")  // labelled
}
```

`collect` is a member of the `PropertyContext` receiver; the distribution is printed after the run.

---

## Permutations DSL (6.2+, experimental)

Add `kotest-property-permutations`. Generators are named delegates and the body goes in `check { }`.

```kotlin
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.permutations.permconfig
import io.kotest.permutations.permutations
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

@OptIn(ExperimentalKotest::class)
val defaults = permconfig {
   iterations = 500
   maxDiscardPercentage = 10
}

@OptIn(ExperimentalKotest::class)
class CommutativityTest : FunSpec({
   test("addition is commutative") {
      permutations(defaults) {
         val a by gen { Arb.int() }
         val b by gen { Arb.int() }
         iterations = 200                         // overrides the shared default
         check {
            assume(a != b)
            classify(a % 2 == 0, "even", "odd")
            (a + b) shouldBe (b + a)
         }
      }
   }
})
```

| Option (`var` on `PermutationConfiguration`) | Default | Meaning                                      |
|----------------------------------------------|---------|----------------------------------------------|
| `iterations`                                 | 1000    | number of permutations                       |
| `duration`                                   | null    | run until elapsed (overrides `iterations`)   |
| `minSuccess` / `maxFailures`                 | — / 0   | success / failure thresholds                 |
| `maxDiscardPercentage`                       | 20      | max % discarded by `assume`                  |
| `seed`, `failOnSeed`, `writeFailedSeed`      | null, false, true | seed control                       |
| `shouldPrintConfig`, `shouldPrintGeneratedValues`, `shouldPrintShrinkSteps` | false, false, true | output |
| `statisticsReportMode`                       | `ON`    | `ON`, `SUCCESS`, `FAILED`, `OFF`             |

---

## Outside the Kotest Framework

```kotlin
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class MyJUnit5Test {
   @Test
   fun stringLength() {            // block body: a @Test that returns a value is silently skipped by JUnit
      runBlocking {
         forAll<String, String> { a, b -> (a + b).length == a.length + b.length }
      }
   }
}
```
