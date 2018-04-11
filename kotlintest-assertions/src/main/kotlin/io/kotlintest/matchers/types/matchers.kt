package io.kotlintest.matchers.types

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should

inline fun <reified T : Annotation> Class<Any>.shouldHaveAnnotation(klass: Class<T>) = this should haveAnnotation(klass)

@Suppress("UNUSED_PARAMETER")
inline fun <A, reified T : Annotation> haveAnnotation(klass: Class<T>) = object : Matcher<Class<A>> {
  val className = T::class.qualifiedName
  override fun test(value: Class<A>): Result {
    val passed = value.annotations.any { it.annotationClass.qualifiedName == className }
    return Result(passed, "Class $value should contain annotation $className", "Class $value should not contain annotation $className")
  }
}