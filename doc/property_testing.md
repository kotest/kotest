Property-based Testing <a name="property-based"></a>
----------------------

### Property Testing

To automatically test your code with many combinations of values, you can allow Kotest to do the boilerplate
by using property testing with `generators`. You invoke `assertAll` or `assertNone` and pass in a lambda, where the lambda
parameters are populated automatically with many different values. The lambda must specify explicitly the parameter
types as Kotest will use those to determine what types of values to pass in.

For example, here is a property test that checks that for any two Strings, the length of `a + b`
is the same as the length of `a` plus the length of `b`. In this example Kotest would
execute the test 1000 times for random String combinations.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      assertAll({ a: String, b: String ->
        (a + b).length shouldBe a.length + b.length
      })
    }

  }
}
```

You can also specify the number of times a test is going to be run. Here is the same test but this time it will run 2300 times.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      forAll(2300) { a: String, b: String ->
        (a + b).length == a.length + b.length
      }
    }

  }
}
```

There are generators defined for all the common base types - String, Ints, UUIDs, etc. If you need to generate custom types
then you can simply specify the generator manually (or write your own). For example here is the same test again but
with the generators explicitly specified.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      forAll(Gen.string(), Gen.string(), { a: String, b: String ->
        (a + b).length == a.length + b.length
      })
    }

  }
}
```






### Custom Generators

To write your own generator for a type T, you just implement the interface `Gen<T>`.

```kotlin
interface Gen<T> {
  fun constants(): Iterable<T>
  fun random(): Sequence<T>
}
```

The first function, `constants` returns values that should _always_ be included
 in the test inputs. This is typically used for common edge case values. For example, the `Int` generator implements
 `constants` to return 0, Int.MIN_VALUE and Int.MAX_VALUE as these are values that are often overlooked.

The second function is `random` which returns a lazy list of random values, which is the bread and butter of a generator.

For example you could write a `Gen` that supports a custom class called `Person`.
 In this case there are no real edge case values for a `Person` instance so we can leave `constants` as an empty list.

```kotlin
data class Person(val name: String, val age: Int)
class PersonGenerator : Gen<Person> {
    override fun constants() = emptyList<Person>()
    override fun random() = generateSequence {
        Person(Gen.string().random().first(), Gen.int().random().first())
    }
}
```



