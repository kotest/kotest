# Kotest Migration Guide

Mappings for migrating from JUnit 4, JUnit 5, TestNG, and Spek to Kotest.

---

## JUnit 5 → Kotest

### Annotations → DSL

| JUnit 5 | Kotest (FunSpec) |
|---------|-----------------|
| `@Test fun name()` | `test("name") { }` |
| `@Nested inner class Group` | `context("Group") { }` |
| `@Disabled("reason")` | `xtest("name") { }` |
| `@Tag("slow")` | `.config(tags = setOf(Slow))` |
| `@Timeout(5, SECONDS)` | `.config(timeout = 5.seconds)` |
| `@RepeatedTest(3)` | `.config(invocations = 3)` |

### Lifecycle

| JUnit 5 | Kotest |
|---------|--------|
| `@BeforeEach` | `beforeEach { }` |
| `@AfterEach` | `afterEach { }` |
| `@BeforeAll` | `beforeSpec { }` |
| `@AfterAll` | `afterSpec { }` |

### Assertions

| JUnit 5 | Kotest |
|---------|--------|
| `assertEquals(expected, actual)` | `actual shouldBe expected` |
| `assertNotEquals(a, b)` | `a shouldNotBe b` |
| `assertTrue(expr)` | `expr.shouldBeTrue()` |
| `assertFalse(expr)` | `expr.shouldBeFalse()` |
| `assertNull(obj)` | `obj.shouldBeNull()` |
| `assertNotNull(obj)` | `obj.shouldNotBeNull()` |
| `assertThrows<E> { }` | `shouldThrow<E> { }` |
| `assertDoesNotThrow { }` | `shouldNotThrowAny { }` |
| `assertAll(...)` | `assertSoftly { }` |
| `assertIterableEquals(a, b)` | `a.shouldContainExactly(b)` |

### Parameterized Tests

**JUnit 5:**
```kotlin
@ParameterizedTest
@CsvSource("1,1,2", "2,3,5")
fun testAdd(a: Int, b: Int, expected: Int) {
    add(a, b) shouldBe expected
}
```

**Kotest:**
```kotlin
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

### Full Example

**JUnit 5 (before):**
```kotlin
class CalculatorTest {
    private lateinit var calc: Calculator

    @BeforeEach fun setup() { calc = Calculator() }

    @Test fun `should add`() { assertEquals(4, calc.add(2, 2)) }

    @Test fun `should throw on div by zero`() {
        assertThrows<ArithmeticException> { calc.divide(1, 0) }
    }

    @Nested inner class Subtraction {
        @Test fun `should subtract`() {
            assertEquals(3, calc.subtract(5, 2))
        }
    }
}
```

**Kotest (after):**
```kotlin
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

---

## JUnit 4 → Kotest

| JUnit 4 | Kotest |
|---------|--------|
| `@Test` | `test("name") { }` |
| `@Before` | `beforeEach { }` |
| `@After` | `afterEach { }` |
| `@BeforeClass` | `beforeSpec { }` |
| `@AfterClass` | `afterSpec { }` |
| `@Ignore` | `xtest("name") { }` |
| `@Rule TemporaryFolder` | `tempdir()` / `tempfile()` |
| `@Test(expected=E::class)` | `shouldThrow<E> { }` |
| `@RunWith(Parameterized)` | `withTests(...)` |
| `Assert.assertEquals` | `shouldBe` |
| `Assert.assertTrue` | `shouldBeTrue()` |
| `Assert.assertSame` | `shouldBeSameInstanceAs` |

---

## TestNG → Kotest

| TestNG | Kotest |
|--------|--------|
| `@Test` | `test("name") { }` |
| `@Test(groups=["slow"])` | `.config(tags = setOf(Slow))` |
| `@BeforeMethod` | `beforeEach { }` |
| `@AfterMethod` | `afterEach { }` |
| `@BeforeClass` | `beforeSpec { }` |
| `@AfterClass` | `afterSpec { }` |
| `@BeforeSuite` | `ProjectConfig` with `beforeProject` |
| `@AfterSuite` | `ProjectConfig` with `afterProject` |
| `@DataProvider` | `withTests(...)` |
| `Assert.assertEquals(actual, expected)` | `actual shouldBe expected` |
| `Assert.assertEqualsNoOrder(a, b)` | `a.shouldContainExactlyInAnyOrder(b)` |

---

## Spek → Kotest

Spek maps naturally to `DescribeSpec`:

| Spek | Kotest DescribeSpec |
|------|---------------------|
| `object Spec : Spek({})` | `class Spec : DescribeSpec({})` |
| `describe` / `context` / `it` | Same keywords |
| `xit` | `xit` |
| `val x by memoized { }` | `lateinit var x` + `beforeEach { }` |
| `beforeEachTest` | `beforeEach` |
| `afterEachTest` | `afterEach` |

**Spek (before):**
```kotlin
object CalculatorSpec : Spek({
    describe("a calculator") {
        val calculator by memoized { Calculator() }
        it("should add") { assertEquals(4, calculator.add(2, 2)) }
    }
})
```

**Kotest (after):**
```kotlin
class CalculatorSpec : DescribeSpec({
    describe("a calculator") {
        lateinit var calculator: Calculator
        beforeEach { calculator = Calculator() }
        it("should add") { calculator.add(2, 2) shouldBe 4 }
    }
})
```

---

## Migration Workflow

1. Add Kotest dependencies alongside existing framework
2. Migrate one file at a time — frameworks coexist on JUnit Platform
3. Replace class → extend Kotest spec style
4. Move `@Test` methods → `test("name") { }`
5. Replace assertions → Kotest matchers
6. Replace lifecycle → DSL hooks
7. Replace parameterized → `withTests(...)`
8. Remove old imports
9. Run `./gradlew test` to verify

