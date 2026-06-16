@file:JvmName("jvmTypeMatchersKt")

package io.kotest.matchers.types

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

inline fun <A, reified T : Annotation> Class<A>.shouldHaveAnnotation() =
   this.shouldHaveAnnotation(T::class.java)

infix fun <A, T> Class<A>.shouldHaveAnnotation(annotationClass: Class<T>) =
   this should haveAnnotation(annotationClass)

fun <A, T> haveAnnotation(annotationClass: Class<T>) = object : Matcher<Class<A>> {
   override fun test(value: Class<A>): MatcherResult {
      val passed = value.annotations.any { it.annotationClass.java == annotationClass }
      return MatcherResult(
         passed,
         { "Class $value should contain annotation ${annotationClass.name}" },
         { "Class $value should not contain annotation ${annotationClass.name}" })
   }
}
