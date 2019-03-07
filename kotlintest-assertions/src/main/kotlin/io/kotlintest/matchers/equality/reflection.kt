package io.kotlintest.matchers.equality

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

fun <T> T.shouldBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) =
    this should beEqualToUsingFields(other, *properties)

fun <T> beEqualToUsingFields(other: T, vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
  override fun test(value: T): Result {

    val failed = fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      if (actual == expected) null else {
        "${it.name}: $actual != $expected"
      }
    }

    val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

    return Result(
        failed.isEmpty(),
        "$value should be equal to $other using fields $fieldsString; Failed for $failed",
        "$value should not be equal to $other using fields $fieldsString"
    )
  }
}

fun <T : Any> T.shouldBeEqualToIgnoringFields(other: T, vararg properties: KProperty<*>) =
    this should beEqualToIgnoringFields(other, *properties)

fun <T : Any> beEqualToIgnoringFields(other: T,
                                      vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
  override fun test(value: T): Result {

    val fieldNames = fields.map { it.name }
    val failed = value::class.memberProperties.filterNot { fieldNames.contains(it.name) }.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      if (actual == expected) null else {
        "${it.name}: $actual != $expected"
      }
    }

    val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

    return Result(
        failed.isEmpty(),
        "$value should be equal to $other ignoring fields $fieldsString; Failed for $failed",
        "$value should not be equal to $other ignoring fields $fieldsString"
    )
  }
}