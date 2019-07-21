package io.kotest.extensions.time

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import java.time.*
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.jvm.javaType

/**
 * Simulate the value of now() while executing [block]
 *
 * This function will simulate the value returned by the static method `now` as a helper to code that is sensitive
 * to `now`. After the execution of [block], the default behavior will be returned.
 *
 * Supported classes are all classes in the `java.time` package that supports `now`, such as `LocalDate.now()` and
 * `LocalDateTime.now()`. Less used classes such as `JapaneseDate.now()` or `Year.now()` are also supported.
 *
 * To simulate the value, this function uses [Mockk](https://mockk.io) to modify the value returned from the static
 * now function
 *
 * **ATTENTION**: This code is very sensitive to race conditions. as the static method is global to the whole JVM instance,
 * if you're mocking `now` while running in parallel, the results may be inconsistent.
 */
inline fun <T, reified Time : Temporal> withConstantNow(now: Time, block: () -> T): T {
   mockNow(now, Time::class)
   try {
      return block()
   } finally {
      unmockNow(Time::class)
   }
}

inline fun <T> newWithConstantNow(now: ZonedDateTime, block: () -> T): T {
  mockNowForTimeClasses(now)
  try {
    return block()
  } finally {
    unmockNowForTimeClasses()
  }
}

@PublishedApi
internal fun <Time : Temporal> mockNow(value: Time, klass: KClass<Time>) {
   mockkStatic(klass)
   every { getNoParameterNowFunction(klass).call() } returns value
}

@PublishedApi
internal fun mockNowForTimeClasses(value: ZonedDateTime) {
  ClassesExtendTemporal.forEach { (klass, toSpecificTimeClass) ->
    mockkStatic(klass)
    every { getNoParameterNowFunction(klass).call() } returns toSpecificTimeClass(value)

    mockNowFunctionWithParameterZoneId(klass, toSpecificTimeClass, value)
  }
}

@PublishedApi
internal fun mockNowFunctionWithParameterZoneId(klass: KClass<out Any>, toSpecificTimeClass: KFunction1<ZonedDateTime, Any>, value: ZonedDateTime) {
  val zoneIdSlot = slot<ZoneId>()
  val nowFunctionWithParameterZonedId = getNowFunctionWithParameterZoneId(klass)
  if (nowFunctionWithParameterZonedId != null) {
    every { nowFunctionWithParameterZonedId.call(capture(zoneIdSlot)) } answers {
      toSpecificTimeClass(value.withZoneSameInstant(zoneIdSlot.captured))
    }
  }
}

private val ClassesExtendTemporal =  mapOf(
  Instant::class to ZonedDateTime::toInstant,
  LocalDate::class to ZonedDateTime::toLocalDate,
  LocalDateTime::class to ZonedDateTime::toLocalDateTime,
  LocalTime::class to ZonedDateTime::toLocalTime,
  OffsetDateTime::class to ZonedDateTime::toOffsetDateTime,
  OffsetTime::class to ZonedDateTime::toOffsetTime,
  Year::class to ZonedDateTime::getYear,
  YearMonth::class to ZonedDateTime::toYearMonth,
  ZonedDateTime::class to ZonedDateTime::self,
  HijrahDate::class to ZonedDateTime::toHijrahDate,
  JapaneseDate::class to ZonedDateTime::toJapaneseDate,
  MinguoDate::class to ZonedDateTime::toMinguoDate,
  ThaiBuddhistDate::class to ZonedDateTime::toThaiBuddhistDate
)

private fun ZonedDateTime.toThaiBuddhistDate() = ThaiBuddhistDate.of(year, monthValue, dayOfMonth)

private fun ZonedDateTime.toMinguoDate() = MinguoDate.of(year, monthValue, dayOfMonth)

private fun ZonedDateTime.toJapaneseDate() = JapaneseDate.of(year, monthValue, dayOfMonth)

private fun ZonedDateTime.toHijrahDate() = HijrahDate.of(year, monthValue, dayOfMonth)

private fun ZonedDateTime.self() = this

private fun ZonedDateTime.toYearMonth() = YearMonth.of(year, month)

private fun ZonedDateTime.toOffsetTime() = toOffsetDateTime().toOffsetTime()

@PublishedApi
internal fun <Time : Temporal> getNoParameterNowFunction(klass: KClass<in Time>): KFunction<*> {
   return klass.staticFunctions.filter { it.name == "now" }.first { it.parameters.isEmpty() }
}
@PublishedApi
internal fun <Time : Temporal> getNowFunctionWithParameterZoneId(klass: KClass<in Time>): KFunction<*>? {
  return klass.staticFunctions.firstOrNull {
    it.name == "now" && it.parameters.size == 1 && it.parameters[0].type.javaType == ZoneId::class.java }
}

@PublishedApi
internal fun <Time : Temporal> unmockNow(klass: KClass<Time>) {
   unmockkStatic(klass)
}

@PublishedApi
internal fun unmockNowForTimeClasses() {
  ClassesExtendTemporal.keys.forEach { klass ->
    unmockkStatic(klass)
  }
}

abstract class ConstantNowListener<Time : Temporal>(private val now: Time) {

   @Suppress("UNCHECKED_CAST")
   private val nowKlass = now::class as KClass<Time>

   protected fun changeNow() {
      mockNow(now, nowKlass)
   }

   protected fun resetNow() {
      unmockNow(nowKlass)
   }
}


/**
 * Simulate the value of now() while executing a test
 *
 * This listener will simulate the value returned by the static method `now` as a helper to code that is sensitive
 * to `now`. After the execution of the test, the default behavior will be returned.
 *
 * Supported classes are all classes in the `java.time` package that supports `now`, such as `LocalDate.now()` and
 * `LocalDateTime.now()`. Less used classes such as `JapaneseDate.now()` or `Year.now()` are also supported.
 *
 * To simulate the value, this listener uses [Mockk](https://mockk.io) to modify the value returned from the static
 * now function
 *
 * **ATTENTION**: This code is very sensitive to race conditions. as the static method is global to the whole JVM instance,
 * if you're mocking `now` while running in parallel, the results may be inconsistent.
 */
class ConstantNowTestListener<Time : Temporal>(now: Time) :
   ConstantNowListener<Time>(now), TestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      changeNow()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      resetNow()
   }
}

/**
 * Simulate the value of now() while executing the project
 *
 * This listener will simulate the value returned by the static method `now` as a helper to code that is sensitive
 * to `now`. After the execution of the project, the default behavior will be returned.
 *
 * Supported classes are all classes in the `java.time` package that supports `now`, such as `LocalDate.now()` and
 * `LocalDateTime.now()`. Less used classes such as `JapaneseDate.now()` or `Year.now()` are also supported.
 *
 * To simulate the value, this listener uses [Mockk](https://mockk.io) to modify the value returned from the static
 * now function
 *
 * **ATTENTION**: This code is very sensitive to race conditions. as the static method is global to the whole JVM instance,
 * if you're mocking `now` while running in parallel, the results may be inconsistent.
 */
class ConstantNowProjectListener<Time : Temporal>(now: Time) :
   ConstantNowListener<Time>(now), ProjectListener {

   override fun beforeProject() {
      changeNow()
   }

   override fun afterProject() {
      resetNow()
   }
}
