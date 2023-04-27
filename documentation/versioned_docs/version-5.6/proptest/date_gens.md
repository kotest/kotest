---
id: date_gens
title: Kotlinx DateTime Gens
slug: kotlinx-datetime-gens.html
sidebar_label: Kotlinx DateTime
---

Kotest provides an optional module that provides generators for [KotlinX DateTime](https://github.com/Kotlin/kotlinx-datetime).

:::note
To use, add `io.kotest.extensions:kotest-property-datetime:version` to your build.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-property-datetime?label=latest%20release"/>](https://search.maven.org/search?q=kotest-property-datetime)


| Generator                                                     | Description                                                                             | JVM | JS  | Native |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------------|-----|-----|--------|
| `Arb.date(yearRange)`                                         | Generates `LocalDate`s with the year between the given range and other fields randomly. | ✓   | ✓   | ✓      |
| `Arb.datetime(yearRange, hourRange, minuteRange, secondRage)` | Generates `LocalDateTime`s with all fields in the given ranges                          | ✓   | ✓   | ✓      |
| `Arb.instant(range)`                                          | Generates `Instant`s with the epoch randomly generated in the given range               | ✓   | ✓   | ✓      |
