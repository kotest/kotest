---
id: permutations
title: Permutations
slug: property-test-permutations.html
sidebar_label: Permutations
---

The permutations DSL is a newer property-testing API introduced in Kotest 6.0. Rather than passing generators as
positional parameters to `forAll` or `checkAll`, generators are declared inline as named properties using a `gen { ... }`
delegate, and the test body is declared in a `check { ... }` block. This produces a more readable test as the inputs
have meaningful names and the configuration is expressed declaratively at the call site.

A simple permutation test that asserts addition is commutative:

```kotlin
permutations {

   val a by gen { Arb.int() }
   val b by gen { Arb.int() }

   iterations = 1000

   check {
      (a + b) shouldBe (b + a)
   }
}
```

The permutations DSL is currently marked `@ExperimentalKotest` and the API may change before it stabilises.

## Configuration

Every option supported by the DSL is a `var` on `PermutationConfiguration` and may be set inside the `permutations { }`
block. The most common options are:

| Option                           | Default                                  | Description                                                                                |
|----------------------------------|------------------------------------------|--------------------------------------------------------------------------------------------|
| `iterations`                     | 1000                                     | Number of permutations to execute when no other constraint is set.                          |
| `duration`                       | null                                     | If set, iterations run until this duration elapses (overrides `iterations`).                |
| `constraints`                    | null                                     | Custom `Constraints` strategy (overrides both `iterations` and `duration`).                 |
| `minSuccess`                     | `Int.MAX_VALUE`                          | The minimum number of successful permutations required; otherwise the test fails.           |
| `maxFailures`                    | 0                                        | The number of failing permutations tolerated before the run aborts.                         |
| `maxDiscardPercentage`           | 20                                       | The maximum percentage of permutations that may be discarded by `assume`.                   |
| `seed`                           | null                                     | If set, generators use this seed instead of a random one.                                   |
| `failOnSeed`                     | false                                    | If true, fails the test when `seed` has been explicitly set.                                |
| `writeFailedSeed`                | true                                     | If true, the seed used by a failing test is written to disk so it can be replayed.          |
| `shouldPrintConfig`              | false                                    | Prints a summary of the active configuration before the run.                                |
| `shouldPrintGeneratedValues`     | false                                    | Prints the value of each generator on every iteration.                                      |
| `shouldPrintShrinkSteps`         | true                                     | Prints each step taken while shrinking a counterexample.                                    |
| `statisticsReportMode`           | `StatisticsReportMode.ON`                | When to print classification statistics: `ON`, `SUCCESS`, `FAILED`, or `OFF`.               |
| `edgecasesGenerationProbability` | 0.02                                     | The probability that a generator emits an edge case rather than a random sample.            |

For example, to run 250 iterations using a fixed seed and print the config:

```kotlin
permutations {
   iterations = 250
   seed = 4242L
   shouldPrintConfig = true

   val n by gen { Arb.int(0..100) }

   check {
      (n * n) shouldBeGreaterThanOrEqual 0
   }
}
```

## Shared configuration

When several tests should share the same defaults, build a `PermutationConfiguration` once with `permconfig` and pass it
to `permutations(default = ...)`. Any options set inside the `permutations` block override those of the shared default.

```kotlin
val defaults = permconfig {
   iterations = 500
   maxDiscardPercentage = 10
   shouldPrintConfig = true
}

class CommutativityTest : FunSpec({

   test("addition is commutative") {
      permutations(defaults) {
         val a by gen { Arb.int() }
         val b by gen { Arb.int() }
         check { (a + b) shouldBe (b + a) }
      }
   }

   test("multiplication is commutative") {
      permutations(defaults) {
         val a by gen { Arb.int() }
         val b by gen { Arb.int() }
         // override just the iteration count for this test
         iterations = 200
         check { (a * b) shouldBe (b * a) }
      }
   }
})
```

## Assumptions

`assume` is used inside `check { }` to discard a permutation whose generated values are not interesting. A discarded
permutation does not count as a success or a failure - it simply does not contribute to the run. There are two forms:

```kotlin
permutations {
   val a by gen { Arb.int(0..10) }
   val b by gen { Arb.int(0..10) }

   check {
      // boolean form: skip when the predicate is false
      assume(a != b)

      // function form: skip if the block throws an AssertionError
      assume { a shouldNotBe b }

      a.compareTo(b) shouldNotBe 0
   }
}
```

If too many permutations are discarded (more than `maxDiscardPercentage`), the run aborts with an error. This protects
against accidentally writing an assumption that filters out almost every generated value.

## Statistics

Inside `check { }`, calls to `classify` track how often a permutation matched a given classification. Classifications
can be grouped under a label so that multiple, independent dimensions can be tracked side by side. Without a label, the
default label `statistics` is used.

```kotlin
permutations {
   val n by gen { Arb.int() }

   check {
      classify(n % 2 == 0, "even", "odd")                  // default label
      classify("sign", n >= 0, "non-negative", "negative") // custom label
   }
}
```

When statistics are enabled, the counts and percentages for each label are printed at the end of the run:

```
Statistics: [addition is commutative] (1000 iterations) [sign]

positive                                                     503 (50%)
negative                                                     497 (50%)

Statistics: [addition is commutative] (1000 iterations) [parity]

even                                                         512 (51%)
odd                                                          488 (49%)
```

Set `statisticsReportMode` to `OFF` to suppress this output, or to `SUCCESS` / `FAILED` to print it only when the run
passes or only when it fails.

### Coverage assertions

A `coverage { }` block lets you assert that classifications appeared at least a certain number of times, or at least
a certain percentage of the time, across the run. A failing coverage check fails the test even if every assertion
inside `check` passed.

```kotlin
permutations {
   iterations = 1000

   val n by gen { Arb.int() }

   check {
      classify("parity", n % 2 == 0, "even", "odd")
      classify("sign", n >= 0, "non-negative", "negative")
   }

   coverage {
      // at least 400 of the iterations must be classified as 'even' under the parity label
      count("parity", "even", 400)

      // at least 40% of iterations must be classified as 'non-negative' under the sign label
      percentage("sign", "non-negative", 40.0)
   }
}
```

The two-argument forms (`count(value, n)` / `percentage(value, p)`) apply to the default label, matching the
two-argument form of `classify`.

## Seeds

By default each run uses a fresh random seed. The active seed is part of the run's identity - the same seed produces
the same sequence of generated values. The DSL provides several knobs around seeds:

### Manually setting the seed

Set `seed` to reproduce a specific run, for example after a failing test reports the seed it used:

```kotlin
permutations {
   seed = 1900646515L

   val a by gen { Arb.int(0..100) }
   check { a shouldBeLessThan 8 }
}
```

### Persisted failing seeds

When a permutation test fails, the seed used by that run is written to disk under the project's seed directory. The
next time the same test runs and finds no explicit `seed`, it will read this persisted seed and replay the failing
inputs. This makes flaky property-test failures easier to investigate. To opt out, set `writeFailedSeed = false`.

### Failing if a seed is hardcoded

`seed` is convenient for debugging, but a hardcoded seed defeats the purpose of property testing in CI. Set
`failOnSeed = true` (typically through global defaults) to fail any permutation test that still has an explicit
`seed` set, helping catch debugging seeds that were forgotten.

```kotlin
permutations {
   failOnSeed = true
   seed = 1234L // this will now fail the test
   check { /* ... */ }
}
```
