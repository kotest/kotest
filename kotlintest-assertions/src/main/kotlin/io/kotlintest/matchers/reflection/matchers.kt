package io.kotlintest.matchers.reflection

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import kotlin.reflect.*
import kotlin.reflect.full.*

fun KClass<*>.shouldHaveAnnotations() = this should haveClassAnnontations()
fun KClass<*>.shouldNotHaveAnnotations() = this shouldNot haveClassAnnontations()
infix fun KClass<*>.shouldHaveAnnotations(count: Int) = this should haveClassAnnontations(count)
infix fun KClass<*>.shouldNotHaveAnnotations(count: Int) = this shouldNot haveClassAnnontations(count)
fun haveClassAnnontations(count: Int = -1) = object : Matcher<KClass<*>> {
  override fun test(value: KClass<*>) = if (count < 0) {
    Result(
        value.annotations.size > 0,
        "Class $value should have annotations",
        "Class $value should not have annotations"
    )
  } else {
    Result(
        value.annotations.size == count,
        "Class $value should have $count annotations",
        "Class $value should not have $count annotations"
    )
  }
}

inline fun <reified T : Annotation> KClass<*>.shouldBeAnnotatedWith(block: (T) -> Unit = {}) {
  this should beClassAnnotatedWith<T>()
  findAnnotation<T>()?.let(block)
}

inline fun <reified T : Annotation> KClass<*>.shouldNotBeAnnotatedWith() = this shouldNot beClassAnnotatedWith<T>()
inline fun <reified T : Annotation> beClassAnnotatedWith() = object : Matcher<KClass<*>> {
  override fun test(value: KClass<*>) = Result(
      value.findAnnotation<T>() != null,
      "Class $value should have annotation ${T::class}",
      "Class $value should not have annotation ${T::class}"
  )
}

fun KClass<*>.shouldHaveFunction(name: String, block: (KFunction<*>) -> Unit = {}) {
  this should haveFunction(name)
  findFunction(name)?.let(block)
}

infix fun KClass<*>.shouldHaveFunction(name: String) = this should haveFunction(name)
infix fun KClass<*>.shouldNotHaveFunction(name: String) = this shouldNot haveFunction(name)
fun haveFunction(name: String) = object : Matcher<KClass<*>> {
  override fun test(value: KClass<*>) = Result(
      value.findFunction(name) != null,
      "Class $value should have function $name",
      "Class $value should not have function $name"
  )
}

fun KClass<*>.shouldHaveMemberProperty(name: String, block: (KProperty<*>) -> Unit) {
  this should haveMemberProperty(name)
  findMemberProperty(name)?.let(block)
}
infix fun KClass<*>.shouldHaveMemberProperty(name: String) = this should haveMemberProperty(name)
infix fun KClass<*>.shouldNotHaveMemberProperty(name: String) = this shouldNot haveMemberProperty(name)
fun haveMemberProperty(name: String) = object : Matcher<KClass<*>> {
  override fun test(value: KClass<*>) = Result(
      value.findMemberProperty(name) != null,
      "Class $value should have a member property $name",
      "Class $value should not have a member property $name"
  )
}

fun KFunction<*>.shouldHaveAnnotations() = this should haveFunctionAnnotations()
fun KFunction<*>.shouldNotHaveAnnotations() = this shouldNot haveFunctionAnnotations()
infix fun KFunction<*>.shouldHaveAnnotations(count: Int) = this should haveFunctionAnnotations(count)
infix fun KFunction<*>.shouldNotHaveAnnotations(count: Int) = this shouldNot haveFunctionAnnotations(count)
fun haveFunctionAnnotations(count: Int = -1) = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = if (count < 0) {
    Result(
        value.annotations.size > 0,
        "Function $value should have annotations",
        "Function $value should not have annotations"
    )
  } else {
    Result(
        value.annotations.size == count,
        "Function $value should have $count annotations",
        "Function $value should not have $count annotations"
    )
  }
}

inline fun <reified T : Annotation> KFunction<*>.shouldBeAnnotatedWith(block: (T) -> Unit = {}) {
  this should beAnnotatedWith<T>()
  findAnnotation<T>()?.let(block)
}

inline fun <reified T : Annotation> KFunction<*>.shouldNotBeAnnotatedWith() = this shouldNot beAnnotatedWith<T>()
inline fun <reified T : Annotation> beAnnotatedWith() = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = Result(
      value.findAnnotation<T>() != null,
      "Function $value should have annotation ${T::class}",
      "Function $value should not have annotation ${T::class}"
  )
}

inline fun <reified T> KFunction<*>.shouldHaveReturnType() = this.returnType.shouldBeOfType<T>()
inline fun <reified T> KFunction<*>.shouldNotHaveReturnType() = this.returnType.shouldNotBeOfType<T>()

inline fun <reified T> KProperty<*>.shouldBeOfType() = this.returnType.shouldBeOfType<T>()
inline fun <reified T> KProperty<*>.shouldNotBeOfType() = this.returnType.shouldNotBeOfType<T>()

inline fun <reified T> KType.shouldBeOfType() = this should beOfType<T>()
inline fun <reified T> KType.shouldNotBeOfType() = this shouldNot beOfType<T>()
inline fun <reified T> beOfType() = object : Matcher<KType> {
  override fun test(value: KType) = Result(
      value.isSubtypeOf(T::class.starProjectedType),
      "Type $value should be ${T::class}",
      "Type $value should not be ${T::class}"
  )
}

infix fun KCallable<*>.shouldHaveVisibility(visibility: KVisibility) = this should haveVisibility(visibility)
infix fun KCallable<*>.shouldNotHaveVisibility(visibility: KVisibility) = this shouldNot haveVisibility(visibility)
fun haveVisibility(expected: KVisibility) = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.visibility == expected,
      "Member $value should have visibility ${expected.humanName()}",
      "Member $value should not have visibility ${expected.humanName()}"
  )
}

fun KCallable<*>.shouldBeFinal() = this should beFinal()
fun KCallable<*>.shouldNotBeFinal() = this shouldNot beFinal()
fun beFinal() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isFinal,
      "Member $value should be final",
      "Member $value should not be final"
  )
}

// EXTENSION FUNCTIONS
private fun KClass<*>.findFunction(name: String) = declaredFunctions.firstOrNull { it.name == name }
private fun KClass<*>.findMemberProperty(name: String) = memberProperties.firstOrNull { it.name == name }
private fun KVisibility.humanName() = name.toLowerCase().capitalize()