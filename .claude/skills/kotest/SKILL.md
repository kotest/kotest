---
name: kotest
description: >
  Kotest expert assistant. Use when writing, reviewing, or debugging Kotest tests.
  Helps with spec styles, matchers, property testing, data-driven tests, lifecycle
  hooks, and Kotest-specific idioms.
argument-hint: "[what you want help with]"
allowed-tools: Read, Grep, Glob
---

You are an expert on Kotest, the Kotlin multiplatform testing framework. Help the user
with $ARGUMENTS, applying the knowledge and patterns below.

## Spec Styles

All spec styles are functionally equivalent — choose based on team preference.

### StringSpec (simplest)
```kotlin
class MyTests : StringSpec({
  "string length should be correct" { "hello".length shouldBe 5 }
})
```

### FunSpec (most common)
```kotlin
class MyTests : FunSpec({
  context("a calculator") {
    test("adds two numbers") { (2 + 2) shouldBe 4 }
  }
})
```

### DescribeSpec (familiar to JS/Ruby devs)
```kotlin
class MyTests : DescribeSpec({
  describe("a calculator") {
    it("adds two numbers") { (2 + 2) shouldBe 4 }
    xit("pending test") { }   // xtest/xit = disabled
  }
})
```

### BehaviorSpec (Gherkin-like)
```kotlin
class MyTests : BehaviorSpec({
  given("a user") {
    `when`("they log in") {
      then("they see their dashboard") { /* ... */ }
    }
  }
})
```

### ShouldSpec
```kotlin
class MyTests : ShouldSpec({
  "a string" {
    should("have correct length") { "hello".length shouldBe 5 }
  }
})
```

### FreeSpec (arbitrary nesting with `-`)
```kotlin
class MyTests : FreeSpec({
  "outer" - {
    "inner" - {
      "a test" { 1 + 1 shouldBe 2 }
    }
  }
})
```

### WordSpec
```kotlin
class MyTests : WordSpec({
  "a string" should {
    "have length 5" { "hello" shouldHaveLength 5 }
  }
})
```

### FeatureSpec
```kotlin
class MyTests : FeatureSpec({
  feature("user login") {
    scenario("valid credentials succeed") { /* ... */ }
  }
})
```

### AnnotationSpec (JUnit-style)
```kotlin
class MyTests : AnnotationSpec() {
  @Test fun `addition works`() { (1 + 1) shouldBe 2 }
  @BeforeEach fun setup() { }
}
```

---

## Matchers

Prefer infix matchers for readability. All matchers are also available as extension
functions with `should` prefix (e.g., `value.shouldBe(5)`).

### Core
```kotlin
value shouldBe expected
value shouldNotBe expected
value.shouldBeNull()
value.shouldNotBeNull()
obj.shouldBeInstanceOf<String>()
```

### Strings
```kotlin
str shouldContain "sub"
str shouldStartWith "pre"
str shouldEndWith "suf"
str shouldHaveLength 10
str.shouldBeEmpty()
str.shouldMatch(Regex("pattern"))
str.shouldBeBlank()
```

### Collections
```kotlin
list shouldContain item
list shouldHaveSize 3
list.shouldBeEmpty()
list shouldContainExactly listOf(1, 2, 3)
list shouldContainAll listOf(1, 2)
map shouldContainKey "key"
map shouldContainValue "val"
```

### Collection Inspectors
```kotlin
list.forAll { it.age shouldBeGreaterThan 18 }
list.forExactly(2) { it.active shouldBe true }
list.forSome { it.name shouldStartWith "A" }
list.forAny { it.score shouldBeGreaterThan 90 }
list.forNone { it.deleted shouldBe true }
```

### Numeric
```kotlin
n shouldBeGreaterThan 0
n shouldBeLessThan 100
n shouldBeInRange 1..10
d.shouldBeWithinPercentageOf(expected, 5.0)
```

### Exceptions
```kotlin
shouldThrow<IllegalArgumentException> { riskyCall() }
shouldThrowExactly<CustomException> { riskyCall() }
shouldNotThrow<Exception> { safeCall() }

val ex = shouldThrow<IllegalArgumentException> { riskyCall() }
ex.message shouldContain "expected message"
```

### Soft Assertions (collect all failures)
```kotlin
assertSoftly {
  name shouldBe "Alice"
  age shouldBe 30
  active shouldBe true
}
```

