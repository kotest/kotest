package io.kotlintest

class Duration(val nanos: Long) {
  companion object {
    fun ofDays(days: Long): Duration = ofHours(days * 24)
    fun ofHours(hours: Long): Duration = ofMinutes(hours * 60)
    fun ofMinutes(minutes: Long): Duration = ofSeconds(minutes * 60)
    fun ofSeconds(seconds: Long): Duration = ofMillis(seconds * 1000)
    fun ofMillis(millis: Long): Duration = ofMicros(millis * 1000)
    fun ofMicros(micros: Long): Duration = ofNanos(micros * 1000)
    fun ofNanos(nanos: Long): Duration = Duration(nanos)
  }
}