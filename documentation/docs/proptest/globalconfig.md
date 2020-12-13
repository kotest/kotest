---
id: globalconfig
title: Global Configuration
slug: property-test-global-config.html
---

Some property test settings can be set globally for all property tests.

### Default Iterations

The standard default iteration count is 1000. This means when you don't specify the iteration count in a property test,
the default will be 1000.

We can override this default either by assigning a value to `PropertyTesting.defaultIterationCount`, or by using the system property `kotest.proptest.default.iteration.count`.

Any test which directly sets the iteration count will of course use that value.

For example:

```kotlin
PropertyTesting.defaultIterationCount = 123

// will use 555 iterations specified in the test
forAll<String, String>(555) { a,b -> a + b == "$a$b" }

// will use 123 iterations from the global default
forAll<String, String> { a,b -> a + b == "$a$b" }
```

### Printing Shrink Steps

By default, when using shrinking, each shrinking step will not be logged, but only the final shrunk value.

To enable logging of each intermediate value, assign true to `PropertyTesting.shouldPrintShrinkSteps`
or use the system property `kotest.proptest.output.shrink-steps=true`.

