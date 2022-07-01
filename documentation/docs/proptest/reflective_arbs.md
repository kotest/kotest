---
id: reflective_arbs
title: Reflective Arbs
slug: reflective-arbs.html
---

When running tests on **JVM**, Kotest supports generating more complex `Arb`s automatically.
The generated `Arb` relies on build-in default and further reflective `Arb`s to populate the class parameters.
If you just need to create and instance and don't need filtering you can use the class type in the `checkAll`/`forAll` calls directly.
When you want to obtain the `Arb` to manipulate it further or filter invalid values, you can use `Arb.bind` with the type argument to obtain the `Arb`.


Example:

```kotlin
enum class Currency {
  USD, GBP, EUR
}

class CurrencyAmount(
  val amount: Long,
  val currency: Currency
)

context("All currencies converts to EUR") { // In some spec
  checkAll(Arb.bind<CurrencyAmount>()) { currencyAmount ->
    val converted = currencyAmount.convertTo(EUR)
    converted.currency shouldBe EUR
  }
}

context("Converting to a currency and back yields the same amount") { // In some spec
  checkAll<CurrencyAmount, Currency>() { currencyAmount, currency ->
    val converted = currencyAmount.convertTo(currency).convertTo(currencyAmount.currency)
    converted.currency shouldBe currencyAmount
  }
}
```

Reflective binding is supported for:

* Classes that are not private and which primary constructor is not private, if the parameters are also supported by reflective binding
* `Pair`, where 1st and 2nd fall into this category
* Primitives
* Enums
* Sealed classes, subtypes must not be private and their primary constructor must not be private
* `LocalDate`, `LocalDateTime`, `LocalTime`, `Period`, `Instant` from `java.time`
* `BigDecimal`, `BigInteger`
* Collections (`Set`, `List`, `Map`)
* Classes for which an Arb has been provided through `providedArbs`

