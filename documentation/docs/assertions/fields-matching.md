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
