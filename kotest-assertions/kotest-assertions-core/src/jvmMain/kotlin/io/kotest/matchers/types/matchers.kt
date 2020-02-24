@file:JvmName("jvmTypeMatchersKt")

package io.kotest.matchers.types
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

inline fun <A, reified T : Annotation> Class<A>.shouldHaveAnnotation(klass: Class<T>) = this should haveAnnotation(klass)

@Suppress("UNUSED_PARAMETER")
inline fun <A, reified T : Annotation> haveAnnotation(klass: Class<T>) = object : Matcher<Class<A>> {
  val className = T::class.qualifiedName
  override fun test(value: Class<A>): MatcherResult {
    val passed = value.annotations.any { it.annotationClass.qualifiedName == className }
    return MatcherResult(passed, "Class $value should contain annotation $className", "Class $value should not contain annotation $className")
  }
}
