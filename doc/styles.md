Testing Styles
==============

There is no functional difference between these styles. All allow the same types of configuration - threads, tags, etc -
 it is simply a matter of preference how you structure your tests. It is common to see several styles in one project.

### String Spec

`StringSpec` reduces the syntax to the absolute minimum.
 Just write a string followed by a lambda expression with your test code. If in doubt, this is the style to use.

```kotlin
class MyTests : StringSpec({
    "strings.length should return size of string" {
      "hello".length shouldBe 5
    }
})
```

### Fun Spec

`FunSpec` allows you to create tests by invoking a function called `test` with a string parameter to describe the test,
and then the test itself as a closure.

```kotlin
class MyTests : FunSpec({
    test("String length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})
```

### Should Spec

`ShouldSpec` is similar to fun spec, but uses the keyword `should` instead of `test`.

```kotlin
class MyTests : ShouldSpec({
    should("return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})
```

This can be nested in context strings too, eg

```kotlin
class MyTests : ShouldSpec({
    "String.length" {
        should("return the length of the string") {
            "sammy".length shouldBe 5
            "".length shouldBe 0
      }
    }
})
```

### Word Spec

`WordSpec` uses the keyword `should` and uses that to nest test blocks after a context string.

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

### Feature Spec

`FeatureSpec` allows you to use `feature` and `scenario`, which will be familar to those who have used [cucumber](http://docs.cucumber.io/gherkin/reference/)
Although not intended to be exactly the same as cucumber, the keywords mimic the style.

```kotlin
class MyTests : FeatureSpec({
    feature("the can of coke") {
        scenario("should be fizzy when I shake it") {
          // test here
        }
        scenario("and should be tasty") {
          // test heree
        }
    }
})
```
### Behavior Spec

Popular with people who like to write tests in the _BDD_ style, `BehaviorSpec` allows you to use `given`, `when`, `then`.

```kotlin
class MyTests : BehaviorSpec({
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

Because `when` is a keyword in Kotlin, we must enclose with backticks. Alternatively, there are title case versions
available if you don't like the use of backticks, eg, `Given`, `When`, `Then`.

### Free Spec

`FreeSpec` allows you to nest arbitary levels of depth using the keyword `-` (minus), as such:

```kotlin
class MyTests : FreeSpec({
    "String.length" - {
        "should return the length of the string" {
            "sammy".length shouldBe 5
            "".length shouldBe 0
        }
    }
})
```

### Describe Spec

`DescribeSpec` offers functionality familar to those who are coming from a Ruby background, as this testing style
 mimics the popular ruby test framework [rspec](http://rspec.info/). The scopes available are `describe`, `context`, and `it`.

```kotlin
class MyTests : DescribeSpec({
    describe("score") {
        it("start as zero") {
            // test here
        }
        context("with a strike") {
            it("adds ten") {
                // test here
            }
            it("carries strike to the next frame") {
                // test here
            }
       }
   }
}
```
### Expect Spec

`ExpectSpec` allows you to use `context` and `expect`.

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

### Annotation Spec

If you are hankering for the halycon days of JUnit then you can use a spec that uses annotations like JUnit 4.
Just add the `@Test` annotation to any function defined in the spec class.

```kotlin
class AnnotationSpecExample : AnnotationSpec() {

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
