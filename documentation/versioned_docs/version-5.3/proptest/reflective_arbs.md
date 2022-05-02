---
id: reflective_arbs
title: Reflective Arbs
slug: reflective-arbs.html
---

When running tests on **JVM**, Kotest supports generating more complex `Arb`s automatically. This can be useful when you
have a `data class` which carries a simple combination of data which can already be automatically derived.

Example:

```kotlin
enum class Currency {
  USD, GBP, EUR
}

data class CurrencyAmount(
  val amount: Long,
  val currency: Currency
)

context("All currencies converts to EUR") { // In some spec
  checkAll(Arb.bind<CurrencyAmount>()) { currencyAmount ->
    val converted = currencyAmount.convertTo(EUR)
    converted.currency shouldBe EUR
  }
}
```

Reflective binding is supported for:

* Data classes, where all properties also fall into this category
* `Pair`, where 1st and 2nd fall into this category
* Primitives
* Enums
* `LocalDate`, `LocalDateTime`, `LocalTime`, `Period`, `Instant` from `java.time`
* `BigDecimal`, `BigInteger`
* Collections (`Set`, `List`, `Map`)
* Other types for which you have provided an Arb explicitly using the `providedArbs` parameter

