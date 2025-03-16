---
id: seeds
title: Property Test Seeds
slug: property-test-seeds.html
sidebar_label: Seeds
---


When a property test is executed, the values are generated using a random source that is created from a seed value. By
default this seed value is itself randomly chosen (using the default `kotlin.random.Random` instance). However, there
are times when this value needs to be fixed or repeated.

You can change the default used by all tests, unless overriden through the options listed below, by changing the
configuration value `PropertyTesting.defaultSeed`.

### Manually specifying the seed

To manually set the seed, pass an instance of `PropTestConfig` to your prop test methods. You might want to do this if
you find a test failure, and you want to ensure that those values continue to be executed in the future as a regression
test.

For example:

```kotlin
class PropertyExample : StringSpec({
  "String size" {
    forAll<String, String>(PropTestConfig(seed = 127305235)) { a, b ->
      (a + b).length == a.length + b.length
    }
  }
})
```

:::tip
Whenever a property test fails, Kotest will output the seed that was used. You can duplicate the test, setting it to use
this seed so you have permanent regression test for those values.
:::

### Rerunning failed seeds

By default, when a property test fails, the seed used by that test is written to a file
in `~/.kotest/seeds/<spec>/<testname>`. Whenever a property test runs, this seed is detected if the file exists, and
used in place of a random seed. Next time the test is successful, the seed file will be removed.

:::note
A manually specified seed always takes precedence over a failed seed.
:::

:::tip
This feature can be disabled by setting `PropertyTesting.writeFailedSeed = false`
:::

### Failing when seeds set

Some users prefer to avoid manually specifying seeds. They want to use them locally only, when developing, but to avoid
checking them in. If this is your style, then set `PropertyTesting.failOnSeed = false` or the env
var `kotest.proptest.seed.fail-if-set` to `false` on your server.

Then if a seed is detected, the test suite will fail.

