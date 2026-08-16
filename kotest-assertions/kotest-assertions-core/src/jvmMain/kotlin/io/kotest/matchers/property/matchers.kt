package io.kotest.matchers.property

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

fun beNullable() = Matcher<KProperty<*>> {
   MatcherResult(
      it.returnType.isMarkedNullable,
      { "Property $it return type should be nullable" },
      { "Property $it return type should not be nullable" },
   )
}

fun beNotNullable() = beNullable().invert()

fun beMutable() = Matcher<KProperty<*>> {
   MatcherResult(
      it is KMutableProperty<*>,
      { "Property $it return type should be mutable" },
      { "Property $it return type should be immutable" },
   )
}

fun beImmutable() = beMutable().invert()

fun beNamed(expectedName: String) = Matcher<KProperty<*>> {
   MatcherResult(
      it.name == expectedName,
      { "Property $it should be named $expectedName" },
      { "Property $it should not be named $expectedName" },
   )
}

@IgnorableReturnValue
fun KProperty<*>.shouldBeNullable() = this should beNullable()
@IgnorableReturnValue
fun KProperty<*>.shouldBeNotNullable() = this should beNullable().invert()
@IgnorableReturnValue
fun KProperty<*>.shouldBeMutable() = this should beMutable()
@IgnorableReturnValue
fun KProperty<*>.shouldBeImmutable() = this should beImmutable()
@IgnorableReturnValue
infix fun KProperty<*>.shouldBeNamed(expectedName: String) = this should beNamed(expectedName)
