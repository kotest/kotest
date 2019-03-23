package io.kotlintest

class Duration {
  companion object {
    fun ofDays(seconds: Long): Duration = Duration()
    fun ofHours(seconds: Long): Duration = Duration()
    fun ofSeconds(seconds: Long): Duration = Duration()
    fun ofMinutes(minutes: Long): Duration = Duration()
    fun ofMillis(millis: Long): Duration = Duration()
    fun ofNanos(millis: Long): Duration = Duration()
  }
}