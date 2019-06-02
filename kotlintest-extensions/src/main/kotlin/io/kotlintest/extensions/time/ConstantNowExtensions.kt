package io.kotlintest.extensions.time

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import java.time.*
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
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

@PublishedApi
internal fun <Time : Temporal> mockNow(value: Time, klass: KClass<Time>) {
  mockkStatic(klass)
  every { getNoParameterNowFunction(klass).call() } returns value

  val slot = slot<ZoneId>()
  val zoneIdNowFunction = getZoneIdNowFunction(klass)
  if (zoneIdNowFunction != null) {
    every { zoneIdNowFunction.call(capture(slot)) } answers {
      val zoneId = slot.captured
      when (klass.java) {
        ZonedDateTime::class.java ->
          ZonedDateTime.ofInstant((value as ZonedDateTime).toInstant(), zoneId)
        OffsetDateTime::class.java ->
          OffsetDateTime.ofInstant((value as OffsetDateTime).toInstant(), zoneId)
        LocalDateTime::class.java -> {
          val offset = Clock.systemDefaultZone().zone.rules.getOffset(value as LocalDateTime)
          LocalDateTime.ofInstant( (value as LocalDateTime).toInstant(offset), zoneId)
        }
        else -> value
      }
    }
  }
}

@PublishedApi
internal fun <Time : Temporal> getNoParameterNowFunction(klass: KClass<Time>): KFunction<*> {
  return klass.staticFunctions.filter { it.name == "now" }.first { it.parameters.isEmpty() }
}

@PublishedApi
internal fun <Time : Temporal> getZoneIdNowFunction(klass: KClass<Time>): KFunction<*>? {
  val functions =  klass.staticFunctions.filter { it.name == "now" && it.parameters.size == 1 && it.parameters[0].type.javaType == ZoneId::class.java}
  return if (functions.isNotEmpty()) {
    functions[0]
  } else {
    null
  }
}

@PublishedApi
internal fun <Time : Temporal> unmockNow(klass: KClass<Time>) {
  unmockkStatic(klass)
}

abstract class ConstantNowListener<Time : Temporal>(private val now: Time) : TestListener {
  
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
class ConstantNowTestListener<Time : Temporal>(now: Time) : ConstantNowListener<Time>(now) {
  
  override fun beforeTest(testCase: TestCase) {
    changeNow()
  }
  
  override fun afterTest(testCase: TestCase, result: TestResult) {
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
class ConstantNowProjectListener<Time : Temporal>(now: Time) : ConstantNowListener<Time>(now) {
  
  override fun beforeProject() {
    changeNow()
  }
  
  override fun afterProject() {
    resetNow()
  }
}