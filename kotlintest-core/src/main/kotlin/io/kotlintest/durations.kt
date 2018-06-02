package io.kotlintest

import java.time.Duration

// Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
val Duration.unlimited: Duration
  get() = Long.MAX_VALUE.days

val Long.days: Duration
  get() = Duration.ofDays(this)

val Int.days: Duration
  get() = Duration.ofDays(this.toLong())

val Long.hours: Duration
  get() = Duration.ofHours(this)

val Int.hours: Duration
  get() = Duration.ofHours(this.toLong())

val Long.minutes: Duration
  get() = Duration.ofMinutes(this)

val Int.minutes: Duration
  get() = Duration.ofMinutes(this + 0L)

val Long.milliseconds: Duration
  get() = Duration.ofMillis(this)

val Int.milliseconds: Duration
  get() = Duration.ofMillis(this + 0L)

val Long.nanoseconds: Duration
  get() = Duration.ofNanos(this)

val Int.nanoseconds: Duration
  get() = Duration.ofNanos(this + 0L)

val Long.seconds: Duration
  get() = Duration.ofSeconds(this)

val Int.seconds: Duration
  get() = Duration.ofSeconds(this + 0L)