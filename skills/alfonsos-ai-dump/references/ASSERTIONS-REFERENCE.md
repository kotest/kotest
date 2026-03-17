# Kotest Assertions Reference

Complete catalog of matchers and assertion utilities in Kotest.

---

## Core Assertions (`kotest-assertions-core`)

### General

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

obj shouldBe expected                     // equality (with detailed diff)
obj shouldNotBe unexpected                // inequality
obj.shouldBeEqual(other)                  // simple equality (for primitives, short collections)
obj::prop.shouldHaveValue(expected)       // property value assertion with info on failure
expr.shouldBeTrue()                       // boolean true
expr.shouldBeFalse()                      // boolean false
```

### Exceptions

```kotlin
import io.kotest.assertions.throwables.*

shouldThrow<IllegalArgumentException> { block }       // throws T or subtype
shouldThrowExactly<IllegalArgumentException> { block } // throws exactly T
shouldThrowAny { block }                               // throws any Throwable
shouldThrowMessage("msg") { block }                    // throws with exact message
shouldNotThrowAny { block }                            // does not throw
```

### Types

```kotlin
import io.kotest.matchers.types.*

obj.shouldBeInstanceOf<String>()          // is T or subtype
obj.shouldBeTypeOf<String>()              // is exactly T
obj.shouldBeSameInstanceAs(other)         // reference equality
obj.shouldBeNull()                        // is null
obj.shouldNotBeNull()                     // is not null (smart-casts)
```

### Comparables

```kotlin
import io.kotest.matchers.comparables.*

comp.shouldBeLessThan(other)
comp.shouldBeLessThanOrEqualTo(other)
comp.shouldBeGreaterThan(other)
comp.shouldBeGreaterThanOrEqualTo(other)
comp.shouldBeEqualComparingTo(other)
comp.shouldBeBetween(lower, upper)        // inclusive both ends
```

---

## String Matchers

```kotlin
import io.kotest.matchers.string.*

str.shouldBeEmpty()
str.shouldBeBlank()
str.shouldBeLowerCase()
str.shouldBeUpperCase()
str.shouldContain("sub")                  // case-sensitive
str.shouldContain(regex)                  // regex match
str.shouldContainIgnoringCase("SUB")
str.shouldContainOnlyDigits()
str.shouldContainADigit()
str.shouldContainOnlyOnce("sub")
str.shouldStartWith("prefix")
str.shouldEndWith("suffix")
str.shouldMatch(regex)                    // full match
str.shouldHaveLength(5)
str.shouldHaveMinLength(3)
str.shouldHaveMaxLength(10)
str.shouldHaveLineCount(3)
str.shouldHaveSameLengthAs("other")
str.shouldBeEqualIgnoringCase("OTHER")
str.shouldBeInteger()                     // returns the parsed Int
str.shouldBeTruthy()                      // "true", "yes", "y", "1"
str.shouldBeFalsy()                       // "false", "no", "n", "0"
str.shouldContainInOrder("a", "b", "c")   // substrings in order
```

---

## Collection Matchers

```kotlin
import io.kotest.matchers.collections.*

collection.shouldBeEmpty()
collection.shouldBeUnique()
collection.shouldContain(element)
collection.shouldContainAll(e1, e2, e3)              // any order, may have extras
collection.shouldContainExactly(e1, e2, e3)          // exact order, no extras
collection.shouldContainExactlyInAnyOrder(e1, e2)    // any order, no extras
collection.shouldContainNoNulls()
collection.shouldContainNull()
collection.shouldContainOnlyNulls()
collection.shouldContainDuplicates()
collection.shouldHaveSize(3)
collection.shouldHaveAtLeastSize(2)
collection.shouldHaveAtMostSize(5)
collection.shouldBeSingleton()
collection.shouldBeSingleton { it shouldBe "only" }
collection.shouldHaveSingleElement("only")
collection.shouldBeSmallerThan(otherCollection)
collection.shouldBeLargerThan(otherCollection)
collection.shouldBeSameSizeAs(otherCollection)
collection.shouldHaveLowerBound(element)             // Comparable elements
collection.shouldHaveUpperBound(element)

// Lists
list.shouldBeSorted()
list.shouldBeSortedBy { it.name }
list.shouldContainInOrder(sublist)
list.shouldStartWith(prefix)
list.shouldEndWith(suffix)
list.shouldHaveElementAt(index, element)
list.shouldExistInOrder({ it > 0 }, { it < 0 })

// Values
value.shouldBeOneOf(collection)                      // reference check
value.shouldBeIn(collection)                         // value check
collection.shouldContainAnyOf(otherCollection)
```

---

## Map Matchers

```kotlin
import io.kotest.matchers.maps.*

map.shouldContain("key", "value")
map.shouldContainAll(otherMap)
map.shouldContainExactly(otherMap)
map.shouldContainKey("key")
map.shouldContainKeys("key1", "key2")
map.shouldContainValue("value")
map.shouldContainValues("v1", "v2")
map.shouldBeEmpty()
map.shouldMatchAll("k1" to { it shouldBe "v1" })
map.shouldMatchExactly("k1" to { it shouldBe "v1" })
```

---

## Number Matchers

```kotlin
import io.kotest.matchers.ints.*      // or longs, doubles, floats
import io.kotest.matchers.doubles.*

