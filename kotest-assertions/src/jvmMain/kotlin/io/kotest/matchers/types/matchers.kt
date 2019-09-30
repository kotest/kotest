package io.kotest.matchers.types

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.beOfType
import io.kotest.matchers.beTheSameInstanceAs
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.shouldNotBe

/**
 * Verifies that this is instanceof T
 *
 * Verifies that this value is an instance of T, which include any subclasses, and lets you execute [block] with that
 * value casted.
 *
 * Opposite of [shouldNotBeInstanceOf]
 *
 * For an exact type, use [shouldBeTypeOf]
 *
 * ```
 *
 * val list: List<Int> = arraylistOf(1, 2, 3)
 *
 * list.shouldBeInstanceOf<ArrayList<Int>> { it: ArrayList<Int> // Typecasted for you if not explicit
 *  // Use it.
 * }
 *
 * ```
 */
inline fun <reified T : Any> Any?.shouldBeInstanceOf(block: (T) -> Unit = { }) {
  val matcher = beInstanceOf<T>()
  this shouldBe matcher
  block(this as T)
}

/**
 * Verifies that this is NOT Instanceof T
 *
 * Verifies that this value is not an instance of T.
 *
 * Opposite of [shouldBeInstanceOf]
 *
 * For an exact type, use [shouldNotBeTypeOf]
 *
 * ```
 * val list: List<Int> = arrayListOf(1, 2, 3)
 *
 * list.shouldNotBeInstanceOf<LinkedList<Int>>
 * ```
 */
inline fun <reified T : Any> Any?.shouldNotBeInstanceOf() {
  val matcher = beInstanceOf<T>()
  this shouldNotBe matcher
}

/**
 * Verifies that this is exactly of type T
 *
 * Verifies that this value is exactly of type T, where no inheritance is verified. If the assertion passes, you may
 * use [this] as T inside [block].
 *
 * Opposite of [shouldNotBeTypeOf]
 *
 * If you want to verify including inheritance, use [shouldBeInstanceOf]
 *
 * ```
 * val list: List<Int> = arrayListOf(1, 2, 3)
 *
 * list.shouldBeTypeOf<ArrayList<Int>> { it: ArrayList<Int> // Typecasted for you if not explicit
 * // Use it
 * }
 * ```
 */
inline fun <reified T : Any> Any?.shouldBeTypeOf(block: (T) -> Unit = { }) {
  val matcher = beOfType<T>()
  this shouldBe matcher
  block(this as T)
}

/**
 * Verifies that this is NOT exactly of type T
 *
 * Verifies that this value is not of type T.
 *
 * Opposite of [shouldBeTypeOf]
 *
 * If you want to consider inheritance, use [shouldNotBeInstanceOf]
 *
 * ```
 * val list: List<Int> = arrayListOf(1, 2, 3)
 *
 * list.shouldNotBeTypeOf<LinkedList<Int>>
 * ```
 */
inline fun <reified T : Any> Any?.shouldNotBeTypeOf() {
  val matcher = beOfType<T>()
  this shouldNotBe matcher
}

infix fun Any?.shouldBeSameInstanceAs(ref: Any?) = this should beTheSameInstanceAs(ref)
infix fun Any?.shouldNotBeSameInstanceAs(ref: Any?) = this shouldNotBe beTheSameInstanceAs(ref)

inline fun <A, reified T : Annotation> Class<A>.shouldHaveAnnotation(klass: Class<T>) = this should haveAnnotation(klass)

@Suppress("UNUSED_PARAMETER")
inline fun <A, reified T : Annotation> haveAnnotation(klass: Class<T>) = object : Matcher<Class<A>> {
  val className = T::class.qualifiedName
  override fun test(value: Class<A>): MatcherResult {
    val passed = value.annotations.any { it.annotationClass.qualifiedName == className }
    return MatcherResult(passed, "Class $value should contain annotation $className", "Class $value should not contain annotation $className")
  }
}
