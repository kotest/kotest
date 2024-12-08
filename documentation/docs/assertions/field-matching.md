---
id: field-matching
title: Matching By Field
slug: field-matching.html
---

Whenever we want to match only some of the fields, excluding some other fields from comparison, we should use `shouldBeEqualUsingFields`:

```kotlin
   val expected = Thing(name = "apple", createdAt = Instant.now())
   val actual = Thing(name = "apple", createdAt = Instant.now().plusMillis(42L))
   expected shouldBeEqualUsingFields {
      excludedProperties = setOf(Thing::createdAt)
      actual
   }
```

Likewise, we can explicitly say which fields to match on, and all other fields will be excluded:

```kotlin
   val expected = Thing(name = "apple", createdAt = Instant.now())
   val actual = Thing(name = "apple", createdAt = Instant.now().plusMillis(42L))
   expected shouldBeEqualUsingFields {
      includedProperties = setOf(Thing::name)
      actual
   }
```

For nested classes, comparison goes recursively, as follows:

```kotlin
         val doctor1 = Doctor("billy", 23, emptyList())
         val doctor2 = Doctor("barry", 23, emptyList())

         val city = City("test1", Hospital("test-hospital1", doctor1))
         val city2 = City("test2", Hospital("test-hospital2", doctor2))

         shouldThrowAny {
            city.shouldBeEqualUsingFields {
               city2
            }
         }.message shouldContain """Using fields:
 - mainHospital.mainDoctor.age
 - mainHospital.mainDoctor.name
 - mainHospital.name
 - name

Fields that differ:
 - mainHospital.mainDoctor.name  =>  expected:<"barry"> but was:<"billy">
 - mainHospital.name  =>  expected:<"test-hospital2"> but was:<"test-hospital1">
 - name  =>  expected:<"test2"> but was:<"test1">"""
```

But we can explicitly stop recursive comparison. In the following example, we are comparing instances of `Doctor` class as a whole, not comparing their individual fields. So the difference in `mainHospital.mainDoctor` is detected, as opposed to detected differences in `mainHospital.mainDoctor.name` in the previous example:

```kotlin
         val doctor1 = Doctor("billy", 22, emptyList())
         val doctor2 = Doctor("billy", 22, emptyList())

         val city = City("test", Hospital("test-hospital", doctor1))
         val city2 = City("test", Hospital("test-hospital", doctor2))

         shouldFail {
            city.shouldBeEqualUsingFields {
               useDefaultShouldBeForFields = listOf(Doctor::class)
               city2
            }
         }.message shouldContain """Using fields:
 - mainHospital.mainDoctor
 - mainHospital.name
 - name

Fields that differ:
 - mainHospital.mainDoctor  =>

```

Also we can provide custom matchers for fields. In the following example we are matching `SimpleDataClass::name` as case-insensitive strings:

```kotlin
     val expected = SimpleDataClass("apple", 1.0, LocalDateTime.now())
     val actual = expected.copy(name = "Apple")
     shouldThrow<AssertionError> {
        actual shouldBeEqualUsingFields expected
     }.message.shouldContainInOrder(
        "Fields that differ:",
        """- name  =>  expected:<"apple"> but was:<"Apple">""",
     )
     actual shouldBeEqualUsingFields {
        overrideMatchers = mapOf(
           SimpleDataClass::name to matchStringsIgnoringCase
        )
        expected
     }
```

Kotest provides the following override matchers:

### matchBigDecimalsIgnoringScale

```kotlin
 val expected = WithManyFields(
      BigDecimal.ONE,
      LocalDateTime.now(),
      ZonedDateTime.now(),
      OffsetDateTime.now(),
      Instant.now()
   )
 val actual = expected.copy(bigDecimal = BigDecimal("1.000"))

 actual shouldBeEqualUsingFields {
    overrideMatchers = mapOf(
       WithManyFields::bigDecimal to matchBigDecimalsIgnoringScale()
    )
    expected
 }
```

### matchDoublesWithTolerance

