package io.kotlintest

import java.util.concurrent.TimeUnit

data class Duration(val amount: Long, val timeUnit: TimeUnit) {

    companion object {
        // Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
        val unlimited = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)
    }

    val Long.days: Duration
        get() = Duration(this, TimeUnit.DAYS)

    val Long.hours: Duration
        get() = Duration(this, TimeUnit.HOURS)

    val Long.microseconds: Duration
        get() = Duration(this, TimeUnit.MICROSECONDS)

    val Long.milliseconds: Duration
        get() = Duration(this, TimeUnit.MILLISECONDS)

    val Long.nanoseconds: Duration
        get() = Duration(this, TimeUnit.NANOSECONDS)

    val Long.seconds: Duration
        get() = Duration(this, TimeUnit.SECONDS)

    val nanoseconds: Long = timeUnit.toNanos(amount)
}