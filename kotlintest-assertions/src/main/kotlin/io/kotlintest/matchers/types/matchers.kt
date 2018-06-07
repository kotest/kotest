package io.kotlintest.matchers.types

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.beOfType
import io.kotlintest.matchers.beTheSameInstanceAs
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

inline fun <reified T : Any> Any?.shouldBeInstanceOf() {
  val matcher = beInstanceOf<T>()
  this shouldBe matcher
}

inline fun <reified T : Any> Any?.shouldNotBeInstanceOf() {
  val matcher = beInstanceOf<T>()
  this shouldNotBe matcher
}

inline fun <reified T : Any> Any?.shouldBeTypeOf() {
  val matcher = beOfType<T>()
  this shouldBe matcher
}

inline fun <reified T : Any> Any?.shouldNotBeTypeOf() {
  val matcher = beOfType<T>()
  this shouldNotBe matcher
}

fun Any?.shouldBeSameInstanceAs(ref: Any?) = this should beTheSameInstanceAs(ref)
fun Any?.shouldNotBeSameInstanceAs(ref: Any?) = this shouldNotBe beTheSameInstanceAs(ref)

inline fun <A, reified T : Annotation> Class<A>.shouldHaveAnnotation(klass: Class<T>) = this should haveAnnotation<A, T>(klass)

@Suppress("UNUSED_PARAMETER")
inline fun <A, reified T : Annotation> haveAnnotation(klass: Class<T>) = object : Matcher<Class<A>> {
  val className = T::class.qualifiedName
  override fun test(value: Class<A>): Result {
    val passed = value.annotations.any { it.annotationClass.qualifiedName == className }
    return Result(passed, "Class $value should contain annotation $className", "Class $value should not contain annotation $className")
  }
}
