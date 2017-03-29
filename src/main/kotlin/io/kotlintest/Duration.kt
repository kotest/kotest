package io.kotlintest

import java.util.concurrent.TimeUnit

data class Duration(val amount: Long, val timeUnit: TimeUnit) {
  val nanoseconds: Long = timeUnit.toNanos(amount)
}

// Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
val unlimited = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)

val Long.days: Duration
  get() = Duration(this, TimeUnit.DAYS)

val Int.days: Duration
  get() = Duration(this + 0L, TimeUnit.DAYS)

val Long.hours: Duration
  get() = Duration(this, TimeUnit.HOURS)

val Int.hours: Duration
  get() = Duration(this + 0L, TimeUnit.HOURS)

val Long.microseconds: Duration
  get() = Duration(this, TimeUnit.MICROSECONDS)

val Int.microseconds: Duration
  get() = Duration(this + 0L, TimeUnit.MICROSECONDS)

val Long.minutes: Duration
  get() = Duration(this, TimeUnit.MINUTES)

val Int.minutes: Duration
  get() = Duration(this + 0L, TimeUnit.MINUTES)

val Long.milliseconds: Duration
  get() = Duration(this, TimeUnit.MILLISECONDS)

val Int.milliseconds: Duration
  get() = Duration(this + 0L, TimeUnit.MILLISECONDS)

val Long.nanoseconds: Duration
  get() = Duration(this, TimeUnit.NANOSECONDS)

val Int.nanoseconds: Duration
  get() = Duration(this + 0L, TimeUnit.NANOSECONDS)

val Long.seconds: Duration
  get() = Duration(this, TimeUnit.SECONDS)

val Int.seconds: Duration
  get() = Duration(this + 0L, TimeUnit.SECONDS)