int.shouldBeBetween(1, 10)
int.shouldBeLessThan(5)
int.shouldBeGreaterThan(0)
int.shouldBeEven()
int.shouldBeOdd()
int.shouldBeZero()
int.shouldBeInRange(1..100)

double.shouldBe(3.14 plusOrMinus 0.01)   // tolerance for floating point
double.shouldBePositive()
double.shouldBeNegative()
double.shouldBeNaN()
double.shouldBePositiveInfinity()
```

---

## Inspectors

Test elements of a collection:

```kotlin
import io.kotest.inspectors.*

list.forAll { it.shouldBePositive() }            // every element
list.forNone { it.shouldBeNegative() }           // no element
list.forOne { it shouldBe 42 }                   // exactly one
list.forAtLeastOne { it shouldBe 42 }            // at least one
list.forAtLeast(3) { it.shouldBePositive() }     // at least k
list.forAtMost(2) { it.shouldBeNegative() }      // at most k
list.forExactly(2) { it shouldBe 42 }            // exactly k
list.forSome { it.shouldBePositive() }           // between 1 and n-1
list.forAny { it.shouldBePositive() }            // alias for forAtLeastOne
list.filterMatching { it.shouldBePositive() }    // returns matching elements
```

---

## Soft Assertions

Collects all failures instead of stopping at the first:

```kotlin
import io.kotest.assertions.assertSoftly

assertSoftly {
    name shouldBe "John"
    age shouldBe 30
    email.shouldContain("@")
}

// With receiver
assertSoftly(person) {
    name shouldBe "John"
    age shouldBe 30
}
```

**Note**: Only Kotest's own assertions are soft-assertion-aware. Wrap other libraries' assertions in `shouldNotThrowAnyUnit { }`.

**Note**: Use `failSoftly("message")` instead of `fail("message")` inside `assertSoftly` blocks.

---

## Clues

Add context to assertion failures:

```kotlin
import io.kotest.assertions.withClue

withClue("checking user ${user.id}") {
    user.name shouldBe "John"
}

// Property-style clue
user.asClue {
    it.name shouldBe "John"
    it.age shouldBe 30
}
```

---

## Non-Deterministic Testing

### Eventually

Retries until the assertion passes or timeout is reached:

```kotlin
import io.kotest.assertions.nondeterministic.eventually

eventually(5.seconds) {
    repository.findById(id).status shouldBe "DONE"
}

// With config
val config = eventuallyConfig {
    duration = 5.seconds
    interval = 250.milliseconds
    retries = 20
}
eventually(config) { /* ... */ }
```

### Continually

Asserts a condition remains true for a duration:

```kotlin
import io.kotest.assertions.nondeterministic.continually

continually(2.seconds) {
    server.isHealthy().shouldBeTrue()
}
```

### Retry

Retries a block a fixed number of times:

```kotlin
import io.kotest.assertions.nondeterministic.retry

retry(maxRetry = 3, timeout = 10.seconds) {
    api.call().status shouldBe 200
}
```

---

## Custom Matchers

```kotlin
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// 1. Define the matcher function
fun beValidEmail() = Matcher<String> { value ->
    MatcherResult(
        value.contains("@") && value.contains("."),
        { "$value should be a valid email" },
        { "$value should not be a valid email" },
    )
}

// 2. Use with should/shouldNot
"user@example.com" should beValidEmail()
"not-an-email" shouldNot beValidEmail()

// 3. Create extension functions for cleaner syntax
fun String.shouldBeValidEmail(): String {
    this should beValidEmail()
    return this
}

fun String.shouldNotBeValidEmail(): String {
    this shouldNot beValidEmail()
    return this
}

// 4. Use
"user@example.com".shouldBeValidEmail()
```

---

## JSON Matchers (`kotest-assertions-json`)

```kotlin
import io.kotest.assertions.json.*

jsonString.shouldEqualJson(expectedJson)
jsonString.shouldContainJsonKey("$.user.name")
jsonString.shouldContainJsonKeyValue("$.user.age", 30)
jsonString.shouldMatchJsonResource("/expected.json")
```

---

## Ktor Matchers (`kotest-assertions-ktor`)

```kotlin
import io.kotest.assertions.ktor.*

response.shouldHaveStatus(HttpStatusCode.OK)
response.shouldHaveContent("expected body")
response.shouldHaveContentType(ContentType.Application.Json)
response.shouldHaveHeader("X-Custom", "value")
```

---

## Arrow Matchers (`kotest-assertions-arrow`)

```kotlin
import io.kotest.assertions.arrow.core.*

either.shouldBeRight()
either.shouldBeRight(expectedValue)
either.shouldBeLeft()
option.shouldBeSome()
option.shouldBeSome(expectedValue)
option.shouldBeNone()
```

