---
id: proptestconfig
title: Configuration
slug: property-test-config.html
---



Kotest provides for the  ability to specify some configuration options when running a property test. We do this by passing
in an instance of `PropTestConfig` to the test methods.

For example:

```kotlin
class PropertyExample: StringSpec({
   "String size" {
      forAll<String, String>(PropTestConfig(options here...)) { a,b ->
         (a + b).length == a.length + b.length
      }
   }
})
```

### Seed

A commonly used configuration option is specifying the seed used by the random source. This is used when you want to
repeat the same values each time the test is run. You might want to do this if you find a test failure,
and you want to ensure that that particular set of values continues to be executed in the future as a regression
test.

For full details on how the seed is used [click here](seed.md).

### Min Failure

By default, Kotest tolerates no failure. Perhaps you want to run some non-deterministic test a bunch of times, and you're happy
to accept some small number of failures. You can specify that in config.

```kotlin
class PropertyExample: StringSpec({
   "some flakey test" {
      forAll<String, String>(PropTestConfig(maxFailure = 3)) { a,b ->
         // max of 3 inputs can fail
      }
   }
})
```

### PropTestListener

Sometimes in property test it is required to perform some setup and tear down in each iteration of test.
For this purpose you can register a ```PropTestListener``` with ```PropTestConfig```.
```kotlin
class PropertyExample: StringSpec({
   "some property test which require setup and tear down in each iteration" {
      forAll<String, String>(PropTestConfig(listeners = listOf(MyPropTestListener))) { a,b ->
         // some assertion
      }
   }
})
```

### Handling Unprintable Characters in Failure Messages

When property tests involve strings with unprintable characters, failure messages can be hard to read and debug.
By setting `outputHexForUnprintableChars` to true in `PropTestConfig`,
unprintable characters in failure messages are displayed as their Unicode code points in the format `U+XXXX`.

```kotlin
class PropertyExample : StringSpec({
    "handle unprintable characters in failure messages" {
        forAll<String>(
            PropTestConfig(outputHexForUnprintableChars = true)
        ) { str ->
            // some assertion
        }
    }
})
```

**Default Value**: `outputHexForUnprintableChars` is `false` by default.

Alternatively, you can set this option as a common project-wide setting
in a `kotest.properties` file located in your classpath:
```properties
kotest.proptest.arb.string.output-hex-for-unprintable-chars=true
```
