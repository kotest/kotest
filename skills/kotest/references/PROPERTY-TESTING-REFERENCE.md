# Kotest Property Testing Reference

Complete reference for property-based testing with Kotest.

---

## Core Concepts

Property testing generates random inputs and verifies that properties (invariants) hold for all of them.

- **Generator (Arb)** — produces random values of a given type
- **Test Function** — `forAll` (returns Boolean) or `checkAll` (uses assertions)
- **Iterations** — default 1000, configurable
- **Shrinking** — on failure, finds the minimal failing input
- **Seed** — deterministic replay of a failing test

---

## Test Functions

### forAll

Returns a Boolean. Test passes if the function returns `true` for all generated inputs.

```kotlin
import io.kotest.property.forAll

test("string concat length") {
  forAll<String, String> { a, b ->
    (a + b).length == a.length + b.length
  }
}
```

### checkAll

Uses assertions. Test passes if no exception is thrown.

```kotlin
import io.kotest.property.checkAll

test("absolute value is non-negative") {
  checkAll<Int> { n ->
    abs(n) shouldBeGreaterThanOrEqualTo 0
  }
}
```

### Arity

Both `forAll` and `checkAll` support up to 14 type parameters:

```kotlin
forAll<String, Int, Boolean> { a, b, c -> /* ... */ }
checkAll<String, Int, Boolean, Double> { a, b, c, d -> /* ... */ }
```

---

## Iterations

Default is 1000. Customize per call:

```kotlin
checkAll<Int>(10_000) { n ->
  // runs 10,000 iterations
}

forAll<String>(500) { s ->
  s.reversed().reversed() == s
}
```

---

## Built-in Generators (Arbs)

### Primitives

| Generator                  | Description                                                 |
|----------------------------|-------------------------------------------------------------|
| `Arb.int()`                | All integers including edge cases (0, MIN_VALUE, MAX_VALUE) |
| `Arb.int(range)`           | Integers in range, e.g., `Arb.int(1..100)`                  |
| `Arb.positiveInt()`        | Positive integers only                                      |
| `Arb.negativeInt()`        | Negative integers only                                      |
| `Arb.long()`               | All longs                                                   |
| `Arb.long(range)`          | Longs in range                                              |
| `Arb.double()`             | All doubles including NaN, infinities                       |
| `Arb.numericDouble()`      | Doubles excluding NaN and infinities                        |
| `Arb.numericDouble(range)` | Doubles in range                                            |
| `Arb.float()`              | All floats                                                  |
| `Arb.boolean()`            | true or false                                               |
| `Arb.byte()`               | All bytes                                                   |
| `Arb.short()`              | All shorts                                                  |
| `Arb.char()`               | All chars                                                   |

### Strings

| Generator                  | Description                              |
|----------------------------|------------------------------------------|
| `Arb.string()`             | Random strings (0..100 chars, printable) |
| `Arb.string(size)`         | Strings of exact size                    |
| `Arb.string(range)`        | Strings with length in range             |
| `Arb.stringPattern(regex)` | Strings matching a regex pattern         |
| `Arb.email()`              | Valid email addresses                    |
| `Arb.uuid()`               | Random UUIDs                             |
| `Arb.ipAddressV4()`        | IPv4 addresses                           |

### Collections

| Generator                   | Description                      |
|-----------------------------|----------------------------------|
| `Arb.list(arb)`             | Lists of elements from `arb`     |
| `Arb.list(arb, range)`      | Lists with size in range         |
| `Arb.set(arb)`              | Sets of elements from `arb`      |
| `Arb.set(arb, range)`       | Sets with size in range          |
| `Arb.map(keyArb, valueArb)` | Maps with random keys and values |

### Enums and Sealed Classes

| Generator                | Description                       |
|--------------------------|-----------------------------------|
| `Arb.enum<MyEnum>()`     | Random enum values                |
| `Arb.choice(arb1, arb2)` | Randomly picks from multiple arbs |

### Combinators

| Generator                                        | Description                            |
|--------------------------------------------------|----------------------------------------|
| `Arb.pair(arbA, arbB)`                           | Random Pair                            |
| `Arb.triple(arbA, arbB, arbC)`                   | Random Triple                          |
| `Arb.bind(arb1, arb2) { a, b -> MyClass(a, b) }` | Compose arbs into a custom type        |
| `arb.orNull()`                                   | Makes any arb nullable                 |
| `arb.orNull(probability)`                        | Nullable with configurable probability |

---

## Specifying Generators Explicitly

Instead of relying on type inference, pass generators as arguments:

