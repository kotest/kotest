package io.kotest.matchers.date

import kotlin.time.Duration

infix fun Duration.and(other: Duration): Duration = this + other
