# Kotest Migration Guide

Mappings from JUnit 4, JUnit 5, TestNG and Spek to Kotest (FunSpec shown unless noted). Data-driven variants for other
spec styles, tags and config: [FRAMEWORK-REFERENCE.md](FRAMEWORK-REFERENCE.md).

---

## JUnit 5 → Kotest

| JUnit 5                          | Kotest                                                   |
|----------------------------------|----------------------------------------------------------|
| `@Test fun name()`               | `test("name") { }`                                       |
| `@Nested inner class Group`      | `context("Group") { }`                                   |
| `@Disabled("reason")`            | `xtest("name") { }` or `.config(enabled = false)`        |
| `@Tag("slow")`                   | `object Slow : Tag()` + `.config(tags = setOf(Slow))`    |
| `@Timeout(5, SECONDS)`           | `.config(timeout = 5.seconds)`                           |
| `@RepeatedTest(3)`               | `.config(invocations = 3)`                               |
| `@ParameterizedTest`             | `withTests(...)` (FunSpec/FreeSpec, `io.kotest.datatest`) |
| `@BeforeEach` / `@AfterEach`     | `beforeEach { }` / `afterEach { }`                       |
| `@BeforeAll` / `@AfterAll`       | `beforeSpec { }` / `afterSpec { }`                       |
| `@TempDir`                       | `tempdir()` / `tempfile()` (JVM, `io.kotest.engine.spec`) |
| `assertEquals(expected, actual)` | `actual shouldBe expected`                               |
| `assertNotEquals(a, b)`          | `a shouldNotBe b`                                        |
| `assertTrue(x)` / `assertFalse(x)` | `x.shouldBeTrue()` / `x.shouldBeFalse()`               |
| `assertNull(x)` / `assertNotNull(x)` | `x.shouldBeNull()` / `x.shouldNotBeNull()`           |
| `assertThrows<E> { }`            | `shouldThrow<E> { }`                                     |
| `assertDoesNotThrow { }`         | `shouldNotThrowAny { }`                                  |
| `assertAll(...)`                 | `assertSoftly { }`                                       |
| `assertIterableEquals(a, b)`     | `a shouldContainExactly b`                               |

### Parameterized Tests

```kotlin
// JUnit 5
@ParameterizedTest
@CsvSource("1,1,2", "2,3,5")
fun add(a: Int, b: Int, expected: Int) = assertEquals(expected, add(a, b))
```

```kotlin
// Kotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe

data class AddCase(val a: Int, val b: Int, val expected: Int)

class AddTest : FunSpec({
   context("addition") {
      withTests(
         AddCase(1, 1, 2),
         AddCase(2, 3, 5),
      ) { (a, b, expected) ->
         add(a, b) shouldBe expected
      }
   }
})
```

Each row becomes its own test, named from the data class. The vararg form needs at least two rows; pass a `List` for
one. On JS/Wasm/Native the innermost data scope must be a leaf (`withTests`), see
[KNOWN-ISSUES.md §7](KNOWN-ISSUES.md#7-data-tests-on-js--wasm--native).

### Full Example

```kotlin
// JUnit 5 (before)
class CalculatorTest {
   private lateinit var calc: Calculator

   @BeforeEach
   fun setup() { calc = Calculator() }

   @Test
   fun `should add`() { assertEquals(4, calc.add(2, 2)) }

   @Test
   fun `should throw on div by zero`() { assertThrows<ArithmeticException> { calc.divide(1, 0) } }

   @Nested
   inner class Subtraction {
      @Test
      fun `should subtract`() { assertEquals(3, calc.subtract(5, 2)) }
   }
}
```

```kotlin
// Kotest (after)
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculatorTest : FunSpec({
   lateinit var calc: Calculator
   beforeEach { calc = Calculator() }

   test("should add") { calc.add(2, 2) shouldBe 4 }

   test("should throw on div by zero") {
      shouldThrow<ArithmeticException> { calc.divide(1, 0) }
   }

   context("Subtraction") {
      test("should subtract") { calc.subtract(5, 2) shouldBe 3 }
   }
})
```

`beforeEach` runs before leaf tests only; use `beforeTest` / `beforeAny` to include containers.

---

## JUnit 4 → Kotest

| JUnit 4                    | Kotest                          |
|----------------------------|---------------------------------|
| `@Test`                    | `test("name") { }`              |
| `@Before` / `@After`       | `beforeEach { }` / `afterEach { }` |
| `@BeforeClass` / `@AfterClass` | `beforeSpec { }` / `afterSpec { }` |
| `@Ignore`                  | `xtest("name") { }`             |
| `@Rule TemporaryFolder`    | `tempdir()` / `tempfile()`      |
| `@Test(expected = E::class)` | `shouldThrow<E> { }`          |
| `@RunWith(Parameterized)`  | `withTests(...)`                |
| `Assert.assertSame`        | `shouldBeSameInstanceAs`        |

Android instrumented tests stay on JUnit 4: `kotest-runner-junit4` + `@RunWith(KotestTestRunner::class)`
([SETUP-REFERENCE.md](SETUP-REFERENCE.md#android)).

---

## TestNG → Kotest

| TestNG                                  | Kotest                                       |
|-----------------------------------------|----------------------------------------------|
| `@Test(groups = ["slow"])`              | `.config(tags = setOf(Slow))`                |
| `@BeforeMethod` / `@AfterMethod`        | `beforeEach { }` / `afterEach { }`           |
| `@BeforeClass` / `@AfterClass`          | `beforeSpec { }` / `afterSpec { }`           |
| `@BeforeSuite` / `@AfterSuite`          | `beforeProject()` / `afterProject()` in `AbstractProjectConfig` |
| `@DataProvider`                         | `withTests(...)`                             |
| `Assert.assertEquals(actual, expected)` | `actual shouldBe expected`                   |
| `Assert.assertEqualsNoOrder(a, b)`      | `a shouldContainExactlyInAnyOrder b`         |

---

## Spek → Kotest (DescribeSpec)

| Spek                          | Kotest                                         |
|-------------------------------|------------------------------------------------|
| `object Spec : Spek({})`      | `class Spec : DescribeSpec({})`                |
| `describe` / `context` / `it` | same keywords                                  |
| `xit`                         | `xit`                                          |
| `val x by memoized { }`       | `lateinit var x` + `beforeEach { x = … }`      |
| `beforeEachTest` / `afterEachTest` | `beforeEach` / `afterEach`                |

```kotlin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CalculatorSpec : DescribeSpec({
   describe("a calculator") {
      lateinit var calculator: Calculator
      beforeEach { calculator = Calculator() }
      it("should add") { calculator.add(2, 2) shouldBe 4 }
   }
})
```

---

## Workflow

1. Add Kotest alongside the existing framework — both run on JUnit Platform (keep `useJUnitPlatform()`; JUnit
   Jupiter tests need their own engine dependency).
2. Migrate one class at a time: extend a spec style, move tests into the constructor lambda.
3. Replace assertions, lifecycle and parameterized tests using the tables above.
4. Remove old imports, run `./gradlew test`, and confirm the test count did not drop.