```kotlin
test("custom generators") {
  forAll(
    Arb.int(1..100),
    Arb.string(5..20)
  ) { age, name ->
    Person(name, age).isValid()
  }
}
```

---

## Custom Generators

### Using Arb.bind

Compose existing arbs:

```kotlin
val personArb = Arb.bind(
  Arb.string(3..50),
  Arb.int(1..120),
  Arb.email()
) { name, age, email ->
  Person(name, age, email)
}
```

### Using arbitrary builder

Full control with the `arbitrary` DSL:

```kotlin
val evenIntArb = arbitrary { rs ->
  val n = Arb.int().bind()  // generate a random int
  if (n % 2 == 0) n else n + 1
}

// Simpler form
val evenIntArb2 = arbitrary {
  val n = Arb.int().bind()
  if (n % 2 == 0) n else n + 1
}
```

### Using arb builder with edge cases

```kotlin
val percentArb = arbitrary(
  edgecases = listOf(0, 50, 100)
) {
  Arb.int(0..100).bind()
}
```

---

## Generator Operations

### map

Transform generated values:

```kotlin
val positiveEvenArb = Arb.positiveInt().map { it * 2 }
```

### flatMap

Chain generators:

```kotlin
val listWithElement = Arb.list(Arb.int(), 1..10).flatMap { list ->
  Arb.element(list).map { element -> list to element }
}
```

### filter

Filter generated values (use sparingly — can be slow):

```kotlin
val oddArb = Arb.int().filter { it % 2 != 0 }
```

### merge

Combine generators:

```kotlin
val mixedArb = Arb.positiveInt().merge(Arb.negativeInt())
```

---

## Configuration

### PropTestConfig

```kotlin
checkAll(
  PropTestConfig(
    seed = 12345,             // reproducible
    minSuccess = 900,         // minimum passing iterations
    maxFailure = 3,           // fail after this many failures
    iterations = 5000,        // total iterations
    edgeConfig = EdgeConfig(
      edgecasesGenerationProbability = 0.1  // probability of edge cases
    ),
  ),
  Arb.int(),
  Arb.string()
) { i, s ->
  // test logic
}
```

### Seed

When a property test fails, Kotest prints the seed. Replay the failure:

```kotlin
checkAll(PropTestConfig(seed = 843297423L)) { /* ... */ }
```

---

## Shrinking

When a test fails, Kotest automatically shrinks the failing input to the minimal example.

For example, if `Arb.list(Arb.int())` generates `[42, -17, 99, 0, 3]` and the test fails,
Kotest might shrink it to `[0]` if that's the minimal failing case.

Built-in shrinkers exist for all primitive types, strings, and collections.

### Custom Shrinker

```kotlin
val myArb = arbitrary(shrinker = IntShrinker) { rs ->
  rs.random.nextInt()
}

// Or use a lambda shrinker
val myArb2 = arbitrary(
  shrinker = { value -> listOf(0, value / 2, value - 1).filter { it != value } }
) { rs ->
  rs.random.nextInt(1, 1000)
}
```

---

## Assumptions

Skip iterations that don't meet preconditions:

```kotlin
import io.kotest.property.assume

checkAll<Int, Int> { a, b ->
  assume(b != 0)  // skip if b is 0
  (a * b) / b shouldBe a
}
```

---

## Statistics

Collect statistics about generated values:

```kotlin
import io.kotest.property.statistics.statistics

checkAll<Int>(1000) { n ->
  statistics("sign") {
    when {
      n > 0 -> "positive"
      n < 0 -> "negative"
      else -> "zero"
    }
  }
  // test logic
}
// Prints distribution: positive: 49.8%, negative: 49.9%, zero: 0.3%
```

---

## Using Property Tests Outside Kotest Framework

Property tests can be used with any test framework:

```kotlin
// With JUnit 5
class MyJUnit5Test {
  @Test
  fun `string length property`() = runBlocking {
    forAll<String, String> { a, b ->
      (a + b).length == a.length + b.length
    }
  }
}
```

---

## Date/Time Generators (`kotest-property-datetime`)

```kotlin
import io.kotest.property.datetime.*

Arb.localDate()
Arb.localDateTime()
Arb.localTime()
Arb.instant()
Arb.period()
Arb.duration()
```

---

## Arrow Generators (`kotest-property-arrow`)

```kotlin
import io.kotest.property.arrow.*

Arb.either(Arb.string(), Arb.int())     // Either<String, Int>
Arb.option(Arb.int())                    // Option<Int>
Arb.nonEmptyList(Arb.string())           // NonEmptyList<String>
```