### Custom Matchers
```kotlin
fun beValid() = Matcher<String> { value ->
  MatcherResult(
    value.isNotBlank() && value.length > 3,
    { "Expected $value to be valid" },
    { "Expected $value to be invalid" }
  )
}
"hello" should beValid()
```

---

## Property-Based Testing

```kotlin
class PropertyTests : FunSpec({
  test("addition is commutative") {
    checkAll<Int, Int> { a, b -> (a + b) shouldBe (b + a) }
  }

  test("string reverse is idempotent") {
    checkAll(Arb.string()) { s -> s.reversed().reversed() shouldBe s }
  }
})
```

### Common Arbitraries (Arb)
```kotlin
Arb.int()                          // includes edge cases: 0, Int.MIN_VALUE, etc.
Arb.int(min = 1, max = 100)
Arb.long()
Arb.double()
Arb.string()
Arb.string(minSize = 1, maxSize = 50)
Arb.email()
Arb.boolean()
Arb.list(Arb.int())
Arb.set(Arb.string(), range = 1..10)
Arb.map(Arb.string(), Arb.int())
Arb.enum<MyEnum>()
Arb.localDate()
```

### Custom Arbitraries
```kotlin
fun arbPerson(): Arb<Person> = arbitrary {
  Person(
    name = Arb.string(minSize = 1).bind(),
    age  = Arb.int(1..120).bind()
  )
}

checkAll(arbPerson()) { person -> person.age shouldBeGreaterThan 0 }
```

### Exhaustive Generators
```kotlin
checkAll(Exhaustive.ints(1..5)) { n -> n shouldBeInRange 1..5 }
checkAll(Exhaustive.boolean())  { b -> /* tests true and false */ }
```

### Property Config
```kotlin
checkAll(iterations = 1000, seed = 42) { a: Int, b: Int ->
  a + b shouldBe b + a
}
```

---

## Lifecycle Hooks

```kotlin
class MyTests : FunSpec({
  beforeSpec  { /* once before all tests */ }
  afterSpec   { /* once after all tests  */ }
  beforeTest  { /* before each test      */ }
  afterTest   { result -> /* after each  */ }
  beforeContainer { /* before each context/describe block */ }
  afterContainer  { /* after each context/describe block  */ }

  test("my test") { }
})
```

---

## Test Config & Tagging

```kotlin
// Disable a test
test("pending").config(enabled = false) { }

// Conditional enable
test("linux only").config(enabledIf = { Os.isLinux() }) { }

// Tags
object Slow : Tag()
test("slow test").config(tags = setOf(Slow)) { }

// Timeout
test("time-bounded").config(timeout = 5.seconds) { }

// Invocations (run multiple times)
test("flaky check").config(invocations = 3) { }
```

---

## Data-Driven Testing

```kotlin
class DataTests : FunSpec({
  withData(
    Pair("hello", 5),
    Pair("hi", 2),
  ) { (str, len) ->
    str shouldHaveLength len
  }
})
```

Or with named data classes for clearer test names:
```kotlin
data class TestCase(val input: String, val expected: Int)

withData(
  TestCase("hello", 5),
  TestCase("hi",    2),
) { (input, expected) ->
  input shouldHaveLength expected
}
```

---

## Coroutines Support

Kotest tests are coroutine-aware by default on the JVM — no `runBlocking` needed:
```kotlin
test("suspending function works") {
  val result = mySuspendFun()
  result shouldBe expected
}
```

---

## Gradle Setup (Kotest 6.x)

```kotlin
// build.gradle.kts
dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:6.x.x")
  testImplementation("io.kotest:kotest-assertions-core:6.x.x")
  testImplementation("io.kotest:kotest-property:6.x.x")   // for property testing
}

tasks.test {
  useJUnitPlatform()
}
```

---

## Key Principles & Tips

- **Spec style**: pick one per project for consistency; FunSpec is the most common.
- **Test names**: write them as plain English sentences — they double as documentation.
- **assertSoftly**: use when checking multiple fields of the same object so all
  failures are reported at once.
- **Property testing**: use for algorithmic code where properties should hold for
  all inputs. Start with `checkAll<T>()` and add `Arb` constraints as needed.
- **Custom matchers**: create them when a pattern repeats more than twice.
- **Coroutines**: never wrap tests in `runBlocking`; Kotest handles the dispatcher.
- **Tagging**: use `@IncludeTags`/`@ExcludeTags` on the JUnit platform or Gradle's
  `systemProperty("kotest.tags", "Slow & !Integration")`.
