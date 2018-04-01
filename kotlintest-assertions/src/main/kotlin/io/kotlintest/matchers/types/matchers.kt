package io.kotlintest.matchers.types

import io.kotlintest.Matcher
import io.kotlintest.Result

inline fun <A, reified T : Annotation> haveAnnotation(klass: Class<T>) = object : Matcher<Class<A>> {
  val className = T::class.qualifiedName
  override fun test(value: Class<A>): Result {
    val passed = value.annotations.any { it.javaClass.canonicalName == className }
    return Result(passed, "Class $value should contain annotation $className", "Class $value should not contain annotation $className")
  }
}