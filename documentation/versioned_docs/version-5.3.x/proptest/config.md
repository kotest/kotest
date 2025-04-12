---
id: proptestconfig
title: Configuration
slug: property-test-config.html
---




## Configuration

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

The most common configuration option is specifying the seed for the random instance. This is used when you want to
reliably create the same values each time the test is run. You might want to do this if you find a test failure,
and you want to ensure that that particular set of values continues to be executed in the future as a kind of regression
test.


:::tip
Whenever a property test fails, Kotest will output the seed that was used. You can duplicate the test, setting it to use
this seed so you have permanent regression test for those values.
:::

For example:

```kotlin
class PropertyExample: StringSpec({
   "String size" {
      forAll<String, String>(PropTestConfig(seed = 127305235)) { a,b ->
         (a + b).length == a.length + b.length
      }
   }
})
```

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
