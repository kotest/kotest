---
id: kotlinx_datetime
title: Kotlinx Datetime Matchers
slug: kotlinx-datetime-matchers.html
sidebar_label: Kotlinx Datetime
---




Matchers for the [Kotlinx Datetime](https://github.com/Kotlin/kotlinx-datetime) library are provided by the `kotest-assertions-kotlinx-time` module.


| LocalDate ||
| -------- | ---- |
| `date.shouldHaveSameYearAs(otherDate)` | Asserts that the date has the same year as the given date. |
| `date.shouldHaveSameMonthAs(otherDate)` | Asserts that the date has the same month as the given date. |
| `date.shouldHaveSameDayAs(otherDate)` | Asserts that the date has the same day of the month as the given date. |
| `date.shouldBeBefore(otherDate)` | Asserts that the date is before the given date. |
| `date.shouldBeAfter(otherDate)` | Asserts that the date is after the given date. |
| `date.shouldBeWithin(period, otherDate)` | Asserts that the date is within the period of the given date. |
| `date.shouldBeWithin(duration, otherDate)` | Asserts that the date is within the duration of the given date. |
| `date.shouldBeBetween(firstDate, secondDate)` | Asserts that the date is between firstdate and seconddate. |
| `date.shouldHaveYear(year)` | Asserts that the date have correct year. |
| `date.shouldHaveMonth(month)` | Asserts that the date have correct month. |
| `date.shouldHaveDayOfYear(day)` | Asserts that the date have correct day of year. |
| `date.shouldHaveDayOfMonth(day)` | Asserts that the date have correct day of month. |
| `date.shouldHaveDayOfWeek(day)` | Asserts that the date have correct day of week. |
| `date.shouldHaveHour(hour)` | Asserts that the date have correct hour. |
| `date.shouldHaveMinute(Minute)` | Asserts that the date have correct minute. |
| `date.shouldHaveSecond(second)` | Asserts that the date have correct second. |
| `date.shouldHaveNano(nano)` | Asserts that the date have correct nano second. |


| LocalDateTime ||
| -------- | ---- |
| `time.shouldHaveSameHoursAs(otherTime)` | Asserts that the time has the same hours as the given time. |
| `time.shouldHaveSameMinutesAs(otherTime)` | Asserts that the time has the same minutes as the given time. |
| `time.shouldHaveSameSecondsAs(otherTime)` | Asserts that the time has the same seconds as the given time. |
| `time.shouldHaveSameNanosAs(otherTime)` | Asserts that the time has the same nanos as the given time. |
| `time.shouldBeBefore(otherTime)` | Asserts that the time is before the given time. |
| `time.shouldBeAfter(otherTime)` | Asserts that the time is after the given time. |
| `time.shouldBeBetween(firstTime, secondTime)` | Asserts that the time is between firstTime and secondTime. |



| Instant ||
| -------- | ---- |
| `instant.shouldBeAfter(anotherInstant)` | Asserts that the instant is after anotherInstant |
| `instant.shouldBeBefore(anotherInstant)` | Asserts that the instant is before anotherInstant |
| `instant.shouldBeBetween(fromInstant, toInstant)` | Asserts that the instant is between fromInstant and toInstant |
