package io.kotest.matchers.collections

/**
 * Verifies this collection contains only one element
 *
 * This assertion is an alias to `collection shouldHaveSize 1`. This will pass if the collection have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1).shouldBeSingleton()    // Assertion passes
 * listOf(1, 2).shouldBeSingleton() // Assertion fails
 * ```
 *
 * @see [shouldHaveSize]
 * @see [shouldNotBeSingleton]
 * @see [shouldHaveSingleElement]
 */
fun <T, C : Collection<T>> C.shouldBeSingleton(): C {
   this shouldHaveSize 1
   return this
}

fun <T, I : Iterable<T>> I.shouldBeSingleton(): I {
   toList().shouldBeSingleton()
   return this
}

fun <T> Array<T>.shouldBeSingleton(): Array<T> {
   asList().shouldBeSingleton()
   return this
}


/**
 * Verifies this collection doesn't contain only one element
 *
 * This assertion is an alias to `collection shouldNotHaveSize 1`. This will pass if the collection doesn't have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1, 2).shouldNotBeSingleton()    // Assertion passes
 * listOf<Int>().shouldNotBeSingleton()   // Assertion passes
 * listOf(1).shouldNotBeSingleton()       // Assertion fails
 * ```
 *
 * @see [shouldNotHaveSize]
 * @see [shouldBeSingleton]
 * @see [shouldNotHaveSingleElement]
 */
fun <T, C : Collection<T>> C.shouldNotBeSingleton(): C {
   this shouldNotHaveSize 1
   return this
}

fun <T, I : Iterable<T>> I.shouldNotBeSingleton(): I {
   toList().shouldNotBeSingleton()
   return this

}

fun <T> Array<T>.shouldNotBeSingleton(): Array<T> {
   asList().shouldNotBeSingleton()
   return this
}


/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
inline fun <T, C : Collection<T>> C.shouldBeSingleton(fn: (T) -> Unit): C {
   this.shouldBeSingleton()
   fn(this.first())
   return this
}

/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
inline fun <T, I : Iterable<T>> I.shouldBeSingleton(fn: (T) -> Unit): I {
   toList().shouldBeSingleton(fn)
   return this
}

/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
inline fun <T> Array<T>.shouldBeSingleton(fn: (T) -> Unit): Array<T> {
   asList().shouldBeSingleton(fn)
   return this
}