```kotlin
      val expected = SimpleDataClass("apple", 1.0, LocalDateTime.now())
      val actual = expected.copy(weight = 1.001)

      actual shouldBeEqualUsingFields {
         overrideMatchers = mapOf(
            SimpleDataClass::weight to matchDoublesWithTolerance(0.01)
         )
         expected
      }
```

### matchInstantsWithTolerance

```kotlin
val expected = WithManyFields(
      BigDecimal.ONE,
      LocalDateTime.now(),
      ZonedDateTime.now(),
      OffsetDateTime.now(),
      Instant.now()
   )
val actual = expected.copy(instant = expected.instant.plusSeconds(1))

actual shouldBeEqualUsingFields {
  overrideMatchers = mapOf(
     WithManyFields::instant to matchInstantsWithTolerance(2.seconds)
  )
  expected
}
```

### matchListsIgnoringOrder

```kotlin
     val expected = DataClassWithList("name", listOf(1, 2, 3))
     val actual = expected.copy(elements = listOf(3, 2, 1))
     actual shouldBeEqualUsingFields {
        overrideMatchers = mapOf(
           DataClassWithList::elements to matchListsIgnoringOrder<Int>()
        )
        expected
     }
```

### matchLocalDateTimesWithTolerance

```kotlin
val expected = WithManyFields(
      BigDecimal.ONE,
      LocalDateTime.now(),
      ZonedDateTime.now(),
      OffsetDateTime.now(),
      Instant.now()
   )
val actual = expected.copy(localDateTime = expected.localDateTime.plusSeconds(1))

actual shouldBeEqualUsingFields {
  overrideMatchers = mapOf(
     WithManyFields::localDateTime to matchLocalDateTimesWithTolerance(2.seconds)
  )
  expected
}
```

### matchOffsetDateTimesWithTolerance

```kotlin
val expected = WithManyFields(
      BigDecimal.ONE,
      LocalDateTime.now(),
      ZonedDateTime.now(),
      OffsetDateTime.now(),
      Instant.now()
   )
val actual = expected.copy(offsetDateTime = expected.offsetDateTime.plusSeconds(1))

actual shouldBeEqualUsingFields {
  overrideMatchers = mapOf(
     WithManyFields::offsetDateTime to matchOffsetDateTimesWithTolerance(2.seconds)
  )
  expected
}
```


### matchStringsIgnoringCase

```kotlin
   val expected = SimpleDataClass("apple", 1.0, LocalDateTime.now())
   val actual = expected.copy(name = "Apple")

   actual shouldBeEqualUsingFields {
      overrideMatchers = mapOf(
         SimpleDataClass::name to matchStringsIgnoringCase
      )
      expected
   }
```

### matchZonedDateTimesWithTolerance

```kotlin
val expected = WithManyFields(
      BigDecimal.ONE,
      LocalDateTime.now(),
      ZonedDateTime.now(),
      OffsetDateTime.now(),
      Instant.now()
   )
val actual = expected.copy(zonedDateTime = expected.zonedDateTime.plusSeconds(1))

actual shouldBeEqualUsingFields {
  overrideMatchers = mapOf(
     WithManyFields::zonedDateTime to matchOffsetDateTimesWithTolerance(2.seconds)
  )
  expected
}
```

## Building Your Own Override Matcher

Implement `Assertable` interface:

```kotlin
fun interface Assertable {
   fun assert(expected: Any?, actual: Any?): CustomComparisonResult
}

sealed interface CustomComparisonResult {
   val comparable: Boolean
   data object NotComparable: CustomComparisonResult {
      override val comparable = false
   }
   data object Equal: CustomComparisonResult {
      override val comparable = true
   }
   data class Different(val assertionError: AssertionError): CustomComparisonResult {
      override val comparable = true
   }
}
```

For instance, here is the implementation of `matchListsIgnoringOrder`:

```kotlin
fun<T> matchListsIgnoringOrder() = Assertable { expected: Any?, actual: Any? ->
   customComparison<List<T>>(expected, actual) { expected: List<T>, actual: List<T> ->
      actual shouldContainExactlyInAnyOrder expected
   }
}
```

We can use any of Kotest's `should***` assertions.
