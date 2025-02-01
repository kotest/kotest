---
id: styles
title: Testing Styles
slug: testing-styles.html
---


Kotest offers 10 different styles of test layout. Some are inspired from other popular test frameworks to make you feel right at home.
Others were created just for Kotest. 

To use Kotest, create a class file that extends one of the test styles. Then inside an `init { }` block,
create your test cases. The following table contains the test styles you can pick from along with examples.

There are no functional differences between the styles.
All allow the same types of configuration &mdash; threads, tags, etc &mdash;
it is simply a matter of preference how you structure your tests.


| Test Style | Inspired By |
| --- | --- |
| [Fun Spec](#fun-spec) | ScalaTest |
| [Describe Spec](#describe-spec) | Javascript frameworks and RSpec |
| [Should Spec](#should-spec) | A Kotest original |
| [String Spec](#string-spec) | A Kotest original |
| [Behavior Spec](#behavior-spec) | BDD frameworks |
| [Free Spec](#free-spec) | ScalaTest |
| [Word Spec](#word-spec) | ScalaTest |
| [Feature Spec](#feature-spec) | Cucumber |
| [Expect Spec](#expect-spec) | A Kotest original |
| [Annotation Spec (JVM only)](#annotation-spec) | JUnit |



:::tip
Some teams prefer to mandate usage of a single style, others mix and match. There is no right or wrong - do whatever feels right for your team.
:::


## Fun Spec

`FunSpec` allows you to create tests by invoking a function called `test` with a string argument to describe the test,
and then the test itself as a lambda. If in doubt, this is the style to use.

```kotlin
class MyTests : FunSpec({
    test("String length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})
```


Tests can be disabled using the `xcontext` and `xtest` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : FunSpec({
    context("this outer block is enabled") {
        xtest("this test is disabled") {
            // test here
        }
    }
    xcontext("this block is disabled") {
        test("disabled by inheritance from the parent") {
            // test here
        }
    }
})
```


## String Spec

`StringSpec` reduces the syntax to the absolute minimum.
 Just write a string followed by a lambda expression with your test code.

```kotlin
class MyTests : StringSpec({
    "strings.length should return size of string" {
        "hello".length shouldBe 5
    }
})
```

Adding config to the test.

```kotlin
class MyTests : StringSpec({
    "strings.length should return size of string".config(enabled = false, invocations = 3) {
        "hello".length shouldBe 5
    }
})
```



## Should Spec

`ShouldSpec` is similar to fun spec, but uses the keyword `should` instead of `test`.

```kotlin
class MyTests : ShouldSpec({
    should("return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})
```


Tests can be nested in one or more context blocks as well:

```kotlin
class MyTests : ShouldSpec({
    context("String.length") {
        should("return the length of the string") {
            "sammy".length shouldBe 5
            "".length shouldBe 0
        }
    }
})
```




Tests can be disabled using the `xcontext` and `xshould` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : ShouldSpec({
    context("this outer block is enabled") {
        xshould("this test is disabled") {
            // test here
        }
    }
    xcontext("this block is disabled") {
        should("disabled by inheritance from the parent") {
            // test here
        }
    }
})
```




## Describe Spec

`DescribeSpec` offers a style familiar to those from a Ruby or Javascript background, as this testing style
 uses `describe` / `it` keywords. Tests must be nested in one or more `describe` blocks.

```kotlin
class MyTests : DescribeSpec({
    describe("score") {
        it("start as zero") {
            // test here
        }
        describe("with a strike") {
            it("adds ten") {
                // test here
            }
            it("carries strike to the next frame") {
                // test here
            }
        }

        describe("for the opposite team") {
            it("Should negate one score") {
                // test here
            }
        }
    }
})
```

Tests can be disabled using the `xdescribe` and `xit` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : DescribeSpec({
    describe("this outer block is enabled") {
        xit("this test is disabled") {
            // test here
        }
    }
    xdescribe("this block is disabled") {
        it("disabled by inheritance from the parent") {
            // test here
        }
    }
})
```








## Behavior Spec

Popular with people who like to write tests in the _BDD_ style, `BehaviorSpec` allows you to use `context`, `given`, `when`, `then`.

```kotlin
class MyTests : BehaviorSpec({
    context("a broomstick should be able to be fly and come back on it's own") {
        given("a broomstick") {
            `when`("I sit on it") {
                then("I should be able to fly") {
                    // test code
                }
            }
            `when`("I throw it away") {
                then("it should come back") {
                    // test code
                }
            }
        }
    }
})
```

:::note
Because `when` is a keyword in Kotlin, we must enclose it with backticks. Alternatively, there are title case versions available if you don't like the use of backticks, eg, `Context`, `Given`, `When`, `Then`.
:::



You can also use the `And` keyword in `Given` and `When` to add an extra depth to it:

```kotlin
class MyTests : BehaviorSpec({
    given("a broomstick") {
        and("a witch") {
            `when`("The witch sits on it") {
                and("she laughs hysterically") {
                    then("She should be able to fly") {
                        // test code
                    }
                }
            }
        }
    }
})
```

Note: `Then` scope doesn't have an `and` scope due to a Gradle bug. For more information, see #594



Tests can be disabled using the `xcontext`, `xgiven`, `xwhen`, and `xthen` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : BehaviorSpec({
    xgiven("this is disabled") {
        When("disabled by inheritance from the parent") {
            then("disabled by inheritance from its grandparent") {
                // disabled test
            }
        }
    }
    given("this is active") {
        When("this is active too") {
            xthen("this is disabled") {
               // disabled test
            }
        }
    }
})
```


## Word Spec

`WordSpec` uses the keyword `should` and uses that to nest tests after a context string.

```kotlin
class MyTests : WordSpec({
    "String.length" should {
        "return the length of the string" {
            "sammy".length shouldBe 5
            "".length shouldBe 0
        }
    }
})
```

It also supports the keyword `When` allowing to add another level of nesting. Note, since `when` is a keyword
in Kotlin, we must use backticks or the uppercase variant.

```kotlin
class MyTests : WordSpec({
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






## Free Spec

`FreeSpec` allows you to nest arbitrary levels of depth using the keyword `-` (minus) for outer tests, and just the test name for the final test:

```kotlin
class MyTests : FreeSpec({
    "String.length" - {
        "should return the length of the string" {
            "sammy".length shouldBe 5
            "".length shouldBe 0
        }
    }
    "containers can be nested as deep as you want" - {
        "and so we nest another container" - {
            "yet another container" - {
                "finally a real test" {
                    1 + 1 shouldBe 2
                }
            }
        }
    }
})
```

:::caution
The innermost test must not use the `-` (minus) keyword after the test name.
:::



## Feature Spec

`FeatureSpec` allows you to use `feature` and `scenario`, which will be familiar to those who have used [cucumber](https://cucumber.io/docs/gherkin/reference/).
Although not intended to be exactly the same as cucumber, the keywords mimic the style.

```kotlin
class MyTests : FeatureSpec({
    feature("the can of coke") {
        scenario("should be fizzy when I shake it") {
            // test here
        }
        scenario("and should be tasty") {
            // test here
        }
    }
})
```



Tests can be disabled using the `xfeature` and `xscenario` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : FeatureSpec({
    feature("this outer block is enabled") {
        xscenario("this test is disabled") {
            // test here
        }
    }
    xfeature("this block is disabled") {
        scenario("disabled by inheritance from the parent") {
            // test here
        }
    }
})
```



## Expect Spec

`ExpectSpec` is similar to `FunSpec` and `ShouldSpec` but uses the `expect` keyword.

```kotlin
class MyTests : ExpectSpec({
    expect("my test") {
        // test here
    }
})
```

Tests can be nested in one or more context blocks as well:

```kotlin
class MyTests : ExpectSpec({
    context("a calculator") {
        expect("simple addition") {
            // test here
        }
        expect("integer overflow") {
            // test here
        }
    }
})
```


Tests can be disabled using the `xcontext` and `xexpect` variants (in addition to the [usual ways](conditional_evaluation.md))

```kotlin
class MyTests : ExpectSpec({
    context("this outer block is enabled") {
        xexpect("this test is disabled") {
            // test here
        }
    }
    xcontext("this block is disabled") {
        expect("disabled by inheritance from the parent") {
            // test here
        }
    }
})
```




## Annotation Spec

If you are migrating from JUnit then `AnnotationSpec` is a spec that uses annotations like JUnit 4/5.
Just add the `@Test` annotation to any function defined in the spec class. Unlike other styles, `AnnotationSpec` only works on JVM.

You can also add annotations to execute something before tests/specs and after tests/specs, similarly to JUnit's

```kotlin
@BeforeAll / @BeforeClass
@BeforeEach / @Before
@AfterAll / @AfterClass
@AfterEach / @After
```

If you want to ignore a test, use `@Ignore`.


:::note
Although this spec doesn't offer much advantage over using JUnit, it allows you to migrate existing tests relatively easily, as you typically just need to adjust imports.
:::




```kotlin
class AnnotationSpecExample : AnnotationSpec() {

    @BeforeEach
    fun beforeTest() {
        println("Before each test")
    }

    @Test
    fun test1() {
        1 shouldBe 1
    }

    @Test
    fun test2() {
        3 shouldBe 3
    }
}
```
