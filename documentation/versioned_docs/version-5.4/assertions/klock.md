---
title: Klock Matchers
slug: klock-matchers.html
sidebar_label: Klock
---



Matchers for the [Klock](https://github.com/korlibs/klock) library, provided by the `kotest-assertions-klock` module.

| Matcher | Description    |
| ---------- | --- |
| `date.shouldHaveSameYear(otherDate)` | Asserts that the date has the same year as the given date. |
| `date.shouldHaveSameMonth(otherDate)` | Asserts that the date has the same month as the given date. |
| `date.shouldHaveSameDay(otherDate)` | Asserts that the date has the same day of the month as the given date. |
| `date.shouldBeBefore(otherDate)` | Asserts that the date is before the given date. |
| `date.shouldBeAfter(otherDate)` | Asserts that the date is after the given date. |
| `date.shouldBeBetween(firstDate, secondDate)` | Asserts that the date is between firstdate and seconddate. |
| `date.shouldHaveYear(year)` | Asserts that the date have correct year. |
| `date.shouldHaveMonth(month)` | Asserts that the date have correct month. |
| `date.shouldHaveDay(day)` | Asserts that the date have correct day of year. |
| `date.shouldHaveHour(hour)` | Asserts that the date have correct hour. |
| `date.shouldHaveMinute(Minute)` | Asserts that the date have correct minute. |
| `date.shouldHaveSecond(second)` | Asserts that the date have correct second. |
| `time.shouldHaveSameHoursAs(time)` | Asserts that the time has the same hours as the given time. |
| `time.shouldHaveHours(hours)` | Asserts that the time has the given hours. |
| `time.shouldHaveSameMinutesAs(time)` | Asserts that the time has the same minutes as the given time. |
| `time.shouldHaveMinutes(minutes)` | Asserts that the time has the given minutes. |
| `time.shouldHaveSameSeconds(time)` | Asserts that the time has the same seconds as the given time. |
| `time.shouldHaveSeconds(seconds)` | Asserts that the time has the given seconds. |
| `time.shouldHaveSameMillisecondsAs(time)` | Asserts that the time has the same milliseconds as the given time. |
| `time.shouldHaveMilliseconds(millis)` | Asserts that the time has the given millis. |
| `time.shouldBeBefore(time)` | Asserts that the time is before the given time. |
| `time.shouldBeAfter(time)` | Asserts that the time is after the given time. |
| `time.shouldBeBetween(time, time)` | Asserts that the time is between the two given times. |
