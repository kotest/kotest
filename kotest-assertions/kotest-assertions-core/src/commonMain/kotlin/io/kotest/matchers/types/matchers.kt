package io.kotest.matchers.types


import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.contracts.contract
import kotlin.reflect.KClass

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
 * ~~~
 * val list: List<Int> = arraylistOf(1, 2, 3)
 *
 * list.shouldBeInstanceOf<ArrayList<Int>> { it: ArrayList<Int> // Typecasted for you if not explicit
 *  // Use it.
 * }
 * ~~~
 *
 * ~~~
 * val list: List<Int> = arraylistOf(1, 2, 3)
 *
 * // arrayList is typecasted to ArrayList<Int>
 * val arrayList = list.shouldBeInstanceOf<ArrayList<Int>>()
 * ~~~
 * @param block Lambda that receives typecasted instance as argument for further assertions.
 * @return The typecasted instance
 */
inline fun <reified T : Any> Any?.shouldBeInstanceOf(block: (T) -> Unit = { }): T {
   val matcher = beInstanceOf<T>()
   this shouldBe matcher
   block(this as T)
   return this
}


/**
 * Verifies that this is instanceof T
 *
 * Verifies that this value is an instance of T, which include any subclasses and smart casts to T.
 *
 * Opposite of [shouldNotBeInstanceOf]
 *
 * For an exact type, use [shouldBeTypeOf]
 *
 * ```
 * val list: List<Int> = arraylistOf(1, 2, 3)
 *
 * // list will be smart casted to ArrayList
 * list.shouldBeInstanceOf<ArrayList<Int>>()
 * ```
 *
 * ```
 * val list: List<Int> = arraylistOf(1, 2, 3)
 *
 * // arrayList is typecasted to ArrayList<Int>
 * val arrayList = list.shouldBeInstanceOf<ArrayList<Int>>()
 * ```
 *
 * @return The typecasted instance
 */
inline fun <reified T : Any> Any?.shouldBeInstanceOf(): T {
   contract {
      returns() implies (this@shouldBeInstanceOf is T)
   }
   val matcher = beInstanceOf<T>()
   this shouldBe matcher
   return this as T
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
 * Verifies that this is instanceof [expected]
 *
 * Verifies that this value is an instance of [expected] class, which includes any subclasses.
 *
 * Opposite of [shouldNotBeInstanceOf]
 *
 * For an exact type, use [shouldBeTypeOf]
 *
 * @param expected The expected class.
 * @return The typecasted instance
 */
infix fun <T : Any> Any?.shouldBeInstanceOf(expected: KClass<T>): T {
   val matcher = beInstanceOf(expected)
   this shouldBe matcher
   @Suppress("UNCHECKED_CAST")
   return this as T
}

/**
 * Verifies that this is instanceof [expected]
 *
 * Verifies that this value is an instance of [expected] class, which includes any subclasses, and lets you execute [block] with that
 * value casted.
 *
 * Opposite of [shouldNotBeInstanceOf]
 *
 * For an exact type, use [shouldBeTypeOf]
 *
 * @param expected The expected class.
 * @param block Lambda that receives typecasted instance as argument for further assertions.
 * @return The typecasted instance
 */
fun <T : Any> Any?.shouldBeInstanceOf(expected: KClass<T>, block: (T) -> Unit): T {
   val matcher = beInstanceOf(expected)
   this shouldBe matcher
   @Suppress("UNCHECKED_CAST")
   val casted = this as T
   block(casted)
   return casted
}

/**
 * Verifies that this is NOT Instanceof [expected]
 *
 * Verifies that this value is not an instance of [expected] class.
 *
 * Opposite of [shouldBeInstanceOf]
 *
 * For an exact type, use [shouldNotBeTypeOf]
 */
infix fun Any?.shouldNotBeInstanceOf(expected: KClass<*>) {
   val matcher = beInstanceOf(expected)
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
 * ```
 * val list: List<Int> = arrayListOf(1, 2, 3)
 *
 * // arrayList is typecasted to ArrayList<Int>()
 * val arrayList = list.shouldBeTypeOf<ArrayList<Int>>()
 * ```
 *
 * @param block Lambda that receives typecasted instance  as argument for further assertions.
 * @return The typecasted instance
 */
inline fun <reified T : Any> Any?.shouldBeTypeOf(block: (T) -> Unit = { }): T {
   val matcher = beOfType<T>()
   this shouldBe matcher
   block(this as T)
   return this
}

inline fun <reified T : Any> Any?.shouldBeTypeOf(): T {
   contract {
      returns() implies (this@shouldBeTypeOf is T)
   }
   val matcher = beOfType<T>()
   this shouldBe matcher
   return this as T
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

/**
 * Verifies that this is exactly of type [expected]
 *
 * Verifies that this value is exactly of type [expected], where no inheritance is verified.
 *
 * Opposite of [shouldNotBeTypeOf]
 *
 * If you want to verify including inheritance, use [shouldBeInstanceOf]
 *
 * @param expected The expected exact class.
 * @return The typecasted instance
 */
infix fun <T : Any> Any?.shouldBeTypeOf(expected: KClass<T>): T {
   val matcher = beOfType(expected)
   this shouldBe matcher
   @Suppress("UNCHECKED_CAST")
   return this as T
}

/**
 * Verifies that this is exactly of type [expected]
 *
 * Verifies that this value is exactly of type [expected], where no inheritance is verified. If the assertion passes, you may
 * use [this] as [T] inside [block].
 *
 * Opposite of [shouldNotBeTypeOf]
 *
 * If you want to verify including inheritance, use [shouldBeInstanceOf]
 *
 * @param expected The expected exact class.
 * @param block Lambda that receives typecasted instance as argument for further assertions.
 * @return The typecasted instance
 */
fun <T : Any> Any?.shouldBeTypeOf(expected: KClass<T>, block: (T) -> Unit): T {
   val matcher = beOfType(expected)
   this shouldBe matcher
   @Suppress("UNCHECKED_CAST")
   val casted = this as T
   block(casted)
   return casted
}

/**
 * Verifies that this is NOT exactly of type [expected]
 *
 * Verifies that this value is not of type [expected].
 *
 * Opposite of [shouldBeTypeOf]
 *
 * If you want to consider inheritance, use [shouldNotBeInstanceOf]
 */
infix fun Any?.shouldNotBeTypeOf(expected: KClass<*>) {
   val matcher = beOfType(expected)
   this shouldNotBe matcher
}

infix fun Any?.shouldBeSameInstanceAs(ref: Any?) = this should beTheSameInstanceAs(ref)
infix fun Any?.shouldNotBeSameInstanceAs(ref: Any?) = this shouldNotBe beTheSameInstanceAs(ref)
