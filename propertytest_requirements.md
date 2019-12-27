### Property Test Requirements

#### Deterministic Re-runs

If a test failed it is useeful to be able to re-run the tests with the same values. Especially in cases where shrinking is not available.
Therefore, the test functions accept a seed value which is used to create the Random instance used by the tests. This seed can then be programatically
set to re-run the tests with the same random instance.

By default the seed is null, which means the seed changes each time the test is run.

#### Exhaustivity

The generators are passed an Exhaustivity enum value which determines the behavior of generated values.
* Random - all values should be randomly generated
* Exhaustive - every value should be generated at least once. If, for the given iteration count, all values cannot be generated, then the test should fail.
* Auto - Exhaustive if possible and supported, otherwise random.

By default Auto mode is used.

_Question - do we want to be able to specify exhaustivity per parameter?_

#### Min and Max Successes

These values determine bounds on how many tests should pass. Typically min and max success would be equal to the iteration count, which gives the `forAll` behavior.
For `forNone` behavior, min and max would both be zero. Other values can be used to mimic behavior like `forSome`, `forExactly(n)` and so on.

By default, min and max success are set to the iteration count.

#### Distribution

It is quite common to want to generate values across a large number space, but have a bias towards certain values. For example, when writing a function
to test emails, it might be more useful to generate more strings with a smaller number of characters than larger amounts. Most emails are probably < 50 characters for example.

The distribution mode can be used to bias values by setting the bound from which each test value is generated.

* Uniform - values are distributed evenly across the space. For an integer generator of values from 1 to 1000 with 10 runs, a random value would be generated from 0.100, another from 101..200 and so on.
* Pareto - values are biased towards the lower end on a rougly 80/20 rule.

By default the uniform distribution is used.

The distribution mode may be ignored by a generator if it has no meaning for the types produced by that generator.

The distribution mode has no effect if the generator is acting in exhaustive mode.

_Question - it would be nice to be able to specify specific "biases" when using specific generators. For example, a generator of A-Z chars may choose to bias towards vowels. How to specify this when distribution is a sealed type?_

#### Shrinking Mode

The _ShrinkingMode_ determines how failing values are shrunk.
* Off - Shrinking is disabled for this generator
* Unbounded - shrinking will continue until the _minimum_ case is reached as determined by the generator
* Bounded(n) - the number of shrink steps is capped at n. After this, the shrinking process will halt, even if the minimum case has not been reached. This mode is useful to avoid long running tests.

By default shrinking is set to Bounded(1000).

_Question1 - do we want to be able to control shrinking per parameter? Turn it off for some parameters, and not others?_

When mapping on a generator, shrinking becomes tricky.
If you have a mapper from GenT to GenU and a value u fails, you need to turn that u back into a t, so you can feed that t into the original shrinker. So you can either keep the associate between original and mapped, or return the shinks along with the value.

_Question2 - which is the best approach?_
