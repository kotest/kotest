# Kotest Spec Styles Reference

Complete examples of all 9 Kotest spec styles with nesting, lifecycle hooks, and disabling tests.

---

## FunSpec

General-purpose style. Recommended default. Uses `test` for tests and `context` for grouping.

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecExample : FunSpec({

  test("simple test") {
    1 + 1 shouldBe 2
  }

  context("a group of tests") {
    test("nested test 1") {
      "hello".length shouldBe 5
    }
    test("nested test 2") {
      "world".length shouldBe 5
    }
    context("deeper nesting") {
      test("deeply nested") {
        true shouldBe true
      }
    }
  }

  // Disabled tests
  xtest("this test is disabled") {
    // not executed
  }
  xcontext("this group is disabled") {
    test("also disabled by inheritance") { }
  }

  // Config
  test("with config").config(timeout = 5.seconds, tags = setOf(Slow)) {
    // ...
  }
})
```

---

## DescribeSpec

Familiar to JavaScript/Ruby developers. Uses `describe` / `it`. `context` is an alias for `describe`.

```kotlin
import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecExample : DescribeSpec({

  describe("a calculator") {
    it("should add correctly") {
      1 + 1 shouldBe 2
    }
    describe("subtraction") {
      it("should subtract correctly") {
        10 - 3 shouldBe 7
      }
    }
    context("multiplication") {
      it("should multiply correctly") {
        3 * 4 shouldBe 12
      }
    }
  }

  // Disabled
  xdescribe("disabled group") {
    it("disabled by inheritance") { }
  }
  describe("active group") {
    xit("disabled individual test") { }
  }
})
```

---

## BehaviorSpec

BDD style using `given` / `when` / `then`. Since `when` is a Kotlin keyword, use backticks or uppercase variants.

```kotlin
import io.kotest.core.spec.style.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec({

  context("a user service") {
    given("a registered user") {
      `when`("logging in with correct credentials") {
        then("should return a valid token") {
          // test here
        }
      }
      `when`("logging in with wrong password") {
        then("should throw AuthenticationException") {
          // test here
        }
      }
    }
  }

  // Uppercase variants (no backticks needed)
  Given("a shopping cart") {
    and("it has items") {
      When("checkout is called") {
        and("payment succeeds") {
          Then("order should be created") {
            // test here
          }
        }
      }
    }
  }

  // Disabled
  xgiven("disabled given") {
    When("also disabled") {
      then("also disabled") { }
    }
  }
})
```

---

## StringSpec

Most minimal style. Just a string and a lambda. No nesting support (use FreeSpec instead).

```kotlin
import io.kotest.core.spec.style.StringSpec

class StringSpecExample : StringSpec({

  "string length should return the length" {
    "hello".length shouldBe 5
  }

  "string should be equal ignoring case" {
    "Hello" shouldBeEqualIgnoringCase "hello"
  }

  // With config
  "a configured test".config(timeout = 5.seconds) {
    // ...
  }
})
```

---

## FreeSpec

Arbitrary nesting depth using `-` for containers and no `-` for leaf tests.

```kotlin
import io.kotest.core.spec.style.FreeSpec

class FreeSpecExample : FreeSpec({

  "String.length" - {
    "should return the length of the string" {
      "hello".length shouldBe 5
    }
    "should return 0 for empty string" {
      "".length shouldBe 0
    }
  }

  "containers can nest arbitrarily deep" - {
    "level 2" - {
      "level 3" - {
        "leaf test" {
          1 + 1 shouldBe 2
        }
      }
    }
  }
})
```

**Caution**: The innermost (leaf) test must NOT use `-` after the name.

---

## WordSpec

Uses `should` keyword. Inspired by ScalaTest's WordSpec. Optional `When` for extra nesting.

```kotlin
import io.kotest.core.spec.style.WordSpec

class WordSpecExample : WordSpec({

  "String.length" should {
    "return the length of the string" {
      "hello".length shouldBe 5
    }
    "return 0 for empty strings" {
      "".length shouldBe 0
    }
  }

  "Hello" When {
    "asked for length" should {
      "return 5" {
        "Hello".length shouldBe 5
      }
    }
    "appended to Bob" should {
      "return Hello Bob" {
        "Hello " + "Bob" shouldBe "Hello Bob"
      }
    }
  }
})
```

---

## FeatureSpec

Cucumber-inspired. Uses `feature` and `scenario`.

```kotlin
import io.kotest.core.spec.style.FeatureSpec

class FeatureSpecExample : FeatureSpec({

  feature("user registration") {
    scenario("should accept valid email") {
      // test here
    }
    scenario("should reject invalid email") {
      // test here
    }
  }

  feature("login") {
    scenario("should authenticate valid credentials") {
      // test here
    }
    xscenario("this scenario is disabled") {
      // not executed
    }
  }

  xfeature("this entire feature is disabled") {
    scenario("disabled by inheritance") { }
  }
})
```

---

## ExpectSpec

Uses `expect` keyword. Supports `context` for grouping.

```kotlin
import io.kotest.core.spec.style.ExpectSpec

class ExpectSpecExample : ExpectSpec({

  context("a calculator") {
    expect("simple addition") {
      1 + 1 shouldBe 2
    }
    expect("integer overflow") {
      // test here
    }
  }

  expect("standalone expect") {
    true shouldBe true
  }

  // Disabled
  xexpect("disabled test") { }
  xcontext("disabled group") {
    expect("disabled by inheritance") { }
  }
})
```

---

## ShouldSpec

Uses `should` keyword. Supports `context` for grouping.

```kotlin
import io.kotest.core.spec.style.ShouldSpec

class ShouldSpecExample : ShouldSpec({

  should("return the length of the string") {
    "hello".length shouldBe 5
  }

  context("String operations") {
    should("concatenate strings") {
      "hello" + " " + "world" shouldBe "hello world"
    }
    should("convert to uppercase") {
      "hello".uppercase() shouldBe "HELLO"
    }
  }

  // Disabled
  xshould("disabled test") { }
  xcontext("disabled group") {
    should("disabled by inheritance") { }
  }
})
```

---

## Choosing a Style — Quick Guide

| If you want...                                  | Use            |
|-------------------------------------------------|----------------|
| Simple, general purpose                         | `FunSpec`      |
| Minimal boilerplate                             | `StringSpec`   |
| Unlimited nesting depth but minimal boilerplate | `FreeSpec`     |
| BDD / Gherkin feel                              | `BehaviorSpec` |
| JavaScript/RSpec familiarity                    | `DescribeSpec` |
| Cucumber-like feature files                     | `FeatureSpec`  |
| ScalaTest WordSpec familiarity                  | `WordSpec`     |
| `should` keyword with grouping                  | `ShouldSpec`   |
| `expect` keyword with grouping                  | `ExpectSpec`   |

All styles support the same configuration options (tags, timeouts, enabled/disabled, invocations, etc.) via
`.config(...)`.